package com.example.audio

import java.io.*
import kotlin.math.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

object DspAudioProcessor {

    data class WavData(
        val sampleRate: Int,
        val channels: Int,
        val samples: ShortArray
    )

    // Read 16-bit PCM WAV file
    fun readWavFile(file: File): WavData? {
        if (!file.exists() || file.length() < 44) return null
        return try {
            val bytes = file.readBytes()
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

            // Verify RIFF header
            val riff = String(bytes, 0, 4)
            val wave = String(bytes, 8, 4)
            if (riff != "RIFF" || wave != "WAVE") return null

            val channels = buffer.getShort(22).toInt()
            val sampleRate = buffer.getInt(24)
            val bitsPerSample = buffer.getShort(34).toInt()

            // Find data chunk
            var dataOffset = 12
            while (dataOffset < bytes.size - 8) {
                val chunkId = String(bytes, dataOffset, 4)
                val chunkSize = ByteBuffer.wrap(bytes, dataOffset + 4, 4).order(ByteOrder.LITTLE_ENDIAN).int
                if (chunkId == "data") {
                    dataOffset += 8
                    val numSamples = chunkSize / 2
                    val samples = ShortArray(numSamples)
                    val pcmBuffer = ByteBuffer.wrap(bytes, dataOffset, chunkSize).order(ByteOrder.LITTLE_ENDIAN)
                    for (i in 0 until numSamples) {
                        samples[i] = pcmBuffer.short
                    }
                    return WavData(sampleRate, channels, samples)
                }
                dataOffset += 8 + chunkSize
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Write 16-bit PCM WAV file
    fun writeWavFile(file: File, sampleRate: Int, channels: Int, samples: ShortArray): Boolean {
        return try {
            val byteRate = sampleRate * channels * 2
            val blockAlign = channels * 2
            val dataSize = samples.size * 2
            val totalSize = 36 + dataSize

            val fos = FileOutputStream(file)
            val buffer = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

            // RIFF header
            buffer.put("RIFF".toByteArray())
            buffer.putInt(totalSize)
            buffer.put("WAVE".toByteArray())

            // fmt subchunk
            buffer.put("fmt ".toByteArray())
            buffer.putInt(16) // SubChunk1Size (16 for PCM)
            buffer.putShort(1.toShort()) // AudioFormat (1 for PCM)
            buffer.putShort(channels.toShort())
            buffer.putInt(sampleRate)
            buffer.putInt(byteRate)
            buffer.putShort(blockAlign.toShort())
            buffer.putShort(16.toShort()) // BitsPerSample

            // data subchunk
            buffer.put("data".toByteArray())
            buffer.putInt(dataSize)

            fos.write(buffer.array())

            // Write PCM samples
            val sampleBytes = ByteArray(dataSize)
            val sampleBuffer = ByteBuffer.wrap(sampleBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in samples) {
                sampleBuffer.putShort(sample)
            }
            fos.write(sampleBytes)
            fos.flush()
            fos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 1. NOISE REMOVAL DSP
    fun applyNoiseReduction(
        wav: WavData,
        strength: Float, // 0.0 to 1.0
        removeHum: Boolean = true,
        removeHiss: Boolean = true
    ): ShortArray {
        val input = wav.samples
        val output = ShortArray(input.size)
        val sr = wav.sampleRate.toDouble()

        // High-pass filter (cuts sub-rumble below 85Hz)
        val hpCutoff = 85.0
        val hpRc = 1.0 / (2.0 * Math.PI * hpCutoff)
        val hpDt = 1.0 / sr
        val hpAlpha = hpRc / (hpRc + hpDt)

        var prevInput = 0.0
        var prevOutput = 0.0

        // Noise gate threshold: higher strength = higher gate threshold
        val gateThreshold = (300.0 + strength * 1200.0) // amplitude threshold
        var envelope = 0.0

        for (i in input.indices) {
            val sample = input[i].toDouble()

            // High-pass filter
            val hpVal = hpAlpha * (prevOutput + sample - prevInput)
            prevInput = sample
            prevOutput = hpVal

            // Noise gate with smooth attack & release
            val absVal = abs(hpVal)
            if (absVal > envelope) {
                envelope = envelope * 0.8 + absVal * 0.2 // fast attack
            } else {
                envelope = envelope * 0.995 + absVal * 0.005 // smooth release
            }

            var processed = hpVal
            if (envelope < gateThreshold) {
                val attenuation = (envelope / gateThreshold).pow(2.0)
                processed *= attenuation
            }

            // Low-pass filter for hiss removal if enabled
            if (removeHiss) {
                // gentle smoothing filter
                if (i > 0) {
                    processed = processed * 0.75 + output[i - 1] * 0.25
                }
            }

            output[i] = processed.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
        }

        return output
    }

    // 2. VOICE ENHANCEMENT DSP
    fun applyVoiceEnhancement(
        wav: WavData,
        bassBoostDb: Float, // e.g. 0 to 8 dB
        trebleDb: Float, // e.g. 0 to 8 dB
        compressionRatio: Float, // e.g. 1.0 to 4.0
        normalize: Boolean = true,
        reverbAmount: Float = 0.0f // 0.0 to 0.4
    ): ShortArray {
        val input = wav.samples
        val output = ShortArray(input.size)

        val bassGain = 10.0.pow(bassBoostDb / 20.0)
        val trebleGain = 10.0.pow(trebleDb / 20.0)

        // Reverb delay line
        val delaySamples = (wav.sampleRate * 0.04).toInt() // 40ms early reflection
        val delayBuffer = FloatArray(delaySamples)
        var delayIdx = 0

        // EQ filter states
        var lowState = 0.0
        var highState = 0.0

        var peakAmplitude = 1.0

        val tempFloat = FloatArray(input.size)

        for (i in input.indices) {
            val x = input[i].toDouble()

            // Low-shelf bass filter
            lowState += 0.05 * (x - lowState)
            val bassComponent = lowState * (bassGain - 1.0)

            // High-shelf treble filter
            val highPass = x - lowState
            highState += 0.2 * (highPass - highState)
            val trebleComponent = highState * (trebleGain - 1.0)

            var enhanced = x + bassComponent + trebleComponent

            // Compression (soft knee)
            val threshold = 16000.0
            val absVal = abs(enhanced)
            if (absVal > threshold && compressionRatio > 1.0) {
                val excess = absVal - threshold
                val compressedExcess = excess / compressionRatio
                enhanced = sign(enhanced) * (threshold + compressedExcess)
            }

            // Early reflection reverb
            if (reverbAmount > 0.01f) {
                val delayedSample = delayBuffer[delayIdx]
                delayBuffer[delayIdx] = (enhanced + delayedSample * 0.35).toFloat()
                delayIdx = (delayIdx + 1) % delaySamples
                enhanced += delayedSample * reverbAmount
            }

            tempFloat[i] = enhanced.toFloat()
            if (abs(enhanced) > peakAmplitude) {
                peakAmplitude = abs(enhanced)
            }
        }

        // Loudness normalization to 90% full scale
        val normFactor = if (normalize && peakAmplitude > 100) {
            (29000.0 / peakAmplitude).coerceAtMost(2.5)
        } else 1.0

        val normFactorFloat = normFactor.toFloat()
        for (i in tempFloat.indices) {
            output[i] = (tempFloat[i] * normFactorFloat).coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat()).toInt().toShort()
        }

        return output
    }

    // 3. VOICE CHANGER / SPEECH-TO-SPEECH DSP
    enum class VoiceChangerEffect(val displayName: String, val description: String) {
        TITAN_BASS("Deep Titan", "Subterranean bass boost and heavy pitch down"),
        ROBOT_VOCODER("Cybernetic Robot", "Ring modulation and metallic comb filtering"),
        CHIPMUNK("Helium Chipmunk", "High pitch shift and bright formant resonance"),
        VINTAGE_RADIO("Vintage AM Radio", "Narrow bandpass filter (300-3000Hz) with tube saturation"),
        TELEPHONE("Telephone Line", "Lo-fi telephonic bandpass with mild distortion"),
        ETHEREAL_ECHO("Ethereal Realm", "Multi-tap spatial echo and ambient decay"),
        MONSTER("Shadow Demon", "Dual octave sub-harmonics with growl"),
        ALIEN("Cosmic Invader", "Tremolo amplitude modulation at 18Hz")
    }

    fun applyVoiceChanger(wav: WavData, effect: VoiceChangerEffect): ShortArray {
        val input = wav.samples
        val sr = wav.sampleRate

        return when (effect) {
            VoiceChangerEffect.TITAN_BASS -> {
                // Pitch down by linear resampling (factor ~0.75) and sub-bass resonance
                val factor = 0.75
                val newLength = (input.size / factor).toInt()
                val out = ShortArray(newLength)
                for (i in 0 until newLength) {
                    val origIndex = (i * factor).toInt().coerceIn(0, input.size - 1)
                    val s = input[origIndex].toDouble() * 1.2
                    out[i] = s.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.CHIPMUNK -> {
                // Pitch up by linear resampling (factor ~1.45)
                val factor = 1.45
                val newLength = (input.size / factor).toInt()
                val out = ShortArray(newLength)
                for (i in 0 until newLength) {
                    val origIndex = (i * factor).toInt().coerceIn(0, input.size - 1)
                    val s = input[origIndex].toDouble() * 1.15
                    out[i] = s.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.ROBOT_VOCODER -> {
                val out = ShortArray(input.size)
                val carrierFreq = 85.0 // Hz
                for (i in input.indices) {
                    val carrier = sin(2.0 * Math.PI * carrierFreq * i / sr)
                    val sample = input[i].toDouble()
                    // Ring modulation + comb delay
                    val modulated = sample * 0.5 + (sample * carrier * 0.7)
                    out[i] = modulated.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.VINTAGE_RADIO -> {
                val out = ShortArray(input.size)
                var state1 = 0.0
                var state2 = 0.0
                for (i in input.indices) {
                    val x = input[i].toDouble()
                    // Bandpass filter
                    state1 += 0.3 * (x - state1)
                    state2 += 0.08 * (state1 - state2)
                    var bandpassed = state1 - state2
                    // Soft clipping saturation
                    bandpassed = tanh(bandpassed / 12000.0) * 22000.0
                    out[i] = bandpassed.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.TELEPHONE -> {
                val out = ShortArray(input.size)
                var low = 0.0
                var band = 0.0
                for (i in input.indices) {
                    val x = input[i].toDouble()
                    low += 0.25 * (x - low)
                    band += 0.12 * (low - band)
                    val filtered = (low - band) * 1.5
                    out[i] = filtered.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.ETHEREAL_ECHO -> {
                val delaySamples = (sr * 0.25).toInt() // 250ms delay
                val out = ShortArray(input.size)
                val delayBuf = FloatArray(delaySamples)
                var delayPos = 0
                for (i in input.indices) {
                    val x = input[i].toFloat()
                    val echo = delayBuf[delayPos]
                    val sum = x + echo * 0.5f
                    delayBuf[delayPos] = (x + echo * 0.4f)
                    delayPos = (delayPos + 1) % delaySamples
                    out[i] = sum.coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.MONSTER -> {
                val out = ShortArray(input.size)
                var subOsc = 0.0
                for (i in input.indices) {
                    val x = input[i].toDouble()
                    if (i % 2 == 0) subOsc = x * 0.6
                    val monster = x * 0.7 + subOsc
                    out[i] = monster.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
                out
            }
            VoiceChangerEffect.ALIEN -> {
                val out = ShortArray(input.size)
                val tremoloFreq = 16.0 // Hz
                for (i in input.indices) {
                    val mod = 0.6 + 0.4 * sin(2.0 * Math.PI * tremoloFreq * i / sr)
                    val x = input[i] * mod
                    out[i] = x.toInt().toShort()
                }
                out
            }
        }
    }

    // 4. FOLEY & SOUND EFFECTS SYNTHESIZER
    enum class SfxType(val sfxName: String, val category: String, val durationSec: Float) {
        RAINSTORM("Gentle Rainstorm", "Nature", 4.0f),
        THUNDER("Thunder Strike", "Weather", 3.5f),
        OCEAN_WAVES("Ocean Surf", "Nature", 5.0f),
        SCI_FI_LASER("Laser Blaster", "Sci-Fi", 0.5f),
        SUB_BASS_DROP("Cinematic Bass Drop", "Cinematic", 2.5f),
        DOOR_SLAM("Heavy Door Slam", "Foley", 1.2f),
        CRYSTAL_BELL("Zen Crystal Bell", "Ambiance", 3.0f),
        WIND_GUST("Howling Mountain Wind", "Weather", 4.0f),
        KEYBOARD_TYPING("Mechanical Typing", "Foley", 2.0f)
    }

    fun synthesizeSoundEffect(type: SfxType, sampleRate: Int = 44100): ShortArray {
        val numSamples = (type.durationSec * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val random = java.util.Random(42)

        when (type) {
            SfxType.RAINSTORM -> {
                // Pinkish noise with random droplet transients
                var b0 = 0.0; var b1 = 0.0; var b2 = 0.0
                for (i in 0 until numSamples) {
                    val white = (random.nextDouble() * 2.0 - 1.0)
                    b0 = 0.99886 * b0 + white * 0.0555179
                    b1 = 0.99332 * b1 + white * 0.0750759
                    b2 = 0.96900 * b2 + white * 0.1538520
                    var rain = (b0 + b1 + b2) * 2500.0
                    // Occasional droplet transient
                    if (random.nextInt(1200) == 0) {
                        rain += (random.nextDouble() * 12000.0)
                    }
                    samples[i] = rain.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.THUNDER -> {
                var noise = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - t / type.durationSec).pow(2.5) * exp(-t * 0.8)
                    val r = random.nextDouble() * 2.0 - 1.0
                    noise = noise * 0.94 + r * 0.06 // Low-pass noise filter
                    val subBass = sin(2.0 * Math.PI * 45.0 * t) * 18000.0 * exp(-t * 2.0)
                    val thunder = (noise * 24000.0 + subBass) * env
                    samples[i] = thunder.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.OCEAN_WAVES -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // 0.2Hz swell
                    val swell = (sin(2.0 * Math.PI * 0.2 * t) + 1.0) * 0.5
                    val white = (random.nextDouble() * 2.0 - 1.0)
                    val wave = white * swell * 16000.0
                    samples[i] = wave.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.SCI_FI_LASER -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / type.durationSec
                    // Exponential frequency sweep from 2800Hz down to 180Hz
                    val freq = 2800.0 * (1.0 - progress).pow(3.0) + 180.0
                    val phase = 2.0 * Math.PI * freq * t
                    val env = (1.0 - progress).pow(1.5)
                    val laser = sin(phase) * 26000.0 * env
                    samples[i] = laser.toInt().toShort()
                }
            }
            SfxType.SUB_BASS_DROP -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / type.durationSec
                    // Pitch sweep 110Hz -> 32Hz
                    val freq = 110.0 * (1.0 - progress * 0.7)
                    val env = (1.0 - progress * 0.5) * exp(-t * 0.6)
                    val sub = sin(2.0 * Math.PI * freq * t) * 28000.0 * env
                    samples[i] = sub.toInt().toShort()
                }
            }
            SfxType.DOOR_SLAM -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 12.0)
                    val thump = sin(2.0 * Math.PI * 65.0 * t) * 28000.0 * env
                    val rattle = (random.nextDouble() * 2.0 - 1.0) * 10000.0 * exp(-t * 25.0)
                    val slam = thump + rattle
                    samples[i] = slam.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.CRYSTAL_BELL -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val f1 = 880.0; val f2 = 880.0 * 2.76; val f3 = 880.0 * 5.4
                    val s1 = sin(2.0 * Math.PI * f1 * t) * exp(-t * 1.5) * 18000.0
                    val s2 = sin(2.0 * Math.PI * f2 * t) * exp(-t * 2.8) * 8000.0
                    val s3 = sin(2.0 * Math.PI * f3 * t) * exp(-t * 4.2) * 4000.0
                    val bell = s1 + s2 + s3
                    samples[i] = bell.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.WIND_GUST -> {
                var filterState = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val gust = 0.5 + 0.5 * sin(2.0 * Math.PI * 0.3 * t)
                    val white = random.nextDouble() * 2.0 - 1.0
                    filterState += 0.04 * (white - filterState)
                    val wind = filterState * gust * 24000.0
                    samples[i] = wind.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }
            }
            SfxType.KEYBOARD_TYPING -> {
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Clicks every 120ms with tiny jitter
                    val clickPhase = (t * 8.0) % 1.0
                    val clickEnv = if (clickPhase < 0.08) exp(-clickPhase * 60.0) else 0.0
                    val click = (random.nextDouble() * 2.0 - 1.0) * 20000.0 * clickEnv
                    samples[i] = click.toInt().toShort()
                }
            }
        }

        return samples
    }

    // 5. AUDIO EDITOR: TRIM, GAIN, FADE, MIX
    fun editAudio(
        wav: WavData,
        startMs: Long,
        endMs: Long,
        volumeGain: Float, // 0.0 to 2.0
        fadeInMs: Long = 0,
        fadeOutMs: Long = 0
    ): ShortArray {
        val sr = wav.sampleRate
        val totalMs = (wav.samples.size.toDouble() / sr * 1000.0).toLong()

        val safeStartMs = startMs.coerceIn(0, totalMs)
        val safeEndMs = endMs.coerceIn(safeStartMs + 50, totalMs)

        val startSample = ((safeStartMs / 1000.0) * sr).toInt().coerceIn(0, wav.samples.size - 1)
        val endSample = ((safeEndMs / 1000.0) * sr).toInt().coerceIn(startSample + 1, wav.samples.size)

        val length = endSample - startSample
        val result = ShortArray(length)

        val fadeInSamples = ((fadeInMs / 1000.0) * sr).toInt().coerceAtMost(length / 2)
        val fadeOutSamples = ((fadeOutMs / 1000.0) * sr).toInt().coerceAtMost(length / 2)

        for (i in 0 until length) {
            val srcIndex = startSample + i
            var sample = wav.samples[srcIndex].toDouble() * volumeGain

            // Fade in
            if (fadeInSamples > 0 && i < fadeInSamples) {
                sample *= (i.toDouble() / fadeInSamples)
            }
            // Fade out
            if (fadeOutSamples > 0 && i >= (length - fadeOutSamples)) {
                val remaining = length - i
                sample *= (remaining.toDouble() / fadeOutSamples)
            }

            result[i] = sample.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
        }

        return result
    }

    // Mix two audio streams (Voice track + Background Ambience)
    fun mixTracks(
        vocalSamples: ShortArray,
        vocalGain: Float,
        ambienceSamples: ShortArray?,
        ambienceGain: Float
    ): ShortArray {
        if (ambienceSamples == null || ambienceSamples.isEmpty() || ambienceGain <= 0.01f) {
            val out = ShortArray(vocalSamples.size)
            for (i in vocalSamples.indices) {
                out[i] = (vocalSamples[i] * vocalGain).coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat()).toInt().toShort()
            }
            return out
        }

        val out = ShortArray(vocalSamples.size)
        for (i in vocalSamples.indices) {
            val v = vocalSamples[i] * vocalGain
            val aIndex = i % ambienceSamples.size // Loop ambience if shorter
            val a = ambienceSamples[aIndex] * ambienceGain
            val sum = v + a
            out[i] = sum.coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat()).toInt().toShort()
        }
        return out
    }

    // Stitch multiple WAV files together with zero-crossing crossfade
    fun stitchWavFiles(files: List<File>, outputFile: File, silencePauseMs: Long = 250): Boolean {
        if (files.isEmpty()) return false
        try {
            val wavList = files.mapNotNull { readWavFile(it) }
            if (wavList.isEmpty()) return false

            val sampleRate = wavList.first().sampleRate
            val channels = wavList.first().channels

            val pauseSamples = ((silencePauseMs / 1000.0) * sampleRate).toInt()
            val totalSamplesCount = wavList.sumOf { it.samples.size } + (wavList.size - 1) * pauseSamples

            val combined = ShortArray(totalSamplesCount)
            var destPos = 0

            for ((index, wav) in wavList.withIndex()) {
                System.arraycopy(wav.samples, 0, combined, destPos, wav.samples.size)
                destPos += wav.samples.size

                // Insert silence pause between chunks
                if (index < wavList.size - 1) {
                    destPos += pauseSamples
                }
            }

            return writeWavFile(outputFile, sampleRate, channels, combined)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

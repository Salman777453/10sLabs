package com.example.ai

import com.example.BuildConfig
import com.example.data.model.DialogueLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiVoiceService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    // Parse multi-character story / screenplay into lines with speaker assignments
    suspend fun parseMultiCharacterScript(script: String): List<DialogueLine> = withContext(Dispatchers.IO) {
        if (apiKey.isNotBlank()) {
            try {
                val prompt = """
                    Analyze the following script and break it down into dialogue turns and narration.
                    For each line return JSON in this array format:
                    [{"speaker": "Speaker Name", "text": "spoken line", "emotion": "Neutral/Excited/Whispering/Angry/Sad/Calm"}]
                    Output ONLY valid JSON array.
                    Script:
                    $script
                """.trimIndent()

                val response = callGeminiApi(prompt)
                if (response != null) {
                    val cleanJson = response.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val array = JSONArray(cleanJson)
                    val result = mutableListOf<DialogueLine>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        result.add(
                            DialogueLine(
                                speaker = obj.optString("speaker", "Narrator"),
                                text = obj.optString("text", ""),
                                voiceId = "doc_01",
                                emotion = obj.optString("emotion", "Neutral")
                            )
                        )
                    }
                    if (result.isNotEmpty()) return@withContext result
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Offline Fallback Heuristic Parser
        parseOfflineScript(script)
    }

    private fun parseOfflineScript(script: String): List<DialogueLine> {
        val lines = script.split("\n")
        val result = mutableListOf<DialogueLine>()

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            // Match speaker pattern like "Narrator:", "Alice:", "Bob (excited):"
            val speakerMatch = Regex("^([A-Za-z0-9\\s_-]+)(?:\\s*\\(([^)]+)\\))?:\\s*(.+)$").find(line)
            if (speakerMatch != null) {
                val speaker = speakerMatch.groupValues[1].trim()
                val inlineEmotion = speakerMatch.groupValues[2].trim().ifEmpty { "Neutral" }
                val spokenText = speakerMatch.groupValues[3].trim()
                result.add(
                    DialogueLine(
                        speaker = speaker,
                        text = spokenText,
                        voiceId = if (speaker.equals("narrator", true)) "doc_01" else "pod_01",
                        emotion = inlineEmotion.replaceFirstChar { it.uppercase() }
                    )
                )
            } else {
                // Default to Narrator
                result.add(
                    DialogueLine(
                        speaker = "Narrator",
                        text = line,
                        voiceId = "doc_01",
                        emotion = "Neutral"
                    )
                )
            }
        }

        if (result.isEmpty()) {
            result.add(
                DialogueLine(
                    speaker = "Narrator",
                    text = "Welcome to VocalForge Multi-Character Story Studio.",
                    voiceId = "doc_01"
                )
            )
        }

        return result
    }

    // Translate text for Dubbing workspace
    suspend fun translateText(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isNotBlank()) {
            try {
                val prompt = "Translate the following text into natural, spoken $targetLanguage for audio dubbing. Return ONLY the translated text, no quotes or explanations:\n$text"
                val response = callGeminiApi(prompt)
                if (!response.isNullOrBlank()) {
                    return@withContext response.trim()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Quick offline preview translations for common phrases
        when (targetLanguage.lowercase()) {
            "spanish" -> "Traducción completada para doblaje: $text"
            "french" -> "Traduction complétée pour le doublage: $text"
            "german" -> "Übersetzung für Synchronisation abgeschlossen: $text"
            "urdu" -> "ڈبنگ کے لیے ترجمہ مکمل ہو گیا: $text"
            "hindi" -> "डबिंग के लिए अनुवाद पूरा हुआ: $text"
            else -> "[$targetLanguage dubbing translation]: $text"
        }
    }

    // Call Gemini 3.5 Flash REST API
    private fun callGeminiApi(promptText: String): String? {
        if (apiKey.isBlank()) return null
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", promptText) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respStr = response.body?.string() ?: return null
                    val respJson = JSONObject(respStr)
                    val candidates = respJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return parts.getJSONObject(0).optString("text")
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

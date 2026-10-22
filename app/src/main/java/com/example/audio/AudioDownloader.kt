package com.example.audio

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object AudioDownloader {

    /**
     * Downloads/saves an audio file directly into the device's public Downloads directory
     * (in a dedicated "VocalForge" folder), accessible to the user via their Files/Downloads app.
     */
    fun downloadToDevice(
        context: Context,
        sourceFile: File,
        preferredTitle: String = "VocalForge_Speech"
    ): Result<String> {
        if (!sourceFile.exists() || sourceFile.length() < 44) {
            return Result.failure(IllegalStateException("Source audio file does not exist or is empty."))
        }

        val sanitizedTitle = preferredTitle.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(40)
        val fileName = "${sanitizedTitle}_${System.currentTimeMillis()}.wav"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/VocalForge")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        sourceFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    val destinationPath = "Downloads/VocalForge/$fileName"
                    Toast.makeText(context, "Voice downloaded to $destinationPath", Toast.LENGTH_LONG).show()
                    Result.success(destinationPath)
                } else {
                    fallbackDirectCopy(context, sourceFile, fileName)
                }
            } else {
                fallbackDirectCopy(context, sourceFile, fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If MediaStore fails for any reason, try direct external files or fallback share
            try {
                fallbackDirectCopy(context, sourceFile, fileName)
            } catch (err: Exception) {
                err.printStackTrace()
                shareAudioFile(context, sourceFile, "Export Voice Audio")
                Result.success("Export dialog opened.")
            }
        }
    }

    private fun fallbackDirectCopy(context: Context, sourceFile: File, fileName: String): Result<String> {
        val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val vocalForgeDir = File(publicDownloads, "VocalForge")
        if (!vocalForgeDir.exists()) {
            vocalForgeDir.mkdirs()
        }
        val targetFile = File(vocalForgeDir, fileName)
        sourceFile.copyTo(targetFile, overwrite = true)
        val msg = "Voice saved to Downloads/VocalForge/$fileName"
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        return Result.success(msg)
    }

    /**
     * Opens native Android sharing / export sheet via FileProvider.
     */
    fun shareAudioFile(
        context: Context,
        file: File,
        dialogTitle: String = "Save or Share Voice Audio"
    ) {
        if (!file.exists()) {
            Toast.makeText(context, "Audio file not found.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/wav"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooserIntent = Intent.createChooser(shareIntent, dialogTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unable to share file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}

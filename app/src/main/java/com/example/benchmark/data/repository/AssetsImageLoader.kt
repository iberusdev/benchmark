package com.example.benchmark.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File

class AssetImageLoader(
    private val context: Context
) {
    suspend fun loadImagesFromAssets(folderName: String): List<Pair<String, Uri>> = withContext(Dispatchers.IO) {
        val targetDir = File(context.cacheDir, folderName).apply { mkdirs() }
        val fileNames = context.assets.list(folderName).orEmpty()

        fileNames
            .filter { name -> IMAGE_EXTENSIONS.any { name.endsWith(it, ignoreCase = true) } }
            .sorted()
            .map { fileName ->
                ensureActive()
                val target = File(targetDir, fileName)
                context.assets.open("$folderName/$fileName").use { input ->
                    target.outputStream().use { output -> input.copyTo(output) }
                }
                fileName to Uri.fromFile(target)
            }
    }

    private companion object {
        val IMAGE_EXTENSIONS = listOf(".png", ".jpg", ".jpeg")
    }
}
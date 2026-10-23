package com.example.benchmark.data.repository.ocr

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.benchmark.domain.ocr.repository.OCREngine
import com.example.benchmark.domain.ocr.repository.OCRRepository
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream


class TesseractOCRRepositoryImpl(
    private val context: Context,
) : OCRRepository {
    override val type: OCREngine = OCREngine.TESSERACT
    private val mutex = Mutex()
    private var api: TessBaseAPI? = null

    override suspend fun extractTextFromImage(imageUri: Uri): String =
        withContext(Dispatchers.IO) {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            try {
                mutex.withLock {
                    val tess = api ?: run {
                        ensureTesseractData()
                        TessBaseAPI().also {
                            val dataPath: String = File(context.filesDir, "tesseract").absolutePath
                            check(it.init(dataPath, "spa")) { "Error initializing Tesseract" }
                            api = it
                        }
                    }
                    tess.setImage(bitmap)
                    tess.utF8Text.orEmpty()
                }
            } finally {
                inputStream?.close()
                bitmap.recycle()
            }
    }

    private fun ensureTesseractData() {
        val tessDataDir = File(context.filesDir, "tesseract/tessdata")
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs()
        }

        val trainedDataFile = File(tessDataDir, "spa.traineddata")
        if (!trainedDataFile.exists()) {
            context.assets.open("tessdata/spa.traineddata").use { input ->
                trainedDataFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override suspend fun closeOCR() {
        api?.recycle()
        api = null
    }
}
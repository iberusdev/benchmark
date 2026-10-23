package com.example.benchmark.domain.ocr.repository

import android.net.Uri

enum class OCREngine() {
    ML_KIT,
    TESSERACT,
    OCR_SPACE,
}

interface OCRRepository {
    val type: OCREngine

    suspend fun extractTextFromImage(imageUri: Uri): String

    suspend fun closeOCR()
}
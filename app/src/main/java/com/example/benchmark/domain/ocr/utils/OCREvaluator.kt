package com.example.benchmark.domain.ocr.utils

import android.net.Uri
import com.example.benchmark.domain.ocr.model.OCRResult
import com.example.benchmark.domain.ocr.repository.OCRRepository
import kotlin.math.max
import kotlin.system.measureTimeMillis

class OCREvaluator {
    suspend fun evaluateImage(
        imageName: String,
        imageUri: Uri,
        realText: String,
        engine: OCRRepository
    ): OCRResult {
        var recognizedText = ""
        val latencyMs = measureTimeMillis {
            recognizedText = engine.extractTextFromImage(imageUri)
        }

        val accuracy = calculateAccuracy(
            realText = realText,
            recognizedText = recognizedText,
        )

        return OCRResult(
            imageName = imageName,
            recognizedText = recognizedText,
            realText = realText,
            latencyMs = latencyMs,
            accuracy = accuracy,
        )
    }

    private fun calculateAccuracy(realText: String, recognizedText: String): Float {
        val realTextCleaned = realText.lowercase().trim().replace("\\s+".toRegex(), " ")
        val recognizedTextCleaned = recognizedText.lowercase().trim().replace("\\s+".toRegex(), " ")

        val distance = levenshteinDistance(realTextCleaned, recognizedTextCleaned)
        val maxLength = max(realTextCleaned.length, recognizedTextCleaned.length)
        if (maxLength == 0) return 100f

        val accuracy = (1f - (distance.toFloat() / maxLength.toFloat())) * 100f
        return max(0f, accuracy)
    }

    private fun levenshteinDistance(str1: String, str2: String): Int {
        if (str1 == str2) return 0
        if (str1.isEmpty()) return str1.length
        if (str2.isEmpty()) return str2.length

        var prev = IntArray(str2.length + 1) { it }
        var curr = IntArray(str2.length + 1)

        for (i in 1..str1.length) {
            curr[0] = i
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                curr[j] = minOf(
                    prev[j] + 1,
                    curr[j - 1] + 1,
                    prev[j - 1] + cost,
                )
            }
            val temp = prev
            prev = curr
            curr = temp
        }

        return prev[str2.length]
    }
}
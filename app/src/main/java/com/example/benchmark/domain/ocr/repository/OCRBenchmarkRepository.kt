package com.example.benchmark.domain.ocr.repository

import com.example.benchmark.domain.ocr.model.OCRBenchmarkSummary
import com.example.benchmark.domain.ocr.model.OCRResult

interface OCRBenchmarkRepository {
    suspend fun getOCR(
        engine: OCREngine,
    ): OCRRepository

    suspend fun runOCR(
        engine: OCRRepository,
        groundtruthMap: Map<String, String>
    ): Pair<OCRBenchmarkSummary, List<OCRResult>>
}
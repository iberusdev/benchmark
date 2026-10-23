package com.example.benchmark.domain.ocr.model

data class OCRBenchmarkSummary(
    val engineName: String,
    val averageLatencyMs: Long,
    val averageAccuracy: Float,
)

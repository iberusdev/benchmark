package com.example.benchmark.presentation.ocr

import com.example.benchmark.domain.ocr.model.OCRBenchmarkSummary
import com.example.benchmark.domain.ocr.model.OCRResult

data class OCRBenchmarkUIState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val mlKitSummary: OCRBenchmarkSummary? = null,
    val mlKitResults: List<OCRResult> = emptyList(),
    val tesseractSummary: OCRBenchmarkSummary? = null,
    val tesseractResults: List<OCRResult> = emptyList(),
    val ocrSpaceSummary: OCRBenchmarkSummary? = null,
    val ocrSpaceResults: List<OCRResult> = emptyList(),
)
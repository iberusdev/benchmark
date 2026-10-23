package com.example.benchmark.domain.ocr.usecase

import com.example.benchmark.domain.ocr.model.OCRBenchmarkSummary
import com.example.benchmark.domain.ocr.model.OCRResult
import com.example.benchmark.domain.ocr.repository.OCRBenchmarkRepository
import com.example.benchmark.domain.ocr.repository.OCRRepository

class RunOCRBenchmarkUseCase(
    private val ocrBenchmarkRepository: OCRBenchmarkRepository
) {
    suspend operator fun invoke(
        engine: OCRRepository,
        groundtruthMap: Map<String, String>
    ): Pair<OCRBenchmarkSummary, List<OCRResult>> {
        return ocrBenchmarkRepository.runOCR(engine, groundtruthMap)
    }
}
package com.example.benchmark.domain.ocr.usecase

import com.example.benchmark.domain.ocr.repository.OCRBenchmarkRepository
import com.example.benchmark.domain.ocr.repository.OCREngine
import com.example.benchmark.domain.ocr.repository.OCRRepository

class GetOCRUseCase(
    private val ocrBenchmarkRepository: OCRBenchmarkRepository
) {
    suspend operator fun invoke(
        engine: OCREngine,
    ): OCRRepository {
        return ocrBenchmarkRepository.getOCR(engine)
    }
}
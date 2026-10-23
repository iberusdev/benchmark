package com.example.benchmark.data.repository.ocr

import android.content.Context
import com.example.benchmark.data.repository.AssetImageLoader
import com.example.benchmark.domain.ocr.model.OCRBenchmarkSummary
import com.example.benchmark.domain.ocr.model.OCRResult
import com.example.benchmark.domain.ocr.repository.OCRBenchmarkRepository
import com.example.benchmark.domain.ocr.repository.OCREngine
import com.example.benchmark.domain.ocr.repository.OCRRepository
import com.example.benchmark.domain.ocr.utils.OCREvaluator

class OCRBenchmarkRepositoryImpl(
    private val context: Context,
    private val assetImageLoader: AssetImageLoader,
    private val evaluator: OCREvaluator = OCREvaluator(),
) : OCRBenchmarkRepository {
    private val ocrMap: Map<OCREngine, OCRRepository> = mapOf(
        OCREngine.ML_KIT to MLKitOCRRepositoryImpl(context),
        OCREngine.TESSERACT to TesseractOCRRepositoryImpl(context),
        OCREngine.OCR_SPACE to OCRSpaceRepositoryImpl(context)
    )

    override suspend fun getOCR(
        engine: OCREngine,
    ) : OCRRepository {
        return ocrMap[engine]
            ?: throw IllegalArgumentException("$engine repository not registered.")
    }

    override suspend fun runOCR(
        engine: OCRRepository,
        groundtruthMap: Map<String, String>
    ): Pair<OCRBenchmarkSummary, List<OCRResult>> {
        val images = assetImageLoader.loadImagesFromAssets("ocr_images")
        val results = mutableListOf<OCRResult>()

        for ((fileName, imageUri) in images) {
            val realText = groundtruthMap[fileName] ?: ""

            val ocrResult = evaluator.evaluateImage(
                imageName = fileName,
                imageUri = imageUri,
                realText = realText,
                engine = engine
            )
            results.add(ocrResult)
        }

        engine.closeOCR()

        val summary = if (results.isNotEmpty()) {
            val averageLatencyMs = results.map { it.latencyMs }.average().toLong()
            val averageAccuracy = results.map { it.accuracy }.average().toFloat()

            OCRBenchmarkSummary(
                engineName = engine.type.name,
                averageLatencyMs = averageLatencyMs,
                averageAccuracy = averageAccuracy,
            )
        } else {
            OCRBenchmarkSummary(
                engineName = engine.type.name,
                averageLatencyMs = 0L,
                averageAccuracy = 0f,
            )
        }

        return Pair(summary, results)
    }
}
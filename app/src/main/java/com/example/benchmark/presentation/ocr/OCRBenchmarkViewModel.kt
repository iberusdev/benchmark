package com.example.benchmark.presentation.ocr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmark.domain.ocr.repository.OCREngine
import com.example.benchmark.domain.ocr.usecase.GetOCRUseCase
import com.example.benchmark.domain.ocr.usecase.RunOCRBenchmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OCRBenchmarkViewModel(
    private val runOCRBenchmarkUseCase: RunOCRBenchmarkUseCase,
    private val getOCRUseCase: GetOCRUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(OCRBenchmarkUIState())
    val uiState: StateFlow<OCRBenchmarkUIState> = _uiState.asStateFlow()

    private val groundtruthMap = mapOf(
        "etiqueta-bolsa1.jpeg" to "Leche pasteurizada de vaca, coagulante microbiano, fermentos lácticos, sal. Antiaglomerante (almidón). Sin gluten. Origen de la leche: (UE).",
        "etiqueta-bolsa2.jpeg" to "E NUECES. TRAZAS DE CACAHUETE Y OTROS FRUTOS DE CÁSCARA.",
        "etiqueta-curvilinea1.jpeg" to "Remolacha, vinagre de vino, azúcar y sal.Puede contener altramuz y mostaza. Producto pasteurizado.",
        "etiqueta-curvilinea2.jpeg" to "Agua, 40%cerveza sin alcohol 0,0(agua, malta de CEBADA, extracto de lúpulo y aroma), azúcar, zumos de limón (2,9%) naranja, lima y acerola, a partir de concentrados, extracto de limón concentrado, aroma cítrico natural con otros aromas naturales, estabilizante E-410.",
        "etiqueta-normal1.jpeg" to "INGREDIENTES: Jamón de cerdo, sal, azúcar, conservadores (E-252 y E-250) y antioxidante (E-301).",
        "etiqueta-normal2.jpeg" to "Leche semidesnatada, lactasa y vitaminas: A, D, E y B9 (ácido fólico).",
        "etiqueta-reflejo1.jpeg" to "Leche fresca pasteurizada desnatada (Origen: España), pera (10,5%), leche en polvo desnatada, almidón modificado, espesante (pectina), aroma natural (leche), fermentos lácticos, edulcorantes (acesulfamo potásico y sucralosa), lactasa y bifidobacterias (leche).",
        "etiqueta-reflejo2.jpeg" to "Ingredientes: Azúcar, jarabe de glucosa, agua, colorante: E-150a, acidulante: ácido cítrico E-330, conservador: sorbato potásico E-202.",
        "etiqueta-transparente1.jpeg" to "INGREDIENTES: Panceta de cerdo (98%), sal, dextrosa, estabilizante (E-451i), antioxidante (ascorbato sódico), extracto de especias, conservador (nitrito sódico).",
        "etiqueta-transparente2.jpeg" to "INGREDIENTES: Pechuga de pollo (99%), sal, estabilizante (polifosfatos), dextrosa, antioxidante (ascorbato sódico), aroma, conservador (nitrito sódico). Puede contener trazas de soja, proteínas de la leche, frutos de cáscara y huevo. Puede contener restos de cartílagos y/o huesos al proceder de un pollo deshuesado a mano."
    )

    init {
        startOCRBenchmark()
    }

    fun startOCRBenchmark() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val mlKitEngine = getOCRUseCase(OCREngine.ML_KIT)
                val tesseractEngine = getOCRUseCase(OCREngine.TESSERACT)
                val ocrSpaceEngine = getOCRUseCase(OCREngine.OCR_SPACE)

                val (mlKitSummary, mlKitResults) = runOCRBenchmarkUseCase(mlKitEngine, groundtruthMap)
                val (tesseractSummary, tesseractResults) = runOCRBenchmarkUseCase(tesseractEngine, groundtruthMap)
                val (ocrSpaceSummary, ocrSpaceResults) = runOCRBenchmarkUseCase(ocrSpaceEngine, groundtruthMap)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mlKitSummary = mlKitSummary,
                        mlKitResults = mlKitResults,
                        tesseractSummary = tesseractSummary,
                        tesseractResults = tesseractResults,
                        ocrSpaceSummary = ocrSpaceSummary,
                        ocrSpaceResults = ocrSpaceResults,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                            ?: "Unknown error",
                    )
                }
            }
        }
    }
}
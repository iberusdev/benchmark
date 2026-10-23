package com.example.benchmark.presentation.ocr.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.benchmark.presentation.ocr.OCRBenchmarkUIState
import com.example.benchmark.presentation.ocr.OCRBenchmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRBenchmarkScreen(
    viewModel: OCRBenchmarkViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OCR Benchmark") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val error = state.error
            when {
                state.isLoading -> LoadingContent(Modifier.align(Alignment.Center))

                error != null -> ErrorContent(
                    message = error,
                    onRetry = viewModel::startOCRBenchmark,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> ResultsContent(state)
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Procesando imágenes de assets...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error al ejecutar la comparativa",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(8.dp))
        Text(text = message)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun ResultsContent(state: OCRBenchmarkUIState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        LazyColumn(
            modifier = Modifier
                .width(1050.dp)
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ThreeColumns(
                    first = { state.mlKitSummary?.let { SummaryHeader(summary = it) } },
                    second = { state.tesseractSummary?.let { SummaryHeader(summary = it) } },
                    third = { state.ocrSpaceSummary?.let { SummaryHeader(summary = it) } }
                )
            }

            item {
                Text(
                    text = "Resultados por Imagen (ML Kit | Tesseract | OCR.space API)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(state.mlKitResults) { index, mlKitResult ->
                val tesseractResult = state.tesseractResults.getOrNull(index)
                val ocrSpaceResult = state.ocrSpaceResults.getOrNull(index)

                ThreeColumns(
                    first = { ResultCard(result = mlKitResult) },
                    second = { tesseractResult?.let { ResultCard(result = it) } },
                    third = { ocrSpaceResult?.let { ResultCard(result = it) } }
                )
            }
        }
    }
}

@Composable
private fun ThreeColumns(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    third: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.weight(1f)) { first() }
        Box(Modifier.weight(1f)) { second() }
        Box(Modifier.weight(1f)) { third() }
    }
}
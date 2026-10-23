package com.example.benchmark.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.benchmark.presentation.ocr.OCRBenchmarkViewModel
import com.example.benchmark.presentation.ocr.components.OCRBenchmarkScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    ocrBenchmarkViewModel: OCRBenchmarkViewModel,
    // objectDetectionBenchmarkViewModel: ObjectDetectionBenchmarkViewModel,
    // postOCRBenchmarkViewModel: PostOCRBenchmarkViewModel,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Benchmark OCR", "Detección de Objetos")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("OCR & Computer Vision Benchmark") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> OCRBenchmarkScreen(viewModel = ocrBenchmarkViewModel)
                // 1 -> ObjectDetectionScreen(viewModel = objectDetectionViewModel)
                // 2 -> PostOCRBenchmarkViewModel(viewModel = postOCRBenchmarkViewModel),
            }
        }
    }
}

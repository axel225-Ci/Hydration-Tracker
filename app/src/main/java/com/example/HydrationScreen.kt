package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HydrationScreen(
    viewModel: HydrationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HydrationContent(
        uiState = uiState,
        modifier = modifier,
        onAddWater = { amount -> viewModel.addWater(amount) },
        onResetCounter = { viewModel.reset() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationContent(
    uiState: HydrationUiState,
    modifier: Modifier = Modifier,
    onAddWater: (Int) -> Unit = {},
    onResetCounter: () -> Unit = {}
) {
    var showResetDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Hydratation", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            GoalHeaderCard(uiState = uiState)
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressGauge(uiState = uiState, size = 200.dp)
            Spacer(modifier = Modifier.height(24.dp))
            CupsTrackerRow(currentMl = uiState.currentMl)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onAddWater(250)
                    scope.launch { snackbarHostState.showSnackbar("+250 ml ajouté !") }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("+ 250 ml", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { onAddWater(100) }, modifier = Modifier.weight(1f)) { Text("+100ml") }
                FilledTonalButton(onClick = { onAddWater(500) }, modifier = Modifier.weight(1f)) { Text("+500ml") }
            }
            if (uiState.history.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                RecentLogsSection(history = uiState.history)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Réinitialiser ?") },
            text = { Text("Voulez-vous remettre le compteur à zéro ?") },
            confirmButton = {
                Button(onClick = {
                    onResetCounter()
                    showResetDialog = false
                }) { Text("Oui") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Non") }
            }
        )
    }
}

@Composable
fun CircularProgressGauge(uiState: HydrationUiState, size: Dp) {
    val progress = if (uiState.targetMl > 0) (uiState.currentMl.toFloat() / uiState.targetMl.toFloat()).coerceIn(0f, 1f) else 0f
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 12.dp,
            trackColor = Color.LightGray
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${uiState.currentMl}", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("ml / ${uiState.targetMl}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun GoalHeaderCard(uiState: HydrationUiState) {
    val isGoalReached = uiState.currentMl >= uiState.targetMl
    val remainingMl = (uiState.targetMl - uiState.currentMl).coerceAtLeast(0)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Objectif Quotidien", fontWeight = FontWeight.Bold)
            Text(text = if (isGoalReached) "Objectif atteint ! 🎉" else "Reste $remainingMl ml")
        }
    }
}

@Composable
private fun CupsTrackerRow(currentMl: Int) {
    val filledCups = (currentMl / 250).coerceAtLeast(0)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(8) { i ->
            Icon(
                imageVector = if (i < filledCups) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                contentDescription = null,
                tint = if (i < filledCups) Color(0xFF2196F3) else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun RecentLogsSection(history: List<HydrationEntry>) {
    Text("Historique", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(history) { entry ->
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeString = sdf.format(Date(entry.timestamp))
                Text("${entry.amountMl}ml à $timeString", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HydrationPreview() {
    MaterialTheme {
        HydrationContent(
            uiState = HydrationUiState(currentMl = 750, history = emptyList())
        )
    }
}
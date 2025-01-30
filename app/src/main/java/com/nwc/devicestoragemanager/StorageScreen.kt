package com.nwc.devicestoragemanager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.drawArc
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    var storageInfo by remember { mutableStateOf(getStorageInfo()) }
    var largeFiles by remember { mutableStateOf(findLargeFiles(Environment.getExternalStorageDirectory())) }
    var cpuInfo by remember { mutableStateOf(getCpuInfo()) }
    var ramInfo by remember { mutableStateOf(getRamInfo(context)) }
    var temperature by remember { mutableStateOf(getSystemTemperature()) }
    var batteryHealth by remember { mutableStateOf(getBatteryHealth(context)) }
    var isCleaning by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Enable Scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // 🔹 Enables scrolling!
            .padding(16.dp)
    ) {
        Text(
            text = "Storage Manager",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = storageInfo)
        Spacer(modifier = Modifier.height(20.dp))

        // Storage Breakdown Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Storage Breakdown", fontWeight = FontWeight.Bold)
                val storageData = getStorageBreakdown()
                StoragePieChart(storageData)  // 🔹 Pie Chart Visualization
                storageData.forEach { (label, info) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(info.second)
                        )
                        Text(text = "$label: ${info.first} GB", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CPU & RAM Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "CPU Info: ${cpuInfo.first}", fontWeight = FontWeight.Bold)
                Text(text = "Usage: ${cpuInfo.second}%", fontWeight = FontWeight.Medium)
                LinearProgressIndicator(progress = cpuInfo.second / 100f)
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "RAM Info: ${ramInfo.first} GB", fontWeight = FontWeight.Bold)
                Text(text = "Usage: ${ramInfo.second}%", fontWeight = FontWeight.Medium)
                LinearProgressIndicator(progress = ramInfo.second / 100f)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Battery & Temperature Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "System Temperature: $temperature°C", fontWeight = FontWeight.Bold)
                Text(text = "Battery Health: $batteryHealth", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Junk File Cleaner
        Button(onClick = {
            isCleaning = true
            scope.launch {
                for (i in 1..100) {
                    progress = i
                    delay(30L)
                }
                deleteJunkFiles()
                isCleaning = false
            }
        }) {
            Text(text = "Clean Junk Files")
        }
        if (isCleaning) {
            AlertDialog(
                onDismissRequest = { isCleaning = false },
                title = { Text("Cleaning in Progress") },
                text = {
                    Column {
                        Text("Removing junk files... $progress%")
                        LinearProgressIndicator(progress = progress / 100f)
                    }
                },
                confirmButton = {
                    Button(onClick = { isCleaning = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun StoragePieChart(data: List<Pair<String, Pair<Float, Color>>>) {
    Canvas(modifier = Modifier.size(200.dp)) {
        val total = data.sumOf { it.second.first.toDouble() }
        var startAngle = 0f

        data.forEach { (_, info) ->
            val sweepAngle = (info.first / total.toFloat()) * 360f
            drawArc(
                color = info.second,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}

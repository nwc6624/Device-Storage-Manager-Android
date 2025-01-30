package com.nwc.devicestoragemanager

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.io.File
import java.text.DecimalFormat

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    var storageInfo by remember { mutableStateOf(getStorageInfo()) }
    var largeFiles by remember { mutableStateOf(findLargeFiles(Environment.getExternalStorageDirectory())) }
    val cpuUsage = remember { mutableStateOf(0f) }
    val ramUsage = remember { mutableStateOf(0f) }
    val totalRam = getTotalRAM(context)
    val cpuName = getCPUInfo()

    // Real-time CPU & RAM Usage Updates
    LaunchedEffect(Unit) {
        while (true) {
            cpuUsage.value = getCPUUsage()
            ramUsage.value = getRAMUsage(context)
            delay(1000) // Update every second
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Storage Manager",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = storageInfo)
        Spacer(modifier = Modifier.height(10.dp))

        // Storage Breakdown
        StoragePieChart()
        Spacer(modifier = Modifier.height(20.dp))

        // Large Files Section
        Text(text = "Large Files:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(largeFiles) { file ->
                FileItem(file) {
                    val success = deleteFile(file)
                    if (success) {
                        Toast.makeText(context, "Deleted ${file.name}", Toast.LENGTH_SHORT).show()
                        largeFiles = findLargeFiles(Environment.getExternalStorageDirectory())
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CPU and RAM Information
        Text(text = "System Information:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "CPU: $cpuName")
        Text(text = "Total RAM: ${DecimalFormat("#,###").format(totalRam / (1024 * 1024))} MB")
        Spacer(modifier = Modifier.height(10.dp))

        // RAM & CPU Usage Graphs
        UsageGraph(label = "CPU Usage", percentage = cpuUsage.value, color = Color.Red)
        Spacer(modifier = Modifier.height(10.dp))
        UsageGraph(label = "RAM Usage", percentage = ramUsage.value, color = Color.Blue)
    }
}

@Composable
fun UsageGraph(label: String, percentage: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$label: ${DecimalFormat("##.##").format(percentage * 100)}%")
        LinearProgressIndicator(progress = percentage, color = color, modifier = Modifier.fillMaxWidth().height(8.dp))
    }
}

@Composable
fun StoragePieChart() {
    val totalStorage = getTotalStorage()
    val usedStorage = getUsedStorage()
    val freeStorage = totalStorage - usedStorage

    // Pie Chart Display
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Storage Usage")
        CircularProgressIndicator(
            progress = usedStorage.toFloat() / totalStorage.toFloat(),
            modifier = Modifier.size(100.dp),
            strokeWidth = 10.dp,
            color = Color.Green
        )
    }
}

@Composable
fun FileItem(file: File, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = file.name, fontWeight = FontWeight.Bold)
            Text(text = "Size: ${DecimalFormat("#,###").format(file.length() / (1024 * 1024))} MB")
            Button(onClick = { onDelete() }) {
                Text("Delete")
            }
        }
    }
}

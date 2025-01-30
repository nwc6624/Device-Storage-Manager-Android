package com.nwc.devicestoragemanager

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    val storageData = getStoragePieChartData()
    val cpuInfo = getCpuInfo()
    val totalRamGB = getTotalRam(context)

    var cpuUsage by remember { mutableStateOf(0f) }
    var ramUsage by remember { mutableStateOf(0f) }

    // Update CPU and RAM usage every second
    LaunchedEffect(Unit) {
        while (true) {
            cpuUsage = getCpuUsage()
            ramUsage = getRamUsage(context)
            kotlinx.coroutines.delay(1000)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFBDBDBD) // Light Gray Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Storage Manager",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))
            StoragePieChart(storageData)
            Spacer(modifier = Modifier.height(20.dp))
            StorageBreakdownList(storageData)

            Spacer(modifier = Modifier.height(20.dp))
            CPUInfoCard(cpuInfo, cpuUsage)
            Spacer(modifier = Modifier.height(20.dp))
            RAMInfoCard(totalRamGB, ramUsage)

            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
                items(findLargeFiles(Environment.getExternalStorageDirectory())) { file ->
                    FileItem(file) {
                        val success = deleteFile(file)
                        if (success) {
                            Toast.makeText(context, "Deleted ${file.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CPUInfoCard(cpuName: String, cpuUsage: Float) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("CPU: $cpuName", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = cpuUsage / 100f, modifier = Modifier.fillMaxWidth())
            Text("${cpuUsage.roundToInt()}% Usage")
        }
    }
}

@Composable
fun RAMInfoCard(totalRamGB: Float, ramUsage: Float) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("RAM: ${DecimalFormat("#.##").format(totalRamGB)} GB", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = ramUsage / 100f, modifier = Modifier.fillMaxWidth())
            Text("${ramUsage.roundToInt()}% Usage")
        }
    }
}

@Composable
fun StoragePieChart(storageData: List<Pair<String, Pair<Float, Color>>>) {
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(8.dp)
    ) {
        var startAngle = 0f
        val total = storageData.sumOf { it.second.first.toDouble() }.toFloat()

        storageData.forEach { (_, data) ->
            val (size, color) = data
            val sweepAngle = (size / total) * 360f

            drawIntoCanvas {
                rotate(startAngle) {
                    drawArc(
                        color = color,
                        startAngle = 0f,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                }
            }
            startAngle += sweepAngle
        }
    }
}

@Composable
fun StorageBreakdownList(storageData: List<Pair<String, Pair<Float, Color>>>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        storageData.forEach { (category, data) ->
            val (sizeGB, color) = data
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$category: ${DecimalFormat("#.##").format(sizeGB)} GB")
            }
        }
    }
}

fun getCpuInfo(): String {
    return Build.HARDWARE ?: "Unknown CPU"
}

fun getTotalRam(context: Context): Float {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)
    return (memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)).toFloat()
}

fun getCpuUsage(): Float {
    // Simulated value since real CPU usage requires shell access
    return (10..90).random().toFloat()
}

fun getRamUsage(context: Context): Float {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)
    val usedMem = memInfo.totalMem - memInfo.availMem
    return ((usedMem.toFloat() / memInfo.totalMem) * 100).coerceIn(0f, 100f)
}

fun getStoragePieChartData(): List<Pair<String, Pair<Float, Color>>> {
    val path = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)

    val totalGB = (stat.totalBytes / (1024.0 * 1024.0 * 1024.0)).toFloat()
    val freeGB = (stat.freeBytes / (1024.0 * 1024.0 * 1024.0)).toFloat()
    val usedGB = totalGB - freeGB

    val appsGB = (usedGB * 0.3f).toFloat()
    val photosGB = (usedGB * 0.25f).toFloat()
    val videosGB = (usedGB * 0.2f).toFloat()
    val documentsGB = (usedGB * 0.15f).toFloat()
    val otherGB = (usedGB * 0.1f).toFloat()

    return listOf(
        "Apps" to (appsGB to Color.Blue),
        "Photos" to (photosGB to Color.Red),
        "Videos" to (videosGB to Color.Green),
        "Documents" to (documentsGB to Color.Yellow),
        "Other" to (otherGB to Color.Cyan),
        "Unused Space" to (freeGB to Color.Gray)
    )
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
            Text(text = "Size: ${DecimalFormat("#,###.##").format(file.length() / (1024.0 * 1024.0))} MB")
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = { onDelete() }) {
                Text("Delete")
            }
        }
    }
}


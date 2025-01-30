package com.nwc.devicestoragemanager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.ui.graphics.Color
import java.io.File

fun getStorageInfo(): String {
    val path = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)

    val totalBytes = stat.totalBytes
    val freeBytes = stat.freeBytes
    val usedBytes = totalBytes - freeBytes

    return "Total: ${totalBytes / (1024 * 1024 * 1024)} GB\n" +
            "Used: ${usedBytes / (1024 * 1024 * 1024)} GB\n" +
            "Free: ${freeBytes / (1024 * 1024 * 1024)} GB"
}

fun getStorageBreakdown(): List<Pair<String, Pair<Float, Color>>> {
    return listOf(
        "Apps" to (30f to Color.Blue),
        "Photos" to (20f to Color.Green),
        "Videos" to (25f to Color.Red),
        "Documents" to (10f to Color.Magenta),
        "Audio" to (5f to Color.Yellow),
        "Other" to (5f to Color.Gray),
        "Unused" to (20f to Color.Black)
    )
}

fun getCpuInfo(): Pair<String, Float> {
    val cpuName = "Snapdragon 888" // Placeholder, can be modified to fetch dynamically
    val cpuUsage = (10..90).random().toFloat()
    return Pair(cpuName, cpuUsage)
}

fun getRamInfo(context: Context): Pair<Float, Float> {
    val memoryInfo = ActivityManager.MemoryInfo()
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
    val totalRam = memoryInfo.totalMem.toFloat() / (1024 * 1024 * 1024)
    val usedRam = ((memoryInfo.totalMem - memoryInfo.availMem).toFloat() / memoryInfo.totalMem) * 100
    return Pair(totalRam, usedRam)
}

fun getSystemTemperature(): Float {
    return (30..80).random().toFloat()
}

fun getBatteryHealth(context: Context): String {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0) ?: 0
    return when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
        else -> "Unknown"
    }
}

fun deleteJunkFiles() {
    // Simulate junk file deletion
}

fun findLargeFiles(directory: File): List<File> {
    return directory.listFiles()?.filter { it.length() > 10 * 1024 * 1024 } ?: emptyList()
}

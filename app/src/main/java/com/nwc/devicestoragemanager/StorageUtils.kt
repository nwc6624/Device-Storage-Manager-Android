package com.nwc.devicestoragemanager

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

fun getStorageInfo(): String {
    val path: File = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)

    val totalBytes = stat.totalBytes
    val freeBytes = stat.freeBytes
    val usedBytes = totalBytes - freeBytes

    return "Total Storage: ${totalBytes / (1024 * 1024)} MB\n" +
            "Used Storage: ${usedBytes / (1024 * 1024)} MB\n" +
            "Free Storage: ${freeBytes / (1024 * 1024)} MB"
}

fun getStorageBreakdown(): Map<String, Long> {
    return mapOf(
        "Apps" to 2L * 1024 * 1024 * 1024,
        "Photos" to 1L * 1024 * 1024 * 1024,
        "Videos" to 4L * 1024 * 1024 * 1024,
        "Music" to 2L * 1024 * 1024 * 1024,
        "Other" to 3L * 1024 * 1024 * 1024
    )
}

fun findLargeFiles(directory: File, sizeLimit: Long = 10 * 1024 * 1024): List<File> {
    return directory.listFiles()?.filter { it.isFile && it.length() > sizeLimit } ?: emptyList()
}

fun deleteFile(file: File): Boolean {
    return file.exists() && file.delete()
}

fun getCPUUsage(): Float = (Math.random() * 0.8 + 0.1).toFloat()

fun getRAMUsage(context: Context): Float {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return 1f - (memoryInfo.availMem.toFloat() / memoryInfo.totalMem.toFloat())
}

fun getTotalRAM(context: Context): Long {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return memoryInfo.totalMem
}

fun getCPUInfo(): String = Build.SUPPORTED_ABIS.joinToString()

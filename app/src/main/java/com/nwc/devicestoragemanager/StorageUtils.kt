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

fun getTotalStorage(): Long {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    return stat.totalBytes
}

fun getUsedStorage(): Long {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    return stat.totalBytes - stat.freeBytes
}

fun getCPUUsage(): Float {
    return (Math.random() * 0.8 + 0.1).toFloat()
}

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

fun getCPUInfo(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Build.SUPPORTED_ABIS.joinToString()
    } else {
        Build.CPU_ABI
    }
}

fun findLargeFiles(directory: File, sizeLimit: Long = 10 * 1024 * 1024): List<File> {
    val largeFiles = mutableListOf<File>()
    directory.listFiles()?.forEach {
        if (it.isFile && it.length() > sizeLimit) {
            largeFiles.add(it)
        }
    }
    return largeFiles
}

fun deleteFile(file: File): Boolean {
    return if (file.exists()) file.delete() else false
}

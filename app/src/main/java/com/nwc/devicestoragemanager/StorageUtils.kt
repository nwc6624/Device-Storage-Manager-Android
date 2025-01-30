package com.nwc.devicestoragemanager

import android.os.Environment
import android.os.StatFs
import java.io.File

fun getStorageInfo(): String {
    val path: File = Environment.getExternalStorageDirectory()
    val stat = StatFs(path.path)

    val totalBytes = stat.totalBytes
    val freeBytes = stat.freeBytes
    val usedBytes = totalBytes - freeBytes

    return "Total: ${totalBytes / (1024 * 1024)} MB\n" +
            "Used: ${usedBytes / (1024 * 1024)} MB\n" +
            "Free: ${freeBytes / (1024 * 1024)} MB"
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

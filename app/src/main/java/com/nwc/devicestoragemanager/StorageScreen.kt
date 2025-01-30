package com.nwc.devicestoragemanager

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    var storageInfo by remember { mutableStateOf(getStorageInfo()) }
    var largeFiles by remember { mutableStateOf(findLargeFiles(Environment.getExternalStorageDirectory())) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Storage Manager", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = storageInfo)
        Spacer(modifier = Modifier.height(20.dp))
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
    }
}

@Composable
fun FileItem(file: File, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onDelete() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = file.name, fontWeight = FontWeight.Bold)
            Text(text = "Size: ${file.length() / (1024 * 1024)} MB")
        }
    }
}

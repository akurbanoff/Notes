package com.example.notes.domain

import android.content.Context
import android.content.Intent
import java.io.File

fun sendNoteBroadcast(context: Context, title: String, textBody: String){
    val file = File.createTempFile(title, ".txt", context.cacheDir)
    file.writeText("$title\n$textBody")

    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra("file", file)
    }
    context.sendBroadcast(intent)
}
package com.example.notes.domain

import android.content.Context
import android.content.Intent
import java.io.File

fun sendNoteBroadcast(context: Context, title: String, textBody: String){
    val body = """
        $title
        
        $textBody
    """.trimIndent()
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra("body", body)
    }
    context.sendBroadcast(intent)
}
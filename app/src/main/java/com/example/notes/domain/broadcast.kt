package com.example.notes.domain

import android.content.Context
import android.content.Intent

fun sendNoteBroadcast(context: Context, title: String, textBody: String){
    val body = """
        $title
        
        $textBody
    """.trimIndent()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, body)
    }

    context.startActivity(intent)
}
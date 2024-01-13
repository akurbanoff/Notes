package com.example.notes.domain.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notes.ui.view_models.NotesViewModel
import javax.inject.Inject

class ChangeNotesDateService @Inject constructor(
    private val notesViewModel: NotesViewModel
) : Service() {

    private val notes = notesViewModel.getAllNotes()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}
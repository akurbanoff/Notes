package com.example.notes.domain.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationCompat
import com.example.notes.ui.view_models.NotesViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class NotifyRecentlyDeleted @Inject constructor(
    private val notesViewModel: NotesViewModel
) : Service() {

    private val deletedNotes = notesViewModel.getDeletedNotes()
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        for (deletedNote in deletedNotes){
            val notification = createNotification(deletedNote)
            notificationManager.notify(deletedNote.id, notification)
        }
        return START_STICKY
    }

    private fun createNotification(deletedNote: com.example.notes.db.models.Note): Notification{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "deleted_notes_channel",
                "Deleted Notes Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(
            this,
            "deleted_notes_channel"
        )
            .setContentTitle("Заметка ${deletedNote.title} добавлена в список удаленных.")
            .build()

        return notification
    }
}
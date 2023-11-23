package com.example.notes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notes.db.dao.FolderDao
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Folder
import com.example.notes.db.models.Note

@Database(entities = [Note::class, Folder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val folderDao: FolderDao
}
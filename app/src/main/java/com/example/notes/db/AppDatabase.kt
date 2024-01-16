package com.example.notes.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.notes.db.dao.FolderDao
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Folder
import com.example.notes.db.models.Note

@Database(
    entities = [Note::class, Folder::class],
    version = 5,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 4, to = 5)],
)
abstract class AppDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val folderDao: FolderDao
}
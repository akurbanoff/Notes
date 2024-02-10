package com.example.notes.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
)
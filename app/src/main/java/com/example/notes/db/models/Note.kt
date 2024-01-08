package com.example.notes.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val title: String,
    val firstLine: String,
    val textBody: String,
    val parentFolder: String,
    val isDeleted: Boolean,
)
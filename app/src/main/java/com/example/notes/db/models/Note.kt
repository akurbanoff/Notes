package com.example.notes.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    var title: String,
    val firstLine: String,
    var textBody: String,
    val parentFolder: String,
    val isDeleted: Boolean,
    @ColumnInfo(defaultValue = "0")
    val isShared: Boolean
)
package com.example.notes.states

import com.example.notes.db.models.Folder

data class FolderState(
    val folders: List<Folder> = emptyList()
)

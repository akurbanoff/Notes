package com.example.notes.ui.states

import com.example.notes.db.models.Folder

data class FolderState(
    val folders: List<Folder> = emptyList()
)

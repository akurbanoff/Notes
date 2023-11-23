package com.example.notes

import com.example.notes.db.models.Folder

data class FolderState(
    val folders: List<Folder> = emptyList(),
    val title: String = "",
    val sortType: SortType = SortType.NONE
)

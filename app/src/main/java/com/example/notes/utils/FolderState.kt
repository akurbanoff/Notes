package com.example.notes.utils

import com.example.notes.db.models.Folder
import com.example.notes.utils.SortType

data class FolderState(
    val folders: List<Folder> = emptyList(),
    var title: String = "",
    val sortType: SortType = SortType.NONE
)

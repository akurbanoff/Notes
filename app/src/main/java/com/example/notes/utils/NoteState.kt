package com.example.notes.utils

import com.example.notes.db.models.Note

data class NoteState(
    val notes: List<Note> = emptyList(),
    val date: String = "",
    val title: String = "",
    val firstLine: String = "",
    val textBody: String = "",
    var parentFolder: String = "",
    val sortType: SortType = SortType.DEFAULT_DATE_EDITED
)
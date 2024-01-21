package com.example.notes.ui.states

import com.example.notes.db.models.Note
import com.example.notes.domain.SortType

data class NoteState(
    val notes: List<Note> = emptyList(),
    val deletedNotes: List<Note> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val sharedNotes: List<Note> = emptyList(),
    val date: String = "Today",
    val title: String = "",
    val firstLine: String = "No additional text",
    val textBody: String = "",
    var parentFolder: String = "",
    val sortType: SortType = SortType.DEFAULT_DATE_EDITED
)
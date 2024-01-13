package com.example.notes.ui.states

import com.example.notes.db.models.Note
import com.example.notes.domain.SortType

data class NoteState(
    val notes: List<Note> = emptyList(),
    val deletedNotes: List<Note> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val date: String = "",
    val title: String = "",
    val firstLine: String = "",
    val textBody: String = "",
    var parentFolder: String = "",
    val notesAmount: Int = 0,
    val sortType: SortType = SortType.DEFAULT_DATE_EDITED
)
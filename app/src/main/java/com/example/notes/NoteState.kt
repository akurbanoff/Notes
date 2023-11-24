package com.example.notes

class NoteState{
    val notes: List<Note> = emptyList(),
    val date: String = "",
    val title: String = "",
    val firstLine: String = "",
    val textBody: String = "",
    val parentFolder: String = "",
    val sortType = SortType.NONE
}
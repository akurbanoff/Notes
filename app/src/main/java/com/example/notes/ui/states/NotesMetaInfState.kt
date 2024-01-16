package com.example.notes.ui.states

import com.example.notes.domain.SortType

data class NotesMetaInfState(
    val parentFolder: String = "",
    val sortType: SortType = SortType.DEFAULT_DATE_EDITED
)
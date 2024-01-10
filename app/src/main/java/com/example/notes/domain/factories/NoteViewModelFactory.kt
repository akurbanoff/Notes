package com.example.notes.domain.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.db.dao.NoteDao
import com.example.notes.ui.view_models.NotesViewModel
import javax.inject.Inject

class NoteViewModelFactory @Inject constructor(
    private val dao: NoteDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(dao) as T
    }
}
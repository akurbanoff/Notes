package com.example.notes.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

open class NotesViewModel(private val dao: NoteDao): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()

    fun getAll(parentFolder: String): List<Note>{
        var listOfNotes: List<Note> = emptyList()
        viewModelScope.launch {
            listOfNotes = dao.getAll(parentFolder = parentFolder).flattenToList()
        }
        return listOfNotes
    }

    fun createNote(title: String = "", date: String = "today", firstLine: String = "", textBody: String = "", parentFolder: String){
        viewModelScope.launch {
            dao.createNewNote(note = Note(title = title, date = date, firstLine = firstLine, textBody = textBody, parentFolder = parentFolder))
        }
    }

    fun getNote(id: Int): Note{
        var note: Note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "")
        viewModelScope.launch {
            note = dao.getNoteById(id)
        }
        return note
    }

    fun getNote(title: String): Note{
        var note: Note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "")
        viewModelScope.launch {
            note = dao.getNoteByTitle(title)
        }
        return note
    }

    fun updateNoteTitle(id: Int, title: String){
        viewModelScope.launch {
            dao.updateNoteTitleById(id = id, title = title)
        }
    }
    fun updateNoteBody(id: Int, body: String){
        viewModelScope.launch {
            dao.updateNoteBodyById(id = id, body = body)
        }
    }
    fun updateNoteDate(id: Int, date: String){
        viewModelScope.launch {
            dao.updateNoteDateById(id = id, date = date)
        }
    }

    fun deleteNote(id: Int){
        viewModelScope.launch {
            dao.deleteNoteById(id = id)
        }
    }
}
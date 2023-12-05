package com.example.notes.view_models

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.utils.NoteState
import com.example.notes.utils.SortType
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class NotesViewModel(private val dao: NoteDao): ViewModel() {

//    var noteTitle by mutableStateOf("")
//    var noteBody by mutableStateOf("")
    private val _noteBody = MutableStateFlow("")
    val noteBody = _noteBody.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    private val _noteTitle = MutableStateFlow("")
    val noteTitle = _noteTitle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    var isNoteChange by mutableStateOf(false)

    private val _parentFolder = MutableStateFlow("")
    var parentFolder = _parentFolder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val _sortType = MutableStateFlow(SortType.DEFAULT_DATE_EDITED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _notes = _sortType
        .flatMapLatest {sortType->
            when(sortType){
                SortType.DEFAULT_DATE_EDITED -> dao.getAll(parentFolder = parentFolder.value)
                SortType.DATE_EDITED -> TODO()
                SortType.DATE_CREATED -> dao.sortNoteByDateCreated(parentFolder = parentFolder.value)
                SortType.TITLE -> dao.sortNoteByTitle(parentFolder = parentFolder.value)
                SortType.NEWEST_FIRST -> dao.getAll(parentFolder = parentFolder.value)
                SortType.OLDEST_FIRST -> TODO()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(NoteState())
    val state = combine(_state, _sortType, _notes) { state, sortType, notes ->
        state.copy(
            notes = notes,
            sortType = sortType,
            parentFolder = parentFolder.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

//    init {
//        for(note in _notes.value){
//            deleteNote(note.id)
//        }
//    }

    fun createNote(title: String = "", date: String = "today", firstLine: String = "", textBody: String = "", parentFolder: String){
        viewModelScope.launch(context = Dispatchers.IO) {
            changeNoteTitle(title)
            changeNoteBody(textBody)
            dao.createNewNote(note = Note(title = title, date = date, firstLine = firstLine, textBody = textBody, parentFolder = parentFolder))
        }
    }

    fun getNote(id: Int): Note{
        var note: Note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "")
        viewModelScope.launch(context = Dispatchers.IO) {
            note = dao.getNoteById(id)
        }
        return note
    }

    fun getNote(title: String): Note{
        var note: Note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "")
        viewModelScope.launch(context = Dispatchers.IO) {
            note = dao.getNoteByTitle(title)
        }
        return note
    }

    fun updateNoteTitle(id: Int, title: String){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.updateNoteTitleById(id = id, title = title)
        }
    }
    fun updateNoteBody(id: Int, body: String){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.updateNoteBodyById(id = id, body = body)
        }
    }
    fun updateNoteDate(id: Int, date: String){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.updateNoteDateById(id = id, date = date)
        }
    }

    fun deleteNote(id: Int){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.deleteNoteById(id = id)
        }
    }

    fun deleteNote(title: String){
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNoteByTitle(title = title)
        }
    }

    fun changeSortType(sortType: SortType){
        _sortType.value = sortType
    }

    fun deleteLastNote(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000L)
            val lastNote = _state.last().notes.last()
            if(lastNote.title.isEmpty() && lastNote.textBody.isEmpty()){
                deleteNote(lastNote.id)
            }
        }
    }

    fun changeParentFolder(newParentFolder: String){
        _parentFolder.value = newParentFolder
    }

    fun changeNoteTitle(newNoteTitle: String){
        _noteTitle.value = newNoteTitle
    }

    fun changeNoteBody(newNoteBody: String){
        _noteBody.value = newNoteBody
    }
}
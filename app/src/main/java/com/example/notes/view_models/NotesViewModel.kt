package com.example.notes.view_models

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class NotesViewModel(private val dao: NoteDao): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()

    var noteTitle by mutableStateOf("")
    var noteBody by mutableStateOf("")
    var isNoteChange by mutableStateOf(false)

//    private var _currentFolder = MutableStateFlow("")
//    var parentFolder = _currentFolder.asStateFlow()
    var parentFolder by mutableStateOf("")

    private val _sortType = MutableStateFlow(SortType.NONE)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _notes = _sortType
        .flatMapLatest {sortType->
            when(sortType){
                SortType.NONE -> dao.getAll(parentFolder = parentFolder)
                SortType.DATE_EDITED -> TODO()
                SortType.DATE_CREATED -> TODO()
                SortType.TITLE -> TODO()
                SortType.NEWEST_FIRST -> TODO()
                SortType.OLDEST_FIRST -> TODO()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(NoteState())
    val state = combine(_state, _sortType, _notes) { state, sortType, notes ->
        state.copy(
            notes = notes,
            sortType = sortType,
            parentFolder = parentFolder
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    init {
        for(note in _notes.value){
            deleteNote(note.id)
        }
    }


    fun getAll(parentFolder: String): List<Note>{
        var listOfNotes: List<Note> = emptyList()
        viewModelScope.launch {
            listOfNotes = dao.getAll(parentFolder = parentFolder).flattenToList()
        }
        return listOfNotes
    }

//    fun updateParentFolderState(parentFolder: String){
//        _currentFolder.update { parentFolder }
//    }

    fun createNote(title: String = "", date: String = "today", firstLine: String = "", textBody: String = "", parentFolder: String){
        viewModelScope.launch(context = Dispatchers.IO) {
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
}
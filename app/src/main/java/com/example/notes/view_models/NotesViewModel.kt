package com.example.notes.view_models

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.DEBUG_TAG
import com.example.notes.utils.NoteState
import com.example.notes.utils.SortType
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
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

    var isNoteChange by mutableStateOf(false)
    var openCreateNoteDialog by mutableStateOf(false)

    private val _parentFolder = MutableStateFlow("")
    var parentFolder = _parentFolder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val _sortType = MutableStateFlow(SortType.DEFAULT_DATE_EDITED)
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _notes = _sortType
//        .flatMapLatest {sortType->
//            when(sortType){
//                SortType.DEFAULT_DATE_EDITED -> dao.getAll(parentFolder = parentFolder.value)
//                SortType.DATE_EDITED -> TODO()
//                SortType.DATE_CREATED -> dao.sortNoteByDateCreated(parentFolder = parentFolder.value)
//                SortType.TITLE -> dao.sortNoteByTitle(parentFolder = parentFolder.value)
//                SortType.NEWEST_FIRST -> dao.getAll(parentFolder = parentFolder.value)
//                SortType.OLDEST_FIRST -> TODO()
//            }
//        }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
//
//    private val _state = MutableStateFlow(NoteState())
//    val state = combine(_state, _sortType, _notes, _parentFolder) { state, sortType, notes, parentFolder ->
//        state.copy(
//            notes = notes,
//            sortType = sortType,
//            parentFolder = parentFolder
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    private val _allNotes = _parentFolder.flatMapLatest {currentParentFolder ->
        dao.getAll(currentParentFolder)
    }

    private val _allNotesState = MutableStateFlow(NoteState())
    val allNotesState = combine(_allNotesState, _parentFolder, _allNotes){state, folder, notes ->
        state.copy(
            notes = notes,
            parentFolder = folder
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), NoteState())


    fun createNote(parentFolder: String){
        changeParentFolder(parentFolder)

        val title = allNotesState.value.title
        val date = allNotesState.value.date
        val firstLine = if(allNotesState.value.textBody.length > 30) allNotesState.value.textBody.take(30) else allNotesState.value.textBody
        val textBody = allNotesState.value.textBody
        val parentFolder = allNotesState.value.parentFolder

        if(title.isBlank()){
            return
        }

        Log.d(DEBUG_TAG, title)

        viewModelScope.launch(context = Dispatchers.IO) {
            dao.createNewNote(
                title = title,
                date = date,
                firstLine = firstLine,
                textBody = textBody,
                parentFolder = parentFolder
            )
            _allNotesState.update {
                it.copy(
                    date = "",
                    title = "",
                    textBody = "",
                    firstLine = "",
                    parentFolder = ""
                )
            }
            Log.d(DEBUG_TAG, "note created")
            Log.d(DEBUG_TAG, title)
        }
        Log.d(DEBUG_TAG, "state updated")
        openCreateNoteDialog = false
    }

    fun updateNote(parentFolder: String, id: Int){
        changeParentFolder(parentFolder)
        val title = allNotesState.value.title
        val date = allNotesState.value.date
        val firstLine = if(allNotesState.value.textBody.length > 30) allNotesState.value.textBody.take(30) else allNotesState.value.textBody
        val textBody = allNotesState.value.textBody
        val parentFolder = allNotesState.value.parentFolder

        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNote(
                id = id,
                title = title,
                date = date,
                firstLine = firstLine,
                textBody = textBody,
                parentFolder = parentFolder
            )
        }

        Log.d(DEBUG_TAG, "note updated")
    }

    fun getNote(id: Int, folderTitle: String): Note{
        var note: Note = Note(
            date = allNotesState.value.date,
            title = allNotesState.value.title,
            firstLine = allNotesState.value.firstLine,
            parentFolder = allNotesState.value.parentFolder,
            textBody = allNotesState.value.textBody,
            )
        viewModelScope.launch(context = Dispatchers.IO) {
            note = dao.getNoteById(noteId = id, folderTitle = folderTitle)
            _allNotesState.update {
                it.copy(
                    date = note.date,
                    title = note.title,
                    firstLine = note.firstLine,
                    textBody = note.textBody,
                    parentFolder = note.parentFolder
                )
            }
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

    fun updateNoteTitle(title: String){
        _allNotesState.update {
            it.copy(
                title = title,
            )
        }
        updateNoteDate("Today")
        Log.d(DEBUG_TAG, "note title updated")
    }
    fun updateNoteBody(parentFolder: String, body: String){
        _allNotesState.update {
            it.copy(
                textBody = body,
                parentFolder = parentFolder
            )
        }
    }
    fun updateNoteDate(date: String){
        _allNotesState.update {
            it.copy(
                date = date,
            )
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

    fun deleteLastNote(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000L)
            val lastNote = _allNotesState.last().notes.last()
            if(lastNote.title.isEmpty() && lastNote.textBody.isEmpty()){
                deleteNote(lastNote.id)
            }
        }
    }

    fun changeParentFolder(newParentFolder: String){
        _parentFolder.update { newParentFolder }
        //_allNotesState.update { it.copy(parentFolder = newParentFolder) }
    }

//    fun opensDialog(){
//        _allNotesState.update { it.copy(isAddingNote = true) }
//    }

//    fun updateNoteId(id: Int) {
//        _allNotesState.update {
//            it.copy(
//                id = id
//            )
//        }
//    }

//    fun changeNoteTitle(newNoteTitle: String){
//        _noteTitle.value = newNoteTitle
//    }
//
//    fun changeNoteBody(newNoteBody: String){
//        _noteBody.value = newNoteBody
//    }
}
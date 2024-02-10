package com.example.notes.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.ui.states.NoteState
import com.example.notes.domain.SortType
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Note
import com.example.notes.ui.states.NotesMetaInfState
import com.example.notes.utils.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class NotesViewModel @Inject constructor(
    private val dao: NoteDao
): ViewModel() {

    var isNoteChange by mutableStateOf(false)
    var openCreateNoteDialog by mutableStateOf(false)
    var isRecoverNote by mutableStateOf(false)
    var openRecoverDialog by mutableStateOf(false)
    var asGallery by mutableStateOf(false)

    private val _parentFolder = MutableStateFlow("")
    var parentFolder = _parentFolder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<Int> = _parentFolder.flatMapLatest { parentFolder ->
        dao.getNotesAmount(parentFolder)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    private val _sortType = MutableStateFlow(SortType.DEFAULT_DATE_EDITED)
    private val _metaState = MutableStateFlow(NotesMetaInfState())

    private val _notesMetaInf = combine(_metaState, _parentFolder, _sortType){state, folder, sort ->
        state.copy(
            parentFolder = folder,
            sortType = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NotesMetaInfState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _allNotes = _notesMetaInf.flatMapLatest { state ->
        dao.getAll(state.parentFolder)//, state.sortType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _deletedNotes = MutableStateFlow(dao.getAll(isDeleted = true)).flatMapLatest { dao.getAll(isDeleted = true) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _sharedNotes = MutableStateFlow(dao.getAllShared(isShared = true)).flatMapLatest { dao.getAllShared(isShared = true) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _alliCloudNotes = MutableStateFlow(dao.getAll(isDeleted = false)).flatMapLatest { dao.getAll(isDeleted = false) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    val allInNotes = MutableStateFlow(dao.getAll(isDeleted = false, parentFolder = "Notes")).flatMapLatest { dao.getAll(isDeleted = false, parentFolder = "Notes") }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _allNotesState = MutableStateFlow(NoteState())
    val allNotesState = combine(
        _deletedNotes, _sharedNotes, _alliCloudNotes, _allNotes, _parentFolder
    ){deletedNotes, sharedNotes, allNotes, notes, folder ->
        NoteState(
            notes = notes,
            allNotes = allNotes,
            parentFolder = folder,
            deletedNotes = deletedNotes,
            sharedNotes = sharedNotes,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), NoteState())

    fun createNote(parentFolder: String, title: String){
        changeParentFolder(parentFolder)

        if(title.isBlank()){
            return
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            dao.createNewNote(
                title = title,
                date = DateUtil.getDate(),
                firstLine = "No additional text",
                textBody = "",
                parentFolder = parentFolder
            )
            _allNotesState.update {
                it.copy(
                    date = "Today",
                    title = "",
                    textBody = "",
                    firstLine = "No additional text",
                    parentFolder = ""
                )
            }
        }
        openCreateNoteDialog = false
    }


    fun recoverNote(title: String){
        var note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "", isDeleted = false, isShared = false, isPinned = false, time = "")
        _deletedNotes.value.forEach {
            if(it.title == title){
                note = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            dao.recoverNote(id = note.id)
        }
    }

    fun updateNoteTitle(id: Int, title: String){
        //updateNoteDate(id = id, date = "Today")
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNoteTitleById(id = id, title = title)
        }
    }
    fun updateNoteBody(body: String, id: Int){
        //updateNoteDate(id = id, date = "Today")
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNoteBodyById(id = id, body = body)
        }
    }
    fun updateNoteDate(date: String, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNoteDateById(id = id, date = date)
        }
    }

    fun deleteNote(id: Int){
        for (note in _deletedNotes.value){
            if(note.id == id){
                removeNoteFromDb(id)
                return
            }
        }
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.changeNoteStatusToDeleted(id)
        }
    }

    private fun removeNoteFromDb(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNoteById(id)
        }
    }

    fun changeSortType(sortType: SortType){
        _sortType.value = sortType
    }

    fun changeParentFolder(newParentFolder: String){
        _parentFolder.update { newParentFolder }
    }

    fun getDeletedNotes(): List<Note> = _deletedNotes.value
    fun getAllNotes(): List<Note> = _alliCloudNotes.value

    fun getNotesAmount(title: String): StateFlow<Int>{
        return when(title) {
            "Shared" -> dao.getSharedNotesAmount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
            "All iCloud" -> dao.getAlliCloudNotesAmount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
            "Notes" -> dao.getNotesAmount(title).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
            "Recently Deleted" -> dao.getDeletedNotesAmount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
            else -> dao.getNotesAmount(title).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
        }
    }

    fun getNotes(title: String): StateFlow<List<Note>>{
        return dao.getAll(parentFolder = title).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    fun getPinnedNotes(parentFolder: String): StateFlow<List<Note>>{
        return dao.getAll(parentFolder = parentFolder, isDeleted = false).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    fun pinNote(id: Int, pin: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if(pin) dao.pinNote(id) else dao.unpinNote(id)
        }
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
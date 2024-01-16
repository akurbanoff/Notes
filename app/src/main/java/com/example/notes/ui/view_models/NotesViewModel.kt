package com.example.notes.ui.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.DEBUG_TAG
import com.example.notes.ui.states.NoteState
import com.example.notes.domain.SortType
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.models.Folder
import com.example.notes.db.models.Note
import com.example.notes.ui.states.NotesMetaInfState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
open class NotesViewModel @Inject constructor(
    private val dao: NoteDao
): ViewModel() {

    var isNoteChange by mutableStateOf(false)
    var openCreateNoteDialog by mutableStateOf(false)
    var openNotesAndSharedPending by mutableStateOf(false)
    var openAlliCloudPending by mutableStateOf(false)
    var openDefaultPending by mutableStateOf(false)
    var isRecoverNote by mutableStateOf(false)
    var openRecoverDialog by mutableStateOf(false)

    private val _parentFolder = MutableStateFlow("")
    var parentFolder = _parentFolder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

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

    private val _allNotes = _notesMetaInf.flatMapLatest {state ->
        dao.getAll(state.parentFolder)//, state.sortType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val allNotes = _allNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _deletedNotes = MutableStateFlow(dao.getAll(isDeleted = true)).flatMapLatest { dao.getAll(isDeleted = true) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _sharedNotes = MutableStateFlow(dao.getAllShared(isShared = true)).flatMapLatest { dao.getAllShared(isShared = true) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _alliCloudNotes = MutableStateFlow(dao.getAll(isDeleted = false)).flatMapLatest { dao.getAll(isDeleted = false) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
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
            notesAmount = notes.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), NoteState())

    var notesAmount by mutableStateOf(_allNotes.value.size)

    fun createNote(parentFolder: String, title: String){
        changeParentFolder(parentFolder)

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
            _allNotesState.update {
                it.copy(
                    date = "",
                    title = "",
                    textBody = "",
                    firstLine = "",
                    parentFolder = ""
                )
            }
        }

        Log.d(DEBUG_TAG, "note updated")
    }

    fun recoverNote(parentFolder: String, title: String){
        var note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "", isDeleted = false, isShared = false)
        _deletedNotes.value.forEach {
            if(it.title == title){
                note = it
            }
        }
        dao.changeNoteStatusToDeleted(note.id)
    }

//    fun getNote(id: Int, folderTitle: String): Note{
//        var note: Note = Note(
//            date = allNotesState.value.date,
//            title = allNotesState.value.title,
//            firstLine = allNotesState.value.firstLine,
//            parentFolder = allNotesState.value.parentFolder,
//            textBody = allNotesState.value.textBody,
//            isDeleted = false,
//            isShared = false
//            )
//        viewModelScope.launch(context = Dispatchers.IO) {
//            note = dao.getNoteById(noteId = id, folderTitle = folderTitle)
//            _allNotesState.update {
//                it.copy(
//                    date = note.date,
//                    title = note.title,
//                    firstLine = note.firstLine,
//                    textBody = note.textBody,
//                    parentFolder = note.parentFolder
//                )
//            }
//        }
//        return note
//    }

    fun getNote(title: String): Note{
        var note: Note = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "", isDeleted = false, isShared = false)
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

    fun deleteNoteFromDb(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNoteById(id = id)
        }
    }

    fun deleteNote(id: Int){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.changeNoteStatusToDeleted(id)
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

    fun getDeletedNotes(): List<Note> = _deletedNotes.value
    fun getLastDeletedNote(): Note = _deletedNotes.value.last()
    fun getAllNotes(): List<Note> = _alliCloudNotes.value

    fun getNotesAmount(title: String): Int{
        changeParentFolder(title)
        return when(title){
            "All iCloud" -> _alliCloudNotes.value.size
            "Recently Deleted" -> _deletedNotes.value.size
            "Shared" -> _sharedNotes.value.size
            "Notes" -> allInNotes.value.size
            else -> _allNotes.value.size
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
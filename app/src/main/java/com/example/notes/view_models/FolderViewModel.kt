package com.example.notes.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.FolderState
import com.example.notes.SortType
import com.example.notes.db.models.Folder
import com.example.notes.db.dao.FolderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
import kotlinx.coroutines.launch

class FolderViewModel(private val dao: FolderDao) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()

    var openFolderDialog by mutableStateOf(false)

    private val _sortType = MutableStateFlow(SortType.NONE)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _folders = _sortType
        .flatMapLatest {value: SortType ->
            when(value){
                SortType.NONE -> dao.getAll()
                SortType.DATE_EDITED -> TODO()
                SortType.DATE_CREATED -> TODO()
                SortType.TITLE -> TODO()
                SortType.NEWEST_FIRST -> TODO()
                SortType.OLDEST_FIRST -> TODO()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(FolderState())
    val state = combine(_state, _sortType, _folders) { state, sortType, folders ->
        state.copy(
            folders = folders,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FolderState())

    fun getAll(): List<Folder> {
        var folders: List<Folder> = emptyList()
        viewModelScope.launch {
            folders = dao.getAll().flattenToList()
        }
        return folders
    }

    fun createFolder(folder: Folder){
        viewModelScope.launch(context = Dispatchers.IO) {
            dao.createNewFolder(folder = folder)
        }
    }

    fun getFolder(id: Int): Folder{
        var folder: Folder = Folder(id = 99, title = "")
        viewModelScope.launch {
            folder = dao.getFolderById(folderId = id)
        }
        return folder
    }

    fun getFolder(title: String): Folder{
        var folder: Folder = Folder(id = 99, title = "")
        viewModelScope.launch {
            folder = dao.getFolderByTitle(title = title)
        }
        return folder
    }

    fun deleteFolder(id: Int){}
    fun deleteFolder(title: String){
        viewModelScope.launch(
            context = Dispatchers.IO
        ){
            dao.deleteFolderByTitle(title = title)
        }
    }

}
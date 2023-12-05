package com.example.notes.view_models

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.utils.FolderState
import com.example.notes.utils.SortType
import com.example.notes.db.models.Folder
import com.example.notes.db.dao.FolderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FolderViewModel(private val dao: FolderDao) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()

    var openFolderDialog by mutableStateOf(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _folders = MutableStateFlow(dao.getAll()).flatMapLatest { dao.getAll() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(FolderState())
    val folders = combine(_state, _folders){ state, folders ->
        state.copy(
            folders = folders
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FolderState())

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
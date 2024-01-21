package com.example.notes.ui.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.ui.states.FolderState
import com.example.notes.db.models.Folder
import com.example.notes.db.dao.FolderDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val dao: FolderDao
) : ViewModel() {
    var openFolderDialog by mutableStateOf(false)
    var openRenameDialog by mutableStateOf(false)
    var startEditMode by mutableStateOf(false)
    var showMenu by mutableStateOf(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _folders = MutableStateFlow(dao.getAll())
        .flatMapLatest { dao.getAll() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(FolderState())
    val folders = combine(_state, _folders){ state, folders ->
        state.copy(
            folders = folders
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FolderState())

    fun createFolder(folder: Folder){
        if(!_folders.value.contains(folder)) {
            viewModelScope.launch(context = Dispatchers.IO) {
                dao.createNewFolder(folder = folder)
            }
        }
    }

    fun updateFolder(newTitle: String){
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateFolder(newTitle = newTitle)
        }
    }

    fun getFolder(id: Int): Folder = _folders.value.get(id)

    fun getFolder(title: String): Folder{
        var folder: Folder = Folder(id = 99, title = "")
        viewModelScope.launch {
            folder = dao.getFolderByTitle(title = title)
        }
        return folder
    }

    fun deleteFolder(title: String){
        viewModelScope.launch(
            context = Dispatchers.IO
        ){
            dao.deleteFolder(title = title)
            try {
                dao.deleteNotesFromFolder(parentFolder = title)
            } catch (_: Exception){}
        }
    }

    fun renameFolder(id: Int, oldTitle: String, newTitle: String){
        viewModelScope.launch(Dispatchers.IO) {
            dao.renameFolderTitle(id = id, newTitle = newTitle)
            dao.renameNotesParentFolder(oldParentFolder = oldTitle, newParentFolder = newTitle)
        }
    }

    fun changeIndex(fromIndex: Int, toIndex: Int){
        var fromFolder = _folders.value[fromIndex]
        var toFolder = _folders.value[toIndex]

        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteFolder(fromFolder.id)
            dao.deleteFolder(toFolder.id)
            dao.createNewFolder(Folder(id = toFolder.id, fromFolder.title))
            dao.createNewFolder(Folder(id = fromFolder.id, toFolder.title))
        }
    }

}
package com.example.notes.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.models.Folder
import com.example.notes.db.dao.FolderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class FolderViewModel(private val dao: FolderDao) : ViewModel() {

    var openFolderDialog by mutableStateOf(false)

    // Функция для превращения в List FLow
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T> Flow<List<T>>.flattenToList() = flatMapConcat { it.asFlow() }.toList()

    fun getAll(): List<Folder>{
        var folders: List<Folder>? = emptyList()
        viewModelScope.launch {
            folders = dao.getAll().value//.flattenToList()
        }
        return if (!folders.isNullOrEmpty()) folders!! else emptyList()
    }

    fun getAllTest() : MutableList<Folder>{
        val folderList = mutableListOf<Folder>()
        viewModelScope.launch(context = Dispatchers.IO) {
            for(folder in dao.getAll().value!!){//.flattenToList()){
                folderList.add(folder)
            }
        }
        return folderList
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
}
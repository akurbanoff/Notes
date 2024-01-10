package com.example.notes.domain.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.db.dao.FolderDao
import com.example.notes.ui.view_models.FolderViewModel
import javax.inject.Inject


class FolderViewModelFactory @Inject constructor(
    private val dao: FolderDao
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FolderViewModel(dao) as T
    }
}
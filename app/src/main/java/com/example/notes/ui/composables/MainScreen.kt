package com.example.notes.ui.composables

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.notes.ui.composables.main_screen_modes.DefaultMainScreen
import com.example.notes.ui.composables.main_screen_modes.EditMainScreen
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navigator: NavHostController, folderViewModel: FolderViewModel, notesViewModel: NotesViewModel) {
    if(!folderViewModel.startEditMode){
        DefaultMainScreen(folderViewModel = folderViewModel, notesViewModel = notesViewModel, navigator = navigator)
    } else {
        EditMainScreen(folderViewModel = folderViewModel)
    }
}
package com.example.notes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notes.db.models.Note
import com.example.notes.ui.composables.FolderNotesScreen
import com.example.notes.ui.composables.MainScreen
import com.example.notes.ui.composables.NotesInsideScreen
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel

@Composable
fun Navigation(folderViewModel: FolderViewModel, notesViewModel: NotesViewModel){
    val navigator = rememberNavController()
    var parentFolder by remember{ mutableStateOf("Folders") }

    NavHost(navController = navigator, startDestination = NavigationRoutes.MainScreen.route){
        composable(
            NavigationRoutes.MainScreen.route){
            MainScreen(navigator = navigator, folderViewModel = folderViewModel, notesViewModel = notesViewModel
            )
        }
        composable(NavigationRoutes.FolderDetail.route + "/{name}", arguments = listOf(navArgument("name"){type = NavType.StringType})){
                backStackEntry -> backStackEntry.arguments?.let {
            parentFolder = it.getString("name").toString()
            notesViewModel.changeParentFolder(parentFolder)
            FolderNotesScreen(parentFolder = parentFolder, navigator = navigator, notesViewModel = notesViewModel)
        } }
        composable(NavigationRoutes.NoteDetail.route + "/{index}", arguments = listOf(navArgument("index"){type = NavType.IntType})){
                backStackEntry -> backStackEntry.arguments?.let {
            val index = it.getInt("index")
            //val currentNote: Note = notesViewModel.getNote(id = index, folderTitle = parentFolder)
            NotesInsideScreen(index = index, navigator = navigator, parentFolder = parentFolder, notesViewModel = notesViewModel)//, currentNote = currentNote)
        } }
    }
}
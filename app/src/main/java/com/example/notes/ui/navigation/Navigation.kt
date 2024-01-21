package com.example.notes.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

    var backToFolderStateEnter by remember{ mutableStateOf(true) }
    var backToFolderStateExit by remember{ mutableStateOf(true) }

    NavHost(navController = navigator, startDestination = NavigationRoutes.MainScreen.route){
        composable(NavigationRoutes.MainScreen.route)
        {
            backToFolderStateEnter = true
            backToFolderStateExit = true
            MainScreen(navigator = navigator, folderViewModel = folderViewModel, notesViewModel = notesViewModel)
        }
        composable(NavigationRoutes.FolderDetail.route + "/{name}", arguments = listOf(navArgument("name"){type = NavType.StringType}),
            enterTransition = {
                if(backToFolderStateEnter) {
                    backToFolderStateEnter = false
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    ) + expandHorizontally()
                } else {
                    EnterTransition.None
                }
                              },
            exitTransition = {
                if(backToFolderStateExit) {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    )
                } else {
                    backToFolderStateExit = true
                    ExitTransition.None
                }
            }
            )
        {
                backStackEntry -> backStackEntry.arguments?.let {
            parentFolder = it.getString("name").toString()
            notesViewModel.changeParentFolder(parentFolder)
            FolderNotesScreen(
                parentFolder = parentFolder,
                navigator = navigator,
                notesViewModel = notesViewModel
            )
        } }
        composable(
            NavigationRoutes.NoteDetail.route + "/{index}",
            arguments = listOf(navArgument("index"){type = NavType.IntType}),
            enterTransition = {
                backToFolderStateEnter = true
                backToFolderStateExit = false
                slideInHorizontally(
                    initialOffsetX = {it},
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) + expandHorizontally()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {it},
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) //+ shrinkHorizontally()
            }
        )
        {
                backStackEntry -> backStackEntry.arguments?.let {
            val index = it.getInt("index")
            //val currentNote: Note = notesViewModel.getNote(id = index, folderTitle = parentFolder)
            NotesInsideScreen(index = index, navigator = navigator, parentFolder = parentFolder, notesViewModel = notesViewModel)//, currentNote = currentNote)
        } }
    }
}
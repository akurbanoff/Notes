package com.example.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.db.models.Note
import com.example.notes.ui.theme.Orange
import com.example.notes.utils.NavigationRoutes
import com.example.notes.view_models.NotesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesInsideScreen(index: Int = 999, navigator: NavHostController, parentFolder: String, notesViewModel: NotesViewModel, currentNote: Note) {
    //нужно изменить на Scaffold
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        bottomBar = { NoteInsideBottomBar()}
    ) {
        Column {
            NotesInsideTopBar(notesViewModel = notesViewModel, navigator = navigator, index = index, parentFolder = parentFolder)
            NoteBody(index = index, parentFolder = parentFolder, notesViewModel = notesViewModel, currentNote = currentNote)
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBody(modifier: Modifier = Modifier, index: Int, parentFolder: String, notesViewModel: NotesViewModel, currentNote: Note) {

    val state by notesViewModel.allNotesState.collectAsState()

    Column(
        modifier = modifier.verticalScroll(state = rememberScrollState())
    ) {
        TextField(
            value = state.title,
            onValueChange = {title ->
                notesViewModel.updateNoteTitle(title = title)
                notesViewModel.isNoteChange = true
                            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        TextField(
            value = state.textBody,
            onValueChange = {
                notesViewModel.updateNoteBody(parentFolder = parentFolder, body = it)
                notesViewModel.isNoteChange = true
                            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesInsideTopBar(parentFolder: String = "Folders", notesViewModel: NotesViewModel, navigator: NavHostController, index: Int){
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                text = parentFolder,
                color = Orange,
                modifier = Modifier.clickable {
                    if(notesViewModel.isNoteChange) {
                        notesViewModel.updateNote(parentFolder = parentFolder, id = index)
                        notesViewModel.isNoteChange = false
                    }
                    if(parentFolder != "Folders") {
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(parentFolder))
                    } else {
                        navigator.navigate(NavigationRoutes.MainScreen.route)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Orange),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(36.dp),
                )
                Image(
                    imageVector = Icons.Default.Pending,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Orange),
                    modifier = Modifier.size(36.dp)
                )
                if(notesViewModel.isNoteChange) {
                    Text(
                        text = "Done",
                        color = Orange,
                        modifier = Modifier
                            .padding(end = 8.dp, start = 8.dp)
                            .clickable {
                                notesViewModel.updateNote(parentFolder = parentFolder, id = index)
                                notesViewModel.isNoteChange = false
                                val inputMethodManager =
                                    context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(
                                    (context as Activity).currentFocus?.windowToken,
                                    0
                                )
                            },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if(notesViewModel.isNoteChange) {
                        notesViewModel.updateNote(parentFolder = parentFolder, id = index)
                        notesViewModel.isNoteChange = false
                    }
                    if(parentFolder != "Folders") {
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(parentFolder))
                    } else {
                        navigator.navigate(NavigationRoutes.MainScreen.route)
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Orange)
            }
        }
    )
}

@Composable
fun NoteInsideBottomBar() {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        containerColor = Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Checklist, contentDescription = null, tint = Orange, modifier = Modifier.size(36.dp))
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Orange, modifier = Modifier.size(36.dp))
            Icon(imageVector = Icons.Default.Draw, contentDescription = null, tint = Orange, modifier = Modifier.size(36.dp))
            Icon(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun NotesInsideScreenPreview() {
//    NotesTheme {
//        NotesInsideScreen(index = 1, navigator = rememberNavController(), title = "Test")
//    }
//}
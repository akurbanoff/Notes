package com.example.notes

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.db.models.Note
import com.example.notes.ui.theme.Orange
import com.example.notes.utils.NavigationRoutes
import com.example.notes.view_models.NotesViewModel

@Composable
fun NotesInsideScreen(index: Int = 0, navigator: NavHostController, title: String, newNote: Boolean = false, notesViewModel: NotesViewModel) {
    if (newNote) {
        notesViewModel.createNote(parentFolder = title)
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column {
            //NotesTopBar(navigator = navigator, title = title, backToFolderNotes = true, notesViewModel = notesViewModel)
            NotesInsideTopBar(title = title, notesViewModel = notesViewModel, navigator = navigator, index = index)
            NoteBody(index = index, parentFolder = title, notesViewModel = notesViewModel)
        }
        NoteInsideBottomBar()
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBody(modifier: Modifier = Modifier, index: Int, parentFolder: String, notesViewModel: NotesViewModel) {
//    var currentNote: Note = Note(id = 999, title = "", date = "", firstLine = "", textBody = "", parentFolder = )

    var currentNote = Note(
        title = "",
        date = "today",
        firstLine = "",
        textBody = "",
        parentFolder = parentFolder
    )

    val state by notesViewModel.state.collectAsState()

    if(state.notes.size < index){
        notesViewModel.createNote(parentFolder = parentFolder)
    } else {
        currentNote = notesViewModel.getNote(index)
    }

    var title: String by remember {
        mutableStateOf(currentNote.title.ifEmpty { "" })
    }
    var body : String by remember {
        mutableStateOf(currentNote.textBody.ifEmpty { "" })
    }

    Column(
        modifier = modifier
    ) {
        TextField(
            value = title.ifEmpty { "" },
            onValueChange = {
                title = it
                notesViewModel.noteTitle = title
                notesViewModel.isNoteChange = true
                            },//notesViewModel.updateNoteTitle(id = index, title = newTitle) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        TextField(
            value = body,
            //modifier = Modifier.verticalScroll(state = )
            onValueChange = {
                body = it
                notesViewModel.noteBody = body
                notesViewModel.isNoteChange = true
                            },//notesViewModel.updateNoteBody(id = index, body = newBody) },
            //label = { Text("Введите текст заметки") },
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
fun NotesInsideTopBar(title: String = "Folders", notesViewModel: NotesViewModel, navigator: NavHostController, index: Int) {
    val noteTitle = notesViewModel.noteTitle
    val noteBody = notesViewModel.noteBody

    val state by notesViewModel.state.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = title,
                color = Orange,
                modifier = Modifier.clickable {
                    val currentNote = state.notes.last()
                    if(currentNote.title.isEmpty() && currentNote.textBody.isEmpty()){
                        notesViewModel.deleteNote(currentNote.id)
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title))
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
                                notesViewModel.updateNoteTitle(title = noteTitle, id = index)
                                notesViewModel.updateNoteBody(id = index, body = noteBody)
                                notesViewModel.isNoteChange = false
                            },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    val currentNote = state.notes.last()
                    if (currentNote.title.isEmpty() && currentNote.textBody.isEmpty()) {
                        notesViewModel.deleteNote(currentNote.id)
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title))
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

//@Preview
//@Composable
//fun NITBPreview() {
//    NotesTheme {
//        NotesInsideTopBar()
//    }
//}

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
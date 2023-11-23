package com.example.notes

import android.annotation.SuppressLint
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.db.models.Note
import com.example.notes.ui.theme.Orange
import com.example.notes.view_models.NotesViewModel

@Composable
fun NotesInsideScreen(index: Int = 0, navigator: NavHostController, title: String?, newNote: Boolean = false, notesViewModel: NotesViewModel) {
    if (newNote) {
        notesViewModel.createNote(parentFolder = title!!)
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column {
            NotesTopBar(navigator = navigator, title = title, backToFolderNotes = true, notesViewModel = notesViewModel)
            NoteBody(index = index, parentFolder = title!!, notesViewModel = notesViewModel)
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
    if(notesViewModel.getAll(parentFolder = parentFolder).size < index){
        notesViewModel.createNote(parentFolder = parentFolder)
    } else {
        currentNote = notesViewModel.getNote(index)
    }

    var title: String by remember {
        mutableStateOf(if (currentNote.title.isEmpty()) "" else currentNote.title)
    }
    var body : String by remember {
        mutableStateOf(if (currentNote.textBody.isEmpty()) "" else currentNote.textBody)
    }
    Column {
        TextField(
            value = if(title.isEmpty()) "" else title,
            onValueChange = { title = it },//notesViewModel.updateNoteTitle(id = index, title = newTitle) },
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
            onValueChange = { body = it },//notesViewModel.updateNoteBody(id = index, body = newBody) },
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
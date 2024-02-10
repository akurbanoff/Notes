package com.example.notes.ui.composables

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.notes.R
import com.example.notes.db.models.Note
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.domain.sendNoteBroadcast
import com.example.notes.ui.view_models.NotesViewModel
import com.example.notes.utils.safePopBackStack

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesInsideScreen(index: Int = 999, navigator: NavHostController, parentFolder: String, notesViewModel: NotesViewModel){
    val state by notesViewModel.allNotesState.collectAsState()

    var currentNote = Note(date = "", title = "", firstLine = "", textBody = "", parentFolder = "", isDeleted = false, isShared = false, isPinned = false, time = "")

    state.allNotes.forEach {note ->
        if(note.id == index){
            currentNote = note
        }
    }

    if(parentFolder == "Recently Deleted"){
        state.deletedNotes.forEach {note ->
            if(note.id == index){
                currentNote = note
                notesViewModel.isRecoverNote = true
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        bottomBar = { NoteInsideBottomBar() }
    ) {
        Column {
            NotesInsideTopBar(notesViewModel = notesViewModel, navigator = navigator, parentFolder = parentFolder, currentNote = currentNote)
            NoteBody(parentFolder = parentFolder, notesViewModel = notesViewModel, currentNote = currentNote, navigator = navigator)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverDialog(notesViewModel: NotesViewModel, parentFolder: String, note: Note, navigator: NavHostController) {
    AlertDialog(onDismissRequest = {
        notesViewModel.openRecoverDialog = false
    }) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Recently Deleted Note",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Recently deleted notes can`t be edited.\nTo edit this note, you`ll need to recover it.",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { notesViewModel.openRecoverDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = {
                        notesViewModel.recoverNote(title = note.title)
                        notesViewModel.openRecoverDialog = false
                        navigator.safePopBackStack()
                        navigator.safePopBackStack()
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs("Notes"))
                        navigator.navigate(NavigationRoutes.NoteDetail.withArgs(note.id))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(text = "Recover", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun NoteBody(modifier: Modifier = Modifier, parentFolder: String, notesViewModel: NotesViewModel, currentNote: Note, navigator: NavHostController) {

    if(notesViewModel.openRecoverDialog){
        RecoverDialog(notesViewModel = notesViewModel, parentFolder = parentFolder, note = currentNote, navigator = navigator)
    }

    var noteTitle by remember { mutableStateOf(currentNote.title)}
    var noteBody by remember { mutableStateOf( currentNote.textBody)}
    val noteDate by remember { mutableStateOf( currentNote.date)}
     Column(
         modifier = modifier.verticalScroll(state = rememberScrollState())
     ) {
         Text(
             text = noteDate,
             color = MaterialTheme.colorScheme.secondary,
             modifier = Modifier
                 .fillMaxWidth()
                 .align(Alignment.CenterHorizontally),
             textAlign = TextAlign.Center
         )
         Spacer(modifier = Modifier.height(10.dp))
         TextField(
             value = noteTitle,
             textStyle = TextStyle(
                 fontWeight = FontWeight.Bold,
                 fontSize = 26.sp,
                 color = MaterialTheme.colorScheme.onSurface
             ),
             onValueChange = { title ->
                 if (notesViewModel.isRecoverNote) {
                     notesViewModel.openRecoverDialog = true
                 } else {
                     noteTitle = title
                     notesViewModel.updateNoteTitle(id = currentNote.id, title = noteTitle)
                     notesViewModel.isNoteChange = true
                 }
             },
             modifier = Modifier.fillMaxWidth(),
             colors = TextFieldDefaults.colors(
                 focusedContainerColor = Color.Transparent,
                 unfocusedContainerColor = Color.Transparent,
                 disabledContainerColor = Color.Transparent,
                 focusedIndicatorColor = Color.Transparent,
                 unfocusedIndicatorColor = Color.Transparent,
             )
         )
         TextField(
             value = noteBody,
             onValueChange = {
                 if (notesViewModel.isRecoverNote) {
                     notesViewModel.openRecoverDialog = true
                 } else {
                     noteBody = it
                     notesViewModel.updateNoteBody(
                         body = noteBody,
                         id = currentNote.id
                     )
                     notesViewModel.isNoteChange = true
                 }
             },
             modifier = Modifier.fillMaxWidth(),
             colors = TextFieldDefaults.colors(
                 focusedContainerColor = Color.Transparent,
                 unfocusedContainerColor = Color.Transparent,
                 disabledContainerColor = Color.Transparent,
                 focusedIndicatorColor = Color.Transparent,
                 unfocusedIndicatorColor = Color.Transparent,
             )
         )
     }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesInsideTopBar(parentFolder: String = "Folders", notesViewModel: NotesViewModel, navigator: NavHostController, currentNote: Note){
    val context = LocalContext.current
    var showMenu by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            Text(
                text = parentFolder,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    if(notesViewModel.isNoteChange) {
                        notesViewModel.isNoteChange = false
                    }
                    navigator.safePopBackStack()
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            Box {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clickable {
                                sendNoteBroadcast(
                                    context = context,
                                    title = currentNote.title,
                                    textBody = currentNote.textBody
                                )
                            },
                    )
                    Image(
                        imageVector = Icons.Default.Pending,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { showMenu = !showMenu }
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ){
                                Column(
                                    modifier = Modifier.clickable { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() }
                                ){
                                    Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null)
                                    Text(text = "Scan")
                                }
                                Column(
                                    modifier = Modifier.clickable { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() }
                                ) {
                                    Icon(imageVector = Icons.Default.PushPin, contentDescription = null)
                                    Text(text = "Pin")
                                }
                                Column(
                                    modifier = Modifier.clickable { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() }
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                                    Text(text = "Lock")
                                }
                            }
                               }, onClick = {},
                    )
                    DropdownMenuItem(text = { Text(text = "Find in Note") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null)})
                    DropdownMenuItem(text = { Text(text = "Move Note") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null)})
                    DropdownMenuItem(text = { Text(text = "Lines & Grids") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.CalendarViewMonth, contentDescription = null)})
                    DropdownMenuItem(text = { Text(text = "Attachment View") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = null)})
                    DropdownMenuItem(text = { Text(text = "Use Light Background") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.Contrast, contentDescription = null)})
                    DropdownMenuItem(text = { Text(text = "Delete") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                        trailingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null)})
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if(notesViewModel.isNoteChange) {
                        notesViewModel.isNoteChange = false
                    }
                    navigator.safePopBackStack()
                }
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun NoteInsideBottomBar() {
    val context = LocalContext.current
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
            Icon(
                imageVector = Icons.Default.Checklist,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        Toast
                            .makeText(context, "This function is developing", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        Toast
                            .makeText(context, "This function is developing", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            Icon(
                imageVector = Icons.Default.Draw,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        Toast
                            .makeText(context, "This function is developing", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            Icon(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
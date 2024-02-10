package com.example.notes.ui.composables

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.notes.R
import com.example.notes.db.models.Note
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.domain.sendNoteBroadcast
import com.example.notes.ui.composables.main_screen_modes.RenameDialog
import com.example.notes.ui.composables.main_screen_modes.SearchBar
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import com.example.notes.utils.safePopBackStack

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FolderNotesScreen(parentFolder: String, navigator: NavHostController, notesViewModel: NotesViewModel, folderViewModel: FolderViewModel) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        bottomBar = { NotesBottomBar(parentFolder = parentFolder, notesViewModel = notesViewModel) }
    ) {
        Column{
            NotesTopBar(navigator = navigator, notesViewModel = notesViewModel, parentFolder = parentFolder, folderViewModel = folderViewModel)
            Text(
                text = parentFolder,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            SearchBar()
            NotesList(
                navigator = navigator,
                modifier = Modifier.padding(top = 10.dp),
                notesViewModel = notesViewModel,
                parentFolder = parentFolder,
                asGallery = notesViewModel.asGallery
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    navigator: NavHostController,
    parentFolder: String,
    notesViewModel: NotesViewModel,
    folderViewModel: FolderViewModel
) {

    var openNotesAndSharedPending by remember {mutableStateOf(false)}
    var openAlliCloudPending by remember {mutableStateOf(false)}
    var openDefaultPending by remember {mutableStateOf(false)}

    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = "Folders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
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
                    when (parentFolder) {
                        "Recently Deleted" -> Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { }
                        )
                        "Notes" -> Icon(
                            imageVector = Icons.Outlined.Pending,
                            contentDescription = null,
                            tint = if (openNotesAndSharedPending) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { openNotesAndSharedPending = true }
                        )
                        "Shared" -> Icon(
                            imageVector = Icons.Outlined.Pending,
                            contentDescription = null,
                            tint = if (openNotesAndSharedPending) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { openNotesAndSharedPending = true }
                        )
                        "All iCloud" -> Icon(
                            imageVector = Icons.Outlined.Pending,
                            contentDescription = null,
                            tint = if (openAlliCloudPending) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { openAlliCloudPending = true }
                        )
                        else -> {
                            Row {
//                        Icon(
//                            imageVector = Icons.Default.IosShare, contentDescription = null,
//                            tint = Orange,
//                            modifier = Modifier.size(36.dp)
//                        )
//                        Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Pending,
                                    contentDescription = null,
                                    tint = if (openDefaultPending) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { openDefaultPending = true }
                                )
                            }
                        }
                    }
                    if (openDefaultPending){
                        DropdownMenu(
                            expanded = openDefaultPending,
                            onDismissRequest = { openDefaultPending = false },
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            if(notesViewModel.asGallery){
                                DropdownMenuItem(
                                    text = { Text(text = "View as List") },
                                    onClick = { notesViewModel.asGallery = false },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = null
                                        )
                                    })
                            } else {
                                DropdownMenuItem(
                                    text = { Text(text = "View as Gallery") },
                                    onClick = { notesViewModel.asGallery = true },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null
                                        )
                                    })
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Share Folder") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.PersonPin, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Add Folder") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.CreateNewFolder, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Move This Folder") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Rename") }, onClick = { folderViewModel.openRenameDialog = true },
                                trailingIcon = {Icon(imageVector = Icons.Default.Edit, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Select Notes") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Sort By") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Group By Date") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "View Attachments") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.AttachFile, contentDescription = null)})
                        }
                        if (folderViewModel.openRenameDialog) {
                            RenameDialog(
                                currentName = parentFolder,
                                id = folderViewModel.getFolderId(parentFolder),
                                folderViewModel = folderViewModel
                            )
                        }
                    } else if (openAlliCloudPending){
                        DropdownMenu(
                            expanded = openAlliCloudPending,
                            onDismissRequest = { openAlliCloudPending = false },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            if(notesViewModel.asGallery){
                                DropdownMenuItem(
                                    text = { Text(text = "View as List") },
                                    onClick = { notesViewModel.asGallery = false },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = null
                                        )
                                    })
                            } else {
                                DropdownMenuItem(
                                    text = { Text(text = "View as Gallery") },
                                    onClick = { notesViewModel.asGallery = true },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null
                                        )
                                    })
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Select Notes") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "View Attachments") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.AttachFile, contentDescription = null)})
                        }
                    } else if (openNotesAndSharedPending){
                        DropdownMenu(
                            expanded = openNotesAndSharedPending,
                            onDismissRequest = { openNotesAndSharedPending = false },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            if(notesViewModel.asGallery){
                                DropdownMenuItem(
                                    text = { Text(text = "View as List") },
                                    onClick = { notesViewModel.asGallery = false },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = null
                                        )
                                    })
                            } else {
                                DropdownMenuItem(
                                    text = { Text(text = "View as Gallery") },
                                    onClick = { notesViewModel.asGallery = true },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null
                                        )
                                    })
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Select Notes") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Sort By") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "Group By Date") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)})
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = "View Attachments") }, onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                                trailingIcon = { Icon(imageVector = Icons.Default.AttachFile, contentDescription = null)})
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navigator.safePopBackStack() }
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun NotesBottomBar(modifier: Modifier = Modifier, parentFolder: String, notesViewModel: NotesViewModel) {//title: String?
    val state by notesViewModel.allNotesState.collectAsState()
    val countNotes = when(parentFolder){
        "Shared" -> state.sharedNotes.size
        "All iCloud" -> state.allNotes.size
        "Notes" -> {
            val _state by notesViewModel.allInNotes.collectAsState()
            _state.size
        }
        "Recently Deleted" -> state.deletedNotes.size
        else -> state.notes.size
    }

    if(notesViewModel.openCreateNoteDialog){
        AddNoteDialog(notesViewModel = notesViewModel, parentFolder = parentFolder)
    }

    BottomAppBar(
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = if(countNotes > 0) "$countNotes Notes" else "No Notes",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        notesViewModel.openCreateNoteDialog = true
                    }
            )
        }
    }
}

@Composable
fun AddNoteDialog(notesViewModel : NotesViewModel, parentFolder: String) {
    var newNoteTitle by remember {mutableStateOf("")}
    AlertDialog(
        onDismissRequest = {notesViewModel.openCreateNoteDialog = false},
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ){
                            Button(onClick = {
                                if(newNoteTitle.isNotEmpty()) {
                                    notesViewModel.createNote(
                                        parentFolder = parentFolder,
                                        title = newNoteTitle
                                    )
                                }
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if(newNoteTitle.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                                ) {
                                Text(text = "Create Note")
                            }
                        }
        },
        title = { Text(text = "Add note")},
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = newNoteTitle,
                    onValueChange = {
                                    newNoteTitle = it
                    },
                    placeholder = { Text(text = "Note title")},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Image(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.clickable { newNoteTitle = "" }
                        )
                    },
                )
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesList(
    parentFolder: String, navigator: NavHostController,
    modifier: Modifier = Modifier, notesViewModel: NotesViewModel,
    asGallery: Boolean = false
) {
    val state by notesViewModel.allNotesState.collectAsState()

    val notesList = when(parentFolder){
        "Recently Deleted" -> state.deletedNotes
        "All iCloud" -> state.allNotes
        else -> state.notes
    }

    val pinnedNotes by notesViewModel.getPinnedNotes(parentFolder).collectAsState()
    val isPinnedNotesExists by remember{ mutableStateOf(pinnedNotes.isNotEmpty())}

    if(asGallery){
        Column {
            if(isPinnedNotesExists){
                Text(text = "Pinned")
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(pinnedNotes) { note ->
                        NoteGallery(
                            note = note,
                            navigator = navigator,
                            notesViewModel = notesViewModel,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Text(text = "Notes")
            }
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(notesList) { item ->
                    NoteGallery(
                        note = item,
                        modifier = Modifier.padding(16.dp),
                        navigator = navigator,
                        notesViewModel = notesViewModel
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
        ) {
            LazyColumn {
                if(isPinnedNotesExists){
                    item {
                        Text(text = "Pinned")
                    }
                    items(pinnedNotes){note ->
                        Note(
                            note = note,
                            navigator = navigator,
                            notesViewModel = notesViewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        Text(text = "Notes")
                    }
                }
                itemsIndexed(notesList) { _, item ->
                    Note(
                        note = item,
                        navigator = navigator,
                        notesViewModel = notesViewModel,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteGallery(note: Note, modifier: Modifier = Modifier, navigator: NavHostController, notesViewModel: NotesViewModel) {
    var clicked by remember {
        mutableStateOf(false)
    }
    var showMenu by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Column(
        modifier = modifier.combinedClickable(
            onClick = {
                clicked = true
                navigator.navigate(NavigationRoutes.NoteDetail.withArgs(note.id))
            },
            onLongClick = {
                showMenu = true
            }
        )
    ) {
        Text(
            text = note.date,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(60.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.tertiary)
                .border(
                    color = if (clicked) MaterialTheme.colorScheme.primary else Color.Transparent,
                    width = 4.dp,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Text(
                text = note.title,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = note.textBody,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = note.title,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    if(showMenu) {
        AlertDialog(
            onDismissRequest = { showMenu = !showMenu },
            modifier = Modifier.clickable { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(note.id)) },
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {},
            properties = DialogProperties(),
            title = {},
            text = {
                Box {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = note.textBody,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val isNotePinned = note.isPinned
                    DropdownMenu(
                        expanded = showMenu,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surface),
                        onDismissRequest = { showMenu = !showMenu }) {
                        DropdownMenuItem(
                            text = { Text(text = if(!isNotePinned) "Pin Note" else "Unpin Note") },
                            onClick = { notesViewModel.pinNote(id = note.id, pin = !isNotePinned) },
                            trailingIcon = { Icon(imageVector = Icons.Default.PushPin, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Lock Note") },
                            onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                            trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Share Note") },
                            onClick = { sendNoteBroadcast(context = context, title = note.title, textBody = note.textBody) },
                            trailingIcon = { Icon(imageVector = Icons.Default.IosShare, contentDescription = null, modifier = Modifier.clickable {
                                sendNoteBroadcast(context = context, title = note.title, textBody = note.textBody)
                            }) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Move") },
                            onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = {
                                Text("Delete", color = Color.Red)
                            },
                            onClick = {
                                showMenu = false
                                notesViewModel.deleteNote(note.id)
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(note: Note, navigator: NavHostController, notesViewModel: NotesViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(note.id)) },
                onLongClick = { showMenu = true }
            )
            .shadow(elevation = 6.dp, shape = MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.tertiary),
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = note.date,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = if (note.textBody.length > 20) {
                        "${note.textBody.take(20)}..."
                    } else if(note.textBody.isEmpty()){
                        "No additional text"
                    } else {
                        note.textBody
                    },
                    modifier = Modifier.padding(start = 5.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
    if(showMenu) {
        AlertDialog(
            onDismissRequest = { showMenu = !showMenu },
            modifier = Modifier.clickable { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(note.id)) },
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {},
            properties = DialogProperties(),
            title = {},
            text = {
                Box {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = note.textBody,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val isNotePinned by remember { mutableStateOf(note.isPinned) }
                    DropdownMenu(
                        expanded = showMenu,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surface),
                        onDismissRequest = { showMenu = !showMenu }) {
                        DropdownMenuItem(
                            text = { Text(text = if(!isNotePinned) "Pin Note" else "Unpin Note") },
                            onClick = { notesViewModel.pinNote(id = note.id, pin = !isNotePinned) },
                            trailingIcon = { Icon(imageVector = Icons.Default.PushPin, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Lock Note") },
                            onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                            trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Share Note") },
                            onClick = { sendNoteBroadcast(context = context, title = note.title, textBody = note.textBody) },
                            trailingIcon = { Icon(imageVector = Icons.Default.IosShare, contentDescription = null, modifier = Modifier.clickable {
                                sendNoteBroadcast(context = context, title = note.title, textBody = note.textBody)
                            }) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = "Move") },
                            onClick = { Toast.makeText(context, "This function is developing", Toast.LENGTH_SHORT).show() },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = {
                                Text("Delete", color = Color.Red)
                            },
                            onClick = {
                                showMenu = false
                                notesViewModel.deleteNote(note.id)
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}
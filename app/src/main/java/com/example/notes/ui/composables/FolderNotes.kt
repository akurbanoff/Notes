package com.example.notes.ui.composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.R
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.domain.sendNoteBroadcast
import com.example.notes.ui.composables.main_screen_modes.SearchBar
import com.example.notes.ui.view_models.NotesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderNotesScreen(parentFolder: String, navigator: NavHostController, notesViewModel: NotesViewModel) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        bottomBar = { NotesBottomBar(parentFolder = parentFolder, navigator = navigator, notesViewModel = notesViewModel) }
    ) {
        Column{
            NotesTopBar(navigator = navigator, notesViewModel = notesViewModel, parentFolder = parentFolder)
            Text(
                text = parentFolder,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            SearchBar()
            NotesList(navigator = navigator, modifier = Modifier.padding(top = 10.dp), notesViewModel = notesViewModel, parentFolder = parentFolder)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    navigator: NavHostController,
    parentFolder: String,
    notesViewModel: NotesViewModel
) {
    val state by notesViewModel.allNotesState.collectAsState()

    var openNotesAndSharedPending by remember {mutableStateOf(false)}
    var openAlliCloudPending by remember {mutableStateOf(false)}
    var openDefaultPending by remember {mutableStateOf(false)}

    val isDeletedFolder by remember {
        mutableStateOf(parentFolder == "Recently Deleted")
    }

    Log.d("parent", parentFolder)

    TopAppBar(
        title = {
            Text(
                text = "Folders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { navigator.popBackStack() }
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
                            DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Share Folder") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Add Folder") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Move This Folder") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Rename") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Convert to Smart Folder") }, onClick = { /*TODO*/ })
                        }
                    } else if (openAlliCloudPending){
                        DropdownMenu(
                            expanded = openAlliCloudPending,
                            onDismissRequest = { openAlliCloudPending = false },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
                        }
                    } else if (openNotesAndSharedPending){
                        DropdownMenu(
                            expanded = openNotesAndSharedPending,
                            onDismissRequest = { openNotesAndSharedPending = false },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            DropdownMenuItem(text = { Text(text ="View as Gallery") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
                            Divider()
                            DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navigator.popBackStack() }
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
//
//@Composable
//fun AlliCloudPending(notesViewModel: NotesViewModel) {
//    DropdownMenu(
//        expanded = notesViewModel.openAlliCloudPending,
//        onDismissRequest = { notesViewModel.openAlliCloudPending = false },
//        modifier = Modifier.clip(MaterialTheme.shapes.small)
//    ) {
//        DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
//    }
//}
//
//@Composable
//fun NotesAndSharedPending(notesViewModel: NotesViewModel) {
//    DropdownMenu(
//        expanded = notesViewModel.openNotesAndSharedPending,
//        onDismissRequest = { notesViewModel.openNotesAndSharedPending = false },
//        modifier = Modifier.clip(MaterialTheme.shapes.small)
//    ) {
//        DropdownMenuItem(text = { Text(text ="View as Gallery") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
//    }
//}
//
//@Composable
//fun DefaultPending(notesViewModel: NotesViewModel) {
//    DropdownMenu(
//        expanded = notesViewModel.openDefaultPending,
//        onDismissRequest = { notesViewModel.openDefaultPending = false },
//        modifier = Modifier
//            .clip(MaterialTheme.shapes.small)
//            .background(MaterialTheme.colorScheme.surface)
//    ) {
//        DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Share Folder") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Add Folder") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Move This Folder") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Rename") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
//        Divider()
//        DropdownMenuItem(text = { Text(text = "Convert to Smart Folder") }, onClick = { /*TODO*/ })
//    }
//}

@Composable
fun NotesBottomBar(modifier: Modifier = Modifier, parentFolder: String, navigator: NavHostController, notesViewModel: NotesViewModel) {//title: String?
    val state by notesViewModel.allNotesState.collectAsState()
    val countNotes = when(parentFolder){
        "Shared" -> state.sharedNotes.size
        "All iCloud" -> state.allNotes.size
        "Notes" -> {
            val state by notesViewModel.allInNotes.collectAsState()
            state.size
        }
        "Recently Deleted" -> state.deletedNotes.size
        else -> state.notes.size
    }

    if(notesViewModel.openCreateNoteDialog){
//        notesViewModel.opensDialog()
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
                        //navigator.navigate(NavigationRoutes.NewNote.route)
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(notesViewModel : NotesViewModel, parentFolder: String) {
    val state by notesViewModel.allNotesState.collectAsState()
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


@Composable
fun NotesList(parentFolder: String, navigator: NavHostController, modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val state by notesViewModel.allNotesState.collectAsState()

    val notesList = when(parentFolder){
        "Recently Deleted" -> state.deletedNotes
        "All iCloud" -> state.allNotes
        else -> state.notes
    }

    Column(
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
    ) {
        LazyColumn{
            itemsIndexed(notesList){_, item ->
                Note(title = item.title, date = item.date, firstLine = item.firstLine, index = item.id, navigator = navigator, notesViewModel = notesViewModel, textBody = item.textBody)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(title: String, date: String, firstLine: String, index: Int, navigator: NavHostController, notesViewModel: NotesViewModel, textBody: String) {
    var showMenu by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(index)) },
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
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = if (firstLine.length > 30) {
                        "${firstLine.take(30)}..."
                    } else {
                        firstLine
                    },
                    modifier = Modifier.padding(start = 5.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

    val context = LocalContext.current
    val currentNote = notesViewModel.getNote(title)

    DropdownMenu(
        expanded = showMenu,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface),
        onDismissRequest = { showMenu = !showMenu }) {
        DropdownMenuItem(
            text = { Text(text = "Pin Note") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.PushPin, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Lock Note") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Share Note") },
            onClick = { sendNoteBroadcast(context = context, title = title, textBody = textBody) },
            trailingIcon = { Icon(imageVector = Icons.Default.IosShare, contentDescription = null, modifier = Modifier.clickable {
                sendNoteBroadcast(context = context, title = currentNote.title, textBody = currentNote.textBody)
            }) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Move") },
            onClick = { /*TODO*/ },
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
                notesViewModel.deleteNote(index)
                showMenu = false
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

//@Preview(showBackground = true)
//@Composable
//fun NotePreview() {
//    NotesTheme {
//        Note(title = "Test", date = "10/01/01", firstLine = "How to implement something new for car bird something special at all", index = 0, navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun FolderNotesScreenPreview() {
//    NotesTheme {
//        FolderNotesScreen(title = "Test", navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun BotBarPreview() {
//    NotesTheme {
//        NotesBottomBar(navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun TopBarPreview() {
//    NotesTheme {
//        NotesTopBar(navigator = rememberNavController())
//    }
//}
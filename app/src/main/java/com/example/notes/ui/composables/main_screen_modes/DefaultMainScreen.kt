package com.example.notes.ui.composables.main_screen_modes

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.notes.db.models.Folder
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import com.example.notes.utils.DefaultFolders
import com.example.notes.utils.collectFoldersName
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.FlexibleSheetState
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DefaultMainScreen(folderViewModel: FolderViewModel, notesViewModel: NotesViewModel, navigator: NavHostController) {
    var isSearchBarVisible by remember{ mutableStateOf(true) }
    val notesState by notesViewModel.allNotesState.collectAsState()

    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        topBar = { TopBar(isEditModeStart = false, folderViewModel = folderViewModel) },
        bottomBar = { BottomBar(folderViewModel = folderViewModel) }
    ) {
        Column(
            //modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Folders",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            if(isSearchBarVisible) {
                SearchBar(modifier = Modifier.padding(top = 8.dp))
            }
            val notesAmount = notesViewModel.getNotesAmount(DefaultFolders.SharedFolder.title)
            FolderElement(
                title = DefaultFolders.SharedFolder.title,
                icon = Icons.Default.PersonPin,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .shadow(elevation = 6.dp, shape = MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium),
                navigator = navigator,
                folderViewModel = folderViewModel,
                notesViewModel = notesViewModel,
                hasMenu = false,
                _notesAmount = notesAmount
            )
            FolderList(navigator = navigator, folderViewModel = folderViewModel, notesViewModel = notesViewModel)
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FolderList(modifier: Modifier = Modifier, navigator: NavHostController, folderViewModel: FolderViewModel, notesViewModel: NotesViewModel) {
    var showFolderList by remember{ mutableStateOf(true) }

    val folders by folderViewModel.folders.collectAsState()

    Column(
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
    ) {
        FolderGroupHeadline(modifier = Modifier
            .padding(top = 12.dp)
            .clickable {
                showFolderList = !showFolderList
            }, showFolderList = showFolderList)
        AnimatedVisibility(
            visible = showFolderList,
            enter = slideInVertically(animationSpec = tween(delayMillis = 50, durationMillis = 200)) + expandVertically(),
            exit = shrinkVertically(animationSpec = tween(delayMillis = 50, durationMillis = 200)) + fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                if (folders.folders.isNotEmpty()) {
                    val notesAmount = notesViewModel.getNotesAmount(DefaultFolders.AlliCloudFolder.title)
                    item {
                        FolderElement(
                            title = DefaultFolders.AlliCloudFolder.title,
                            icon = Icons.Default.FolderOpen,
                            navigator = navigator,
                            folderViewModel = folderViewModel,
                            notesViewModel = notesViewModel,
                            hasMenu = false,
                            _notesAmount = notesAmount
                        )
                    }
                }
                item {
                    val notesAmount = notesViewModel.getNotesAmount(DefaultFolders.NotesFolder.title)
                    FolderElement(
                        title = DefaultFolders.NotesFolder.title,
                        icon = Icons.Default.FolderOpen,
                        navigator = navigator,
                        folderViewModel = folderViewModel,
                        notesViewModel = notesViewModel,
                        hasMenu = false,
                        _notesAmount = notesAmount
                    )
                }

                itemsIndexed(folders.folders) { _, item ->
                    val notesAmount = notesViewModel.getNotesAmount(item.title)
                    FolderElement(
                        id = item.id,
                        title = item.title,
                        icon = Icons.Default.FolderOpen,
                        navigator = navigator,
                        folderViewModel = folderViewModel,
                        notesViewModel = notesViewModel,
                        hasMenu = true,
                        _notesAmount = notesAmount
                        //notesAmount = notesViewModel.getNotesAmount(item.title)
                    )
                }
                item{
                    val notesAmount = notesViewModel.getNotesAmount(DefaultFolders.RecentlyDeletedFolder.title)
                    FolderElement(
                        title = DefaultFolders.RecentlyDeletedFolder.title,
                        icon = Icons.Outlined.Delete,
                        navigator = navigator,
                        folderViewModel = folderViewModel,
                        notesViewModel = notesViewModel,
                        hasMenu = false,
                        _notesAmount = notesAmount
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FolderElement(
    modifier: Modifier = Modifier, id: Int = 999, title: String, icon: ImageVector,
    navigator: NavHostController, folderViewModel: FolderViewModel,
    hasMenu: Boolean, notesViewModel: NotesViewModel, _notesAmount: StateFlow<Int>
){
    var showMenu by remember{ mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title)) },
                onLongClick = {
                    showMenu = true
                }
            )
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val notesAmount by _notesAmount.collectAsState()
                Log.d("folder", "$title - ${notesAmount.toString()}")
                Text(
                    text = notesAmount.toString(), //количество заметок в этой папке
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
    val notes by notesViewModel.getNotes(title).collectAsState()
    if(showMenu && hasMenu) {
        AlertDialog(
            modifier = Modifier.clickable { navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title)) },
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { showMenu = !showMenu },
            properties = DialogProperties(),
            confirmButton = {},
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = AbsoluteAlignment.Left
                ) {
                    Text(text = title)
                    Text(text = if (notes.isNotEmpty()) "${notes.size} Notes" else "No Notes")
                }
            },
            text = {
                Box() {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        userScrollEnabled = false,
                        modifier = Modifier
                    ) {
                        items(if (notes.size < 4) notes else notes.take(4)) { note ->
                            PreviewWindowElement(
                                title = note.title,
                                textBody = note.textBody,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu && hasMenu,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surface),
                        onDismissRequest = { showMenu = !showMenu }
                    ) {
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
                            text = { Text(text = "Rename") },
                            onClick = { folderViewModel.openRenameDialog = true },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = null
                                )
                            }
                        )
                        Divider()
                        if (folderViewModel.openRenameDialog) {
                            RenameDialog(
                                currentName = title,
                                id = id,
                                folderViewModel = folderViewModel
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text("Delete", color = Color.Red)
                            },
                            onClick = {
                                folderViewModel.deleteFolder(title)
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
            }
        )
    }
}

@Composable
fun PreviewWindowElement(modifier: Modifier = Modifier, title: String, textBody: String) {
    Box(
        modifier = modifier.size(height = 120.dp, width = 100.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.tertiary).padding(8.dp)
    ) {
        Column {
            Text(text = title)
            Text(text = textBody)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialog(id: Int, currentName: String, folderViewModel: FolderViewModel) {
    val state by folderViewModel.folders.collectAsState()
    var currentFolder = Folder(title = "")

    state.folders.forEach {
        if(it.id == id){
            currentFolder = it
        }
    }

    var newFolderTitle by remember{ mutableStateOf(currentFolder.title) }
    //val isNewFolderTitleEmpty by remember{ mutableStateOf(newFolderTitle.isEmpty()) }
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Text(
                text = "Rename Folder",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        onDismissRequest = { folderViewModel.openRenameDialog = false },
        confirmButton = {},
        text = {
            Column {
                TextField(
                    value = newFolderTitle,
                    onValueChange = { newFolderTitle = it },
                    shape = MaterialTheme.shapes.medium,
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
                            modifier = Modifier.clickable { newFolderTitle = "" }
                        )
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { folderViewModel.openRenameDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                    if(newFolderTitle.isEmpty()){
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Text(text = "Save", color = Color.Gray)
                        }
                    } else {
                        Button(
                            onClick = {
                                folderViewModel.renameFolder(
                                    id = id,
                                    oldTitle = currentName,
                                    newTitle = newFolderTitle
                                )
                                folderViewModel.openRenameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Text(text = "Save", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BottomBar(modifier: Modifier = Modifier, folderViewModel: FolderViewModel) {
    val sheetState = rememberFlexibleBottomSheetState(
        isModal = false,
        flexibleSheetSize = FlexibleSheetSize(
            fullyExpanded = 0.85f,
            slightlyExpanded = 0f,
            intermediatelyExpanded = 0f
        )
    )

    val scope = rememberCoroutineScope()

    if (folderViewModel.openFolderDialog) {
        CreateFolderAlertDialog(folderViewModel = folderViewModel)
    }
        //scope.launch { sheetState.show(target = FlexibleSheetValue.FullyExpanded) }
//    } else {
//        //scope.launch { sheetState.hide() }
//    }

    BottomAppBar(
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth()
            .padding(8.dp),
        containerColor = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                imageVector = Icons.Default.CreateNewFolder,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        //открывается Dialog и создается Folder
                        folderViewModel.openFolderDialog = true
                    },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        //CreateFolderDialog(folderViewModel = folderViewModel, sheetState = sheetState)
    }
}
@Composable
fun CreateFolderAlertDialog(folderViewModel: FolderViewModel) {
    var countEmptyFolders by remember {mutableStateOf(1)}
    val folders by folderViewModel.folders.collectAsState()

    for (folder in folders.folders) {
        if (folder.title.contains("New Folder")) {
            countEmptyFolders += 1
        }
    }

    var folderName by remember {
        mutableStateOf("New Folder $countEmptyFolders")
    }

    AlertDialog(
        onDismissRequest = { folderViewModel.openFolderDialog = false },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { folderViewModel.openFolderDialog = false }
                    )
                    Text(
                        text = "New Folder",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Done",
                        color = if(folderName.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.clickable {
                            if(folderName.isNotEmpty()) {
                                folderViewModel.createFolder(Folder(title = folderName))
                                folderViewModel.openFolderDialog = false
                            }
                        }
                    )
                }
                TextField(
                    value = folderName,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        folderName = it
                        //folderViewModel.updateFolder(newTitle = folderName)
                    },
                    trailingIcon = {
                        if(folderName.isNotEmpty()) {
                            Image(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.clickable { folderName = "" },
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(modifier: Modifier = Modifier, folderViewModel: FolderViewModel, sheetState: FlexibleSheetState) {
    var countEmptyFolders by remember {mutableStateOf(1)}
    //val state by folderViewModel.folderState.collectAsState()
    val folders by folderViewModel.folders.collectAsState()

    for (folder in folders.folders) {
        if (folder.title.contains("New Folder")) {
            countEmptyFolders += 1
        }
    }

    var folderName by remember {
        mutableStateOf("New Folder $countEmptyFolders")
    }

    //val folderId = folderViewModel.createFolder(Folder(title = folderName))

    FlexibleBottomSheet(
        onDismissRequest = { folderViewModel.openFolderDialog = false },
        sheetState = sheetState,
    ) {
        Column(
            modifier = modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { folderViewModel.openFolderDialog = false }
                )
                Text(
                    text = "New Folder",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Done",
                    color = if(folderName.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.clickable {
                        if(folderName.isNotEmpty()) {
                            folderViewModel.createFolder(Folder(title = folderName))
                            folderViewModel.openFolderDialog = false
                        }
                    }
                )
            }
            TextField(
                value = folderName,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    folderName = it
                    folderViewModel.updateFolder(newTitle = folderName)
                },
                trailingIcon = {
                    if(folderName.isNotEmpty()) {
                        Image(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.clickable { folderName = "" }
                        )
                    }
                },
                shape = MaterialTheme.shapes.small,
                colors = TextFieldDefaults.colors(

                )
            )
        }
    }
}
package com.example.notes.ui.composables.main_screen_modes

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.db.models.Folder
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.FlexibleSheetState
import com.skydoves.flexible.core.FlexibleSheetValue
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DefaultMainScreen(folderViewModel: FolderViewModel, notesViewModel: NotesViewModel, navigator: NavHostController, sharedFolder: Folder) {
    var isSearchBarVisible by remember{ mutableStateOf(true) }
    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        topBar = { TopBar(isEditModeStart = false, folderViewModel = folderViewModel) },
        bottomBar = { BottomBar(folderViewModel = folderViewModel, navigator = navigator) }
    ) {
        Column(
            //modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
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
            FolderElement(
                title = sharedFolder.title,
                icon = Icons.Default.PersonPin,
                modifier = Modifier.padding(top = 20.dp),
                navigator = navigator,
                folderViewModel = folderViewModel,
                notesViewModel = notesViewModel,
                hasMenu = false
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
        if(showFolderList) {
            if(folders.folders.isNotEmpty()){
                FolderElement(
                    title = "All iCloud",
                    icon = Icons.Default.FolderOpen,
                    navigator = navigator,
                    folderViewModel = folderViewModel,
                    notesViewModel = notesViewModel,
                    hasMenu = false
                )
            }
            FolderElement(
                title = "Notes",
                icon = Icons.Default.FolderOpen,
                navigator = navigator,
                folderViewModel = folderViewModel,
                notesViewModel = notesViewModel,
                hasMenu = false
            )
            LazyColumn{
                itemsIndexed(folders.folders) { _, item ->
                    FolderElement(
                        id = item.id,
                        title = item.title,
                        icon = Icons.Default.FolderOpen,
                        navigator = navigator,
                        folderViewModel = folderViewModel,
                        notesViewModel = notesViewModel,
                        hasMenu = true
                    )
                }
            }
            FolderElement(
                title = "Recently Deleted",
                icon = Icons.Outlined.Delete,
                navigator = navigator,
                folderViewModel = folderViewModel,
                notesViewModel = notesViewModel,
                hasMenu = false
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderElement(modifier: Modifier = Modifier, id: Int = 999, title: String, icon: ImageVector,
                  navigator: NavHostController, folderViewModel: FolderViewModel,
                  hasMenu: Boolean, notesViewModel: NotesViewModel
){
    var showMenu by remember {
        mutableStateOf(false)
    }

    //notesViewModel.changeParentFolder(newParentFolder = title)
    val notesState by notesViewModel.allNotesState.collectAsState()

    if(folderViewModel.openRenameDialog){
        RenameDialog(currentName = title, id = id, folderViewModel = folderViewModel)
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title)) },
                onLongClick = {
                    showMenu = true
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.small
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
                modifier = Modifier.padding(start = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = notesViewModel.getNotesAmount(title).toString(), //количество заметок в этой папке
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.secondary
                )
                Image(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
    DropdownMenu(
        expanded = showMenu && hasMenu,
        modifier = Modifier.clip(MaterialTheme.shapes.small),
        onDismissRequest = {showMenu = !showMenu}) {
//        DropdownMenuItem(
//            text = { Text(text = "Share Folder") },
//            onClick = { /*TODO*/ },
//            trailingIcon = { Icon(imageVector = Icons.Default.IosShare, contentDescription = null) }
//        )
//        Divider()
        DropdownMenuItem(
            text = { Text(text = "Move") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Rename") },
            onClick = { folderViewModel.openRenameDialog = true },
            trailingIcon = { Icon(imageVector = Icons.Default.Create, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = {
                Text("Delete", color = Color.Red)
            },
            onClick = {
                folderViewModel.deleteFolder(title)
                showMenu = false
            },
            trailingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialog(id: Int, currentName: String, folderViewModel: FolderViewModel) {
    var newFolderTitle by remember{ mutableStateOf(currentName) }
    //val isNewFolderTitleEmpty by remember{ mutableStateOf(newFolderTitle.isEmpty()) }
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
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
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
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
fun BottomBar(modifier: Modifier = Modifier, folderViewModel: FolderViewModel, navigator: NavHostController) {
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
fun CreateFolderAlertDialog(folderViewModel: FolderViewModel, ) {
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
package com.example.notes.ui.composables.main_screen_modes

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import com.example.notes.utils.DefaultFolders
import com.example.notes.utils.collectFoldersName
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditMainScreen(folderViewModel: FolderViewModel) {
    var isSearchBarVisible by remember{ mutableStateOf(true) }

    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        topBar = { TopBar(isEditModeStart = true, folderViewModel = folderViewModel) },
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            if (isSearchBarVisible) {
                SearchBar(modifier = Modifier.padding(top = 8.dp))
            }
            EditFolderElement(
                title = DefaultFolders.SharedFolder.title,
                icon = Icons.Default.PersonPin,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .shadow(elevation = 6.dp, shape = MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
            )
            EditFolderList(folderViewModel = folderViewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditFolderList(modifier: Modifier = Modifier, folderViewModel: FolderViewModel) {
    var showFolderList by remember{ mutableStateOf(true) }
    
    val folders by folderViewModel.folders.collectAsState()

    val lazyListState = rememberLazyListState()

    var list by remember{ mutableStateOf(folders.folders) }

    val dragState = rememberReorderableLazyColumnState(lazyListState = lazyListState,
        onMove = {from, to ->
            val fromIndex = if(from.index > 2) {
                from.index - 2
            } else if(from.index in 1..1){
                from.index - 1
            } else 0

            val toIndex = if(to.index > 2) {
                to.index - 2
            } else if (to.index in 1..1){
                to.index - 1
            } else 0

            list = list.toMutableList().apply {
                this.add(toIndex, removeAt(fromIndex))
            }
            folderViewModel.changeIndex(fromIndex = fromIndex, toIndex = toIndex)
        }
    )

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
            enter = slideInVertically() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                if (folders.folders.isNotEmpty()) {
                    item {
                        EditFolderElement(
                            title = DefaultFolders.AlliCloudFolder.title,
                            icon = Icons.Default.FolderOpen
                        )
                    }
                }
                item {
                    EditFolderElement(
                        title = DefaultFolders.NotesFolder.title,
                        icon = Icons.Default.FolderOpen
                    )
                }
                itemsIndexed(list, key = {_,it -> it.id}) { _, item ->
                    ReorderableItem(reorderableLazyListState = dragState, key = item.id, enabled = true) { isDragging ->
                        val elevation  = animateDpAsState(if(isDragging) 16.dp else 0.dp,
                            label = ""
                        )
                        EditFolderElement(
                            modifier = Modifier
                                .shadow(elevation.value)
                                .draggableHandle(),
                            title = item.title,
                            icon = Icons.Default.FolderOpen,
                        )
                    }
                }
                item{
                    EditFolderElement(
                        title = DefaultFolders.RecentlyDeletedFolder.title,
                        icon = Icons.Outlined.Delete
                    )
                }
            }
        }
    }
}

@Composable
fun EditFolderElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector
) {
    val isDefaultFolder by remember { mutableStateOf(title in collectFoldersName()) }
    val isSharedFolder by remember{ mutableStateOf(title == DefaultFolders.SharedFolder.title) }

    var openDefaultEditPending by remember {mutableStateOf(false)}
    var openSharedEditPending by remember {mutableStateOf(false)}

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if(!isDefaultFolder || isSharedFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                color = if(!isDefaultFolder || isSharedFolder) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onTertiary
            )
            if(!isDefaultFolder || isSharedFolder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Pending, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            if (title == DefaultFolders.SharedFolder.title) {
                                openSharedEditPending = true
                            } else {
                                openDefaultEditPending = true
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (!isSharedFolder) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DensityMedium,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    }

    DropdownMenu(
        expanded = openDefaultEditPending,
        onDismissRequest = { openDefaultEditPending = false },
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        DropdownMenuItem(text = { Text(text = "Share Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Add Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Move This Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Rename") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Delete") }, onClick = { /*TODO*/ })
    }

    DropdownMenu(
        expanded = openSharedEditPending,
        onDismissRequest = { openSharedEditPending = false },
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
    }
}
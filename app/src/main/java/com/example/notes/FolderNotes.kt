package com.example.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import com.example.notes.ui.theme.Orange
import com.example.notes.utils.NavigationRoutes
import com.example.notes.view_models.NotesViewModel

@Composable
fun FolderNotesScreen(title: String?, navigator: NavHostController, notesViewModel: NotesViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            NotesTopBar(navigator = navigator, notesViewModel = notesViewModel)
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            SearchBar()
            NotesList(navigator = navigator, modifier = Modifier.padding(top = 10.dp), notesViewModel = notesViewModel, folder = title!!)
        }
        NotesBottomBar(navigator = navigator, notesViewModel = notesViewModel, folder = title!!)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    navigator: NavHostController,
    title: String = "Folders",
    notesViewModel: NotesViewModel
) {
    val state by notesViewModel.state.collectAsState()
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Orange,
                modifier = Modifier.clickable {
                    if(title != "Folders") {
                        val currentNote = state.notes.last()
                        if (currentNote.title.isEmpty() && currentNote.textBody.isEmpty()) {
                            notesViewModel.deleteNote(currentNote.id)
                        }
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
                    imageVector = Icons.Default.Pending,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Orange),
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if(title != "Folders") {
                        val currentNote = state.notes.last()
                        if (currentNote.title.isEmpty() && currentNote.textBody.isEmpty()) {
                            notesViewModel.deleteNote(currentNote.id)
                        }
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

@Composable
fun NotesBottomBar(modifier: Modifier = Modifier, folder: String, navigator: NavHostController, notesViewModel: NotesViewModel) {//title: String?
    val state by notesViewModel.state.collectAsState()
    val countNotes = state.notes.size
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
                text = "$countNotes Notes",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            Icon(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = Orange,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        navigator.navigate(NavigationRoutes.NewNote.route)
                    }
            )
        }
    }
}


@Composable
fun NotesList(folder: String, navigator: NavHostController, modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val state by notesViewModel.state.collectAsState()
    Column(
        modifier = modifier
    ) {
        LazyColumn{
            itemsIndexed(state.notes){index, item ->
                Note(title = item.title, date = item.date, firstLine = item.firstLine, index = index, navigator = navigator, notesViewModel = notesViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(title: String, date: String, firstLine: String, index: Int, navigator: NavHostController, notesViewModel: NotesViewModel) {
    var showMenu by remember{
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(index)) },
                onLongClick = { showMenu = true }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Row {
                Text(
                    text = date,
                    color = Color.Gray
                )
                Text(
                    text = if (firstLine.length > 30) {
                        "${firstLine.take(30)}..."
                    } else {
                        firstLine
                    },
                    modifier = Modifier.padding(start = 5.dp),
                    color = Color.Gray
                )
            }
        }
    }
    if(showMenu){
        Popup(
            onDismissRequest = {showMenu = false}
        ) {
            Column {
                ActionItem(text = "Delete", icon = Icons.Default.Delete, iconColor = Color.Red) {
                    notesViewModel.deleteNote(index - 1)
                    showMenu = false
                }
            }
        }
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
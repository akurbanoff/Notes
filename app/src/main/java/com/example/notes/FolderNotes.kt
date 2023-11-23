package com.example.notes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notes.R
import com.example.notes.SearchBar
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.ui.theme.Orange
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
    title: String? = "Folders",
    backToFolderNotes: Boolean = false,
    notesViewModel: NotesViewModel
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title,
                    color = Orange,
                    modifier = Modifier.clickable {
                        if (backToFolderNotes){
                            val lastNote = notesViewModel.getAll(parentFolder = title).last()
                            if(lastNote.title.isEmpty() && lastNote.textBody.isEmpty()){
                                notesViewModel.deleteNote(lastNote.id)
                            }
                            navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title))
                        } else {
                            navigator.navigate(NavigationRoutes.MainScreen.route)
                        }
                    }
                )
            }
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
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (backToFolderNotes){
                        val lastNote = notesViewModel.getAll(parentFolder = title!!).last()
                        if(lastNote.title.isEmpty() && lastNote.textBody.isEmpty()){
                            notesViewModel.deleteNote(lastNote.id)
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
    val countNotes = notesViewModel.getAll(parentFolder = folder).size
    BottomAppBar(
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween, // изменено на SpaceBetween
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
    Column(
        modifier = modifier
    ) {
        LazyColumn(){
            itemsIndexed(notesViewModel.getAll(parentFolder = folder)){index, item ->
                Note(title = item.title, date = item.date, firstLine = item.firstLine, index = index, navigator = navigator)
            }
        }
    }
}

@Composable
fun Note(title: String, date: String, firstLine: String, index: Int, navigator: NavHostController) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.navigate(NavigationRoutes.NoteDetail.withArgs(index))
            },
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
}

//@Preview(showBackground = true)
@Composable
fun NotePreview() {
    NotesTheme {
        Note(title = "Test", date = "10/01/01", firstLine = "How to implement something new for asasdasdasdasdasd", index = 0, navigator = rememberNavController())
    }
}

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
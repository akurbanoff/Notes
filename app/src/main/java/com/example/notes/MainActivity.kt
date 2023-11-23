package com.example.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.notes.db.AppDatabase
import com.example.notes.db.models.Folder
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.ui.theme.Orange
import com.example.notes.view_models.FolderViewModel
import com.example.notes.view_models.NotesViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

//import com.google.android.material.color.DynamicColors

class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "note_db"
        ).build()
    }

    private val NotesViewModel by viewModels<NotesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(db.noteDao) as T
                }
            }
        }
    )

    private val FolderViewModel by viewModels<FolderViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FolderViewModel(db.folderDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DynamicColors.applyToActivitiesIfAvailable(application)
        setContent {
            NotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InitMainNavigation(folderViewModel = FolderViewModel, notesViewModel = NotesViewModel)//folderVM = FolderViewModel)
                }
            }
        }
    }
}

@Composable
fun InitMainNavigation(folderViewModel: FolderViewModel, notesViewModel: NotesViewModel){
    val navigator = rememberNavController()
    var folderTitle: String? = "Folders"

    NavHost(navController = navigator, startDestination = NavigationRoutes.MainScreen.route){
        composable(NavigationRoutes.MainScreen.route){ MainScreen(navigator = navigator, folderViewModel = folderViewModel)}
        composable(NavigationRoutes.FolderDetail.route + "/{name}", arguments = listOf(navArgument("name"){type = NavType.StringType})){
            backStackEntry -> backStackEntry.arguments?.let {
                folderTitle = it.getString("name")
                FolderNotesScreen(title = folderTitle, navigator = navigator, notesViewModel = notesViewModel)
        } }
        composable(NavigationRoutes.NoteDetail.route + "/{index}", arguments = listOf(navArgument("index"){type = NavType.IntType})){
            backStackEntry -> backStackEntry.arguments?.let {
                val index = it.getInt("index")
                NotesInsideScreen(index = index, navigator = navigator, title = folderTitle, notesViewModel = notesViewModel)
        } }
        composable(NavigationRoutes.NewNote.route){ NotesInsideScreen(navigator = navigator, title = folderTitle, newNote = true, notesViewModel = notesViewModel) }
        //composable("show_folder_dialog"){ CreateFolderDialog(folderViewModel = folderViewModel)}
    }
}

@Composable
fun MainScreen(navigator: NavHostController, folderViewModel: FolderViewModel) {
    val sharedFolder = Folder(title = "Shared")
    //val deletedFolder = FolderAdapter().deletedFolder
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            TopBar()
            Text(
                text = "Folders",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            SearchBar(modifier = Modifier.padding(top = 8.dp))
            FolderElement(
                title = sharedFolder.title,
                icon = Icons.Default.FolderOpen,
                modifier = Modifier.padding(top = 20.dp),
                navigator = navigator
            )
            //FolderGroupHeadline(modifier = Modifier.padding(top = 12.dp))
            FolderList(navigator = navigator, folderViewModel = folderViewModel)
            //FolderElement(title = deletedFolder.title, icon = deletedFolder.icon)
            //Spacer(modifier = Modifier.weight(1f))
        }
        BottomBar(folderViewModel = folderViewModel, navigator = navigator)
    }
}

@Composable
fun FolderGroupHeadline(modifier: Modifier = Modifier, showFolderList: Boolean) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "iCloud",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            imageVector = if(showFolderList) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Orange)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp),
        actions = {
            Text(text = "Edit", color = Orange)
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    TextField(
        value = "",
        shape = MaterialTheme.shapes.small,
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth(),
            //.height(36.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray)
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.DarkGray,
            textColor = Color.Gray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(text = "Search")
        },
        trailingIcon = {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_mic_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FolderList(modifier: Modifier = Modifier, navigator: NavHostController, folderViewModel: FolderViewModel) {
    val deletedFolder = Folder(title = "Recently Deleted")
    var showFolderList by remember{ mutableStateOf(true) }
    val state by folderViewModel.state.collectAsState()

    Column {
        FolderGroupHeadline(modifier = Modifier
            .padding(top = 12.dp)
            .clickable {
                showFolderList = !showFolderList
            }, showFolderList = showFolderList)
        if(showFolderList) {
            LazyColumn() {
                itemsIndexed(state.folders) { _, item ->
                    FolderElement(title = item.title, icon = Icons.Default.FolderOpen, navigator = navigator)
                }
            }
            FolderElement(title = deletedFolder.title, icon = Icons.Default.Delete, navigator = navigator)
        }
    }
}

@Composable
fun FolderElement(modifier: Modifier = Modifier, title: String, icon: ImageVector, navigator: NavHostController) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
            .clickable {
                navigator.navigate(NavigationRoutes.FolderDetail.withArgs(title))//("folder/$title")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ){
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(Orange)
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
                    text = "0", //количество заметок в этой папке
                    textAlign = TextAlign.End,
                    color = Color.Gray
                )
                Image(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }
        }
    }
}

@Composable
fun BottomBar(modifier: Modifier = Modifier, folderViewModel: FolderViewModel, navigator: NavHostController) {

    if (folderViewModel.openFolderDialog){
        CreateFolderDialog(folderViewModel = folderViewModel, navigator = navigator)
    }

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
                colorFilter = ColorFilter.tint(Orange)
            )
            Image(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        navigator.navigate(NavigationRoutes.NewNote.route)
                    },
                colorFilter = ColorFilter.tint(Orange)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(modifier: Modifier = Modifier, folderViewModel: FolderViewModel, navigator: NavHostController) {
    var countEmptyFolders = 1

    for (folder in folderViewModel.getAll()) {
        if (folder.title.contains("New Folder")) {
            countEmptyFolders += 1
        }
    }

    var DefaultFolderName = "New Folder $countEmptyFolders"
    var folderName by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { folderViewModel.openFolderDialog = false }) {
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
                    color = Orange,
                    modifier = Modifier.clickable { folderViewModel.openFolderDialog = false }
                )
                Text(
                    text = "New Folder",
                    color = Color.White
                )
                Text(
                    text = "Done",
                    color = Orange,
                    modifier = Modifier.clickable {
                        folderViewModel.createFolder(Folder(title = folderName))
                        folderViewModel.openFolderDialog = false
                    }
                )
            }
            TextField(
                value = "New Folder $countEmptyFolders",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { folderName = it },
                trailingIcon = {
                    Image(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.clickable { folderName = "" }
                    )
                },
                shape = MaterialTheme.shapes.small
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DialogPreview() {
//    NotesTheme {
//        CreateFolderDialog()
//    }
//}

//@Preview(showBackground = true)
@Composable
fun FolderGroupHeadlinePreview() {
    NotesTheme {
        FolderGroupHeadline(showFolderList = true)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TopBarPreview() {
//    NotesTheme {
//        TopBar()
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun BottomBarPreview() {
//    NotesTheme {
//        BottomBar()
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun FolderElementPreview() {
//    NotesTheme {
//        FolderElement(title = "Test", icon = Icons.Default.AccountCircle)
//    }
//}

//@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    NotesTheme {
        SearchBar()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    NotesTheme {
//        MainScreen(navigator = rememberNavController(), folderViewModel = vm)
//    }
//}
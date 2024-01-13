package com.example.notes.ui.composables.main_screen_modes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.db.models.Folder
import com.example.notes.ui.composables.main_screen_modes.SearchBar
import com.example.notes.ui.composables.main_screen_modes.TopBar
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditMainScreen(folderViewModel: FolderViewModel, notesViewModel: NotesViewModel, navigator: NavHostController, sharedFolder: Folder) {
    var isSearchBarVisible by remember{ mutableStateOf(true) }
    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        topBar = { TopBar(isEditModeStart = true, folderViewModel = folderViewModel) },
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
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            if (isSearchBarVisible) {
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
        }
    }
}
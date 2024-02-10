package com.example.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.domain.SaveLastNavStation
import com.example.notes.ui.navigation.Navigation
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                val folderViewModel = hiltViewModel<FolderViewModel>()
                val notesViewModel = hiltViewModel<NotesViewModel>()
                val stationSaver = SaveLastNavStation(this)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(folderViewModel = folderViewModel, notesViewModel = notesViewModel, stationSaver = stationSaver)
                }
            }
        }
    }
}
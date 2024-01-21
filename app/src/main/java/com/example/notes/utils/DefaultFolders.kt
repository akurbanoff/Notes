package com.example.notes.utils

sealed class DefaultFolders(val title: String) {
    object AlliCloudFolder: DefaultFolders("All iCloud")
    object NotesFolder: DefaultFolders("Notes")
    object RecentlyDeletedFolder: DefaultFolders("Recently Deleted")
    object SharedFolder: DefaultFolders("Shared")
}

fun collectFoldersName(): List<String>{
    return listOf(DefaultFolders.AlliCloudFolder.title, DefaultFolders.NotesFolder.title, DefaultFolders.RecentlyDeletedFolder.title, DefaultFolders.SharedFolder.title)
}
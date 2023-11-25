package com.example.notes.utils

sealed class NavigationRoutes(val route: String){
    object MainScreen: NavigationRoutes("main_screen")
    object FolderDetail: NavigationRoutes("folder")
    object NoteDetail: NavigationRoutes("note")
    object NewNote: NavigationRoutes("new_note")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withArgs(vararg args: Int): String{
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/${arg.toString()}")
            }
        }
    }
}
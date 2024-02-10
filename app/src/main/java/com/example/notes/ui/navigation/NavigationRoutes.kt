package com.example.notes.ui.navigation

sealed class NavigationRoutes(val route: String){
    object MainScreen: NavigationRoutes("main_screen")
    object FolderDetail: NavigationRoutes("folder")
    object NoteDetail: NavigationRoutes("note")

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
                append("/$arg")
            }
        }
    }
}
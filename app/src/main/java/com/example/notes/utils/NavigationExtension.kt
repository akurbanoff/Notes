package com.example.notes.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.navigation.NavHostController
import com.example.notes.ui.navigation.NavigationRoutes

@SuppressLint("RestrictedApi")
fun NavHostController.safePopBackStack(): Boolean{
    return if(this.currentBackStackEntry?.destination?.route!! != NavigationRoutes.MainScreen.route) {
        this.popBackStack()
    } else {
        false
    }
}
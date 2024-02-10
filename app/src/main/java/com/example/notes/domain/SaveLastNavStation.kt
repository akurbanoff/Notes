package com.example.notes.domain

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.notes.ui.navigation.NavigationRoutes


class SaveLastNavStation(activity: Activity) {
    private val STATION_KEY = "station"
    private val sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)

    fun saveStation(station: String){
        val lastStation = sharedPrefs.getString(STATION_KEY, "")
        if(station == lastStation!!) return
        if(station.contains("null")) return
        Log.d(STATION_KEY+"last", "$lastStation")
        if(!lastStation.isNullOrEmpty()){
            with(sharedPrefs.edit()){
                remove(STATION_KEY)
                apply()
            }
        }

        with(sharedPrefs.edit()){
            Log.d(STATION_KEY+"current", station)
            putString(STATION_KEY, station)
            apply()
        }
    }

    fun getStation(): String{
        val station = sharedPrefs.getString(STATION_KEY, NavigationRoutes.MainScreen.route)!!
        Log.d(STATION_KEY+"return", "$station")
        return station
    }
}
package com.example.notes.utils

import java.lang.StringBuilder
import java.time.ZonedDateTime
import java.time.ZoneId

class DateUtil {
    private enum class Month(name: String) {
        JANUARY("January"),
        FEBRUARY("February"),
        MARCH("March"),
        APRIL("April"),
        MAY("May"),
        JUNE("June"),
        JULY("July"),
        AUGUST("August"),
        SEPTEMBER("September"),
        OCTOBER("October"),
        NOVEMBER("November"),
        DECEMBER("December")
    }
    companion object Date{
        val zoneId = ZoneId.systemDefault()
        fun getDate(): String{
            val currentDate = ZonedDateTime.now(zoneId)
            val date = reformatDate(currentDate)
            return date
        }

        private fun reformatDate(zonedDate: ZonedDateTime): String{
            fun reformat(el: Int, isLast: Boolean = false): String{
                return if(el.toString().length > 1)
                    if(!isLast) "$el/" else "$el"
                else
                    if(!isLast) "0$el/" else "0$el"
            }

            val date = StringBuilder()

            val day = reformat(zonedDate.dayOfMonth)
            val month = reformat(zonedDate.monthValue)
            val year = reformat(el = zonedDate.year, isLast = true)

            date.append(day, month, year)
            return date.toString()
        }

        fun getCurrentTime(): String{
            fun reformat(el: Int): String{
                return if(el.toString().length > 1)
                    el.toString()
                else
                    "0$el"
            }

            val now = ZonedDateTime.now(zoneId)
            val time = StringBuilder()

            val hour = reformat(now.hour)
            val minute = reformat(now.minute)

            time.append(hour, ":", minute)

            return time.toString()
        }

        fun checkBeginOfDay(): Boolean{
            val currentTime = getCurrentTime()
            return currentTime.equals("00:01")
        }
    }
}
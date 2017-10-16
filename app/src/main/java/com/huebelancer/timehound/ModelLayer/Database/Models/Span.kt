package com.huebelancer.timehound.ModelLayer.Database.Models

import android.util.Log
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.R
import java.util.*

/**
 * Created by mahuebel on 10/15/17.
 */
data class Span(val start: EventDTO, val end: EventDTO, val format: String) {

    fun text() : String {
        val difference = Helpers.round((end.time!!.time - start.time!!.time) / Helpers.hourInMillis)
        return "${Helpers.displayDateFormat(start.time!!, format)}\n$difference hours"
    }

    fun icon(): Int {
        val startHour = startHour()
        val endHour = endHour()

        return if (startHour < 5 && endHour < 7 || startHour > 16 && endHour < 7 || endHour < 8 && startHour < 5) {
            //moon icon
            R.drawable.ic_brightness_3_black_24dp
        } else {
            //sun icon
            R.drawable.ic_brightness_high_black_24dp
        }
    }

    fun startHour() : Int {
        val startCal = Calendar.getInstance()
        startCal.time = start.time!!
        return startCal[Calendar.HOUR_OF_DAY]
    }


    fun endHour() : Int {
        val endCal = Calendar.getInstance()
        endCal.time = end.time!!
        return endCal[Calendar.HOUR_OF_DAY]
    }

    companion object {
        val TAG = Span::class.java.simpleName
    }
}
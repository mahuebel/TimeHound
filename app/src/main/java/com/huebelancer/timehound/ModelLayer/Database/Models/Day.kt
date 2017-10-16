package com.huebelancer.timehound.ModelLayer.Database.Models

import android.util.Log
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.ModelLayer.Enums.EventType
import java.util.*


/**
 * Created by mahuebel on 9/29/17.
 */
data class Day(var events: MutableList<EventDTO>, var date: Date) {

    init {
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        date = cal.time
    }

    fun getDayName() : String {
        val cal = Calendar.getInstance()
        cal.time = date
        Log.d(TAG, "${cal[Calendar.DAY_OF_WEEK]}")
        return when (cal[Calendar.DAY_OF_WEEK]) {
            1 -> "SUNDAY"
            2 -> "MONDAY"
            3 -> "TUESDAY"
            4 -> "WEDNESDAY"
            5 -> "THURSDAY"
            6 -> "FRIDAY"
            7 -> "SATURDAY"
            else -> ""
        }
    }

    fun clockInCount() : Int {
        return events.count {
            it.type == EventType.Clock_In
        }
    }

    fun clockOutCount(inCount: Int?) : Int {
        return if (inCount != null)
            events.size - inCount
        else
            events.size - clockInCount()
    }

    fun hoursWorked() : Double {
//        Log.i(Helpers.TAG, "Calculating Hours worked in a Day")
        return Helpers.hoursWorkedInEvents(events, true)
    }

    fun spans(): MutableList<Span> {
        return Helpers.getTimeSpans(events, "h:mma")
        /*
        val list = mutableListOf<String>()

        val hourInMillis: Double = 1000 * 60 * 60.0

        var lastClockIn: Date? = null
        val cal = Calendar.getInstance()

        events.forEachIndexed { index, event ->
            when (event.type) {
                EventType.Clock_In -> {
                    lastClockIn = event.time

                    if (index == events.lastIndex) {
                        val difference = Helpers.round((Date().time - lastClockIn!!.time) / hourInMillis)

                        list.add("$difference starting at ${Helpers.displayDateFormat(lastClockIn!!, "h:mm:ssa")}")
                    }
                }
                else -> {
                    if (index == 0) {
                        cal.time = date
                        cal[Calendar.HOUR_OF_DAY] = 0
                        cal[Calendar.MINUTE] = 0
                        cal[Calendar.SECOND] = 0
                        lastClockIn = cal.time
                    }

                    val difference = Helpers.round((event.time!!.time - lastClockIn!!.time) / hourInMillis)
                    list.add("$difference starting at ${Helpers.displayDateFormat(lastClockIn!!, "h:mm:ssa")}")
                }
            }
        }

        return list
        */
    }


    companion object {
        val TAG = Day::class.java.simpleName
    }

}
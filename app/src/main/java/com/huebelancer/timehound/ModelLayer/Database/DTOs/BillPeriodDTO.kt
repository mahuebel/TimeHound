package com.huebelancer.timehound.ModelLayer.Database.DTOs

import android.util.Log
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.Models.Day
import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import com.huebelancer.timehound.ModelLayer.Enums.EventType
import java.util.*

/**
 * Created by mahuebel on 10/6/17.
 */
data class BillPeriodDTO(var client: String, var isBilled: Boolean, var events: MutableList<EventDTO>) {

    fun startDate(): Date {
        return if (events.size > 0) {
            events[0].time!!
        } else {
            Date()
        }
    }

    fun endDate() : Date {
        return if (events.size > 0) {
            lastClockEvent().time!!
        } else {
            Date()
        }
    }

    fun lastClockEventType() : EventType {
        return lastClockEvent().type!!
    }

    fun lastClockEvent() : EventDTO {
        return events[events.lastIndex]
    }

    private fun isInRange(
            event: EventDTO,
            dayOfYear: Int,
            isLeapYear: Boolean) : Boolean {
        val cal = Calendar.getInstance()
        cal.time = event.time

        val yearDays = if (isLeapYear)
            366
        else
            365

        return if (dayOfYear > yearDays) {
            cal[Calendar.DAY_OF_YEAR] == dayOfYear - yearDays
        } else {
            cal[Calendar.DAY_OF_YEAR] == dayOfYear
        }
    }


    fun clockInCount() : Int {
        var count = 0
        events.forEach { event ->
            if (event.type == EventType.Clock_In)
                count++
        }
        return count
    }

    fun clockOutCount() : Int {
        var count = 0
        events.forEach { event ->
            if (event.type == EventType.Clock_Out)
                count++
        }
        return count
    }


    fun days(): MutableList<Day> {

        val cal = GregorianCalendar.getInstance()
        cal.time = endDate()
        val endYear = cal[Calendar.YEAR]
        var endDOY  = cal[Calendar.DAY_OF_YEAR]

        cal.time = startDate()
        val startYear = cal[Calendar.YEAR]
        val startDOY  = cal[Calendar.DAY_OF_YEAR]
        val isLeapYear = GregorianCalendar().isLeapYear(startYear)

        if (endYear > startYear) {
            endDOY += if (isLeapYear) {
                (366 - startDOY)
            } else {
                (365 - startDOY)
            }
            endDOY++
        }

        Log.d(ClientDTO.TAG, "" + startDOY + " to " + endDOY)

        val days = mutableListOf<Day>()

        for (i in startDOY..endDOY) {
            cal[Calendar.DAY_OF_YEAR] = i
            val day = Day(mutableListOf(), cal.time)
            events.forEach { event ->
                if (isInRange(event, i, isLeapYear)) {
                    day.events.add(event)
                }
            }
            if (day.events.size > 0) {
                days.add(day)
            }
        }

        return days
    }

    fun hoursWorked(): Double {
//        Log.i(Helpers.TAG, "Calculating Hours worked in Period")
        return Helpers.hoursWorkedInEvents(events, false)
    }

    fun isOpen(): Boolean {
        return events[events.lastIndex].type == EventType.Clock_In
    }

    fun spans(): MutableList<Span> {
        return Helpers.getTimeSpans(events, "MMM dd h:mma")
    }
}
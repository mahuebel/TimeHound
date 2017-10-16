package com.huebelancer.timehound.ModelLayer.Database.DTOs

import com.huebelancer.timehound.ModelLayer.Database.Models.Day
import com.huebelancer.timehound.ModelLayer.Enums.EventType

/**
 * Created by mahuebel on 9/4/17.
 */
data class ClientDTO(
        var name: String?,
        var hidden: Boolean?,
        var periods: MutableList<BillPeriodDTO> = mutableListOf(),
        var notes: MutableList<NoteDTO> = mutableListOf()
) {

    fun daysInPeriod() : MutableList<Day> {

        val period = if (periods.size > 0) {
            periods[periods.lastIndex]
        } else {
            BillPeriodDTO(
                    name!!,
                    false,
                    mutableListOf()
            )
        }

        return period.days()
    }


    fun periodHistory(): MutableList<BillPeriodDTO> {
        val billedList = mutableListOf<BillPeriodDTO>()

        periods.forEach({period ->
            if (period.isBilled)
                billedList.add(period)
        })

        return billedList
    }



    fun isOnTheClock() : Boolean {
        if (periods.size > 0) {
            val lastPeriod = periods[periods.lastIndex]
            return lastPeriod.events.size > 0
                    && lastPeriod.lastClockEventType() == EventType.Clock_In
        }
        return false
    }

    fun lastOpenPeriod() : BillPeriodDTO {
        return if (periods.size > 0)
            periods[periods.lastIndex]
        else
            BillPeriodDTO(name!!, false, mutableListOf())
    }


    override fun hashCode(): Int {
        val result = 69

        return result
    }


    companion object {
        val TAG = ClientDTO::class.java.simpleName
    }

}
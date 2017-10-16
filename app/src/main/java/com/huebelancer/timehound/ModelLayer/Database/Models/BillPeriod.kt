package com.huebelancer.timehound.ModelLayer.Database.Models

import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

/**
 * Created by mahuebel on 10/1/17.
 */
open class BillPeriod() : RealmObject() {
    var client: String? = null
    var isBilled = false
    var clockEvents = RealmList<ClockEvent>()

    fun startDate(): Date {
        return if (clockEvents.size > 0) {
            clockEvents[0].time!!
        } else {
            Date()
        }
    }

    fun endDate(): Date {
        return if (clockEvents.size > 0) {
            clockEvents[clockEvents.lastIndex].time!!
        } else {
            Date()
        }
    }

    override fun toString(): String {
        return "BillPeriod(client: ${client}, isBilled: ${isBilled}, clockEvents: ${clockEvents.size})"
    }
}
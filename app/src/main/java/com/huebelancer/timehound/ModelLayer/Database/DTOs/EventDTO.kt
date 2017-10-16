package com.huebelancer.timehound.ModelLayer.Database.DTOs

import com.huebelancer.timehound.ModelLayer.Enums.EventType
import java.util.*

/**
 * Created by mahuebel on 9/5/17.
 */
class EventDTO(
        var type: EventType?,
        var time: Date?,
        var clientId: String?
) {

    var dayOfYear = 0
    var year = 0

    init {
        val cal = Calendar.getInstance()
        cal.time = time
        dayOfYear = cal[Calendar.DAY_OF_YEAR]
        year = cal[Calendar.YEAR]
    }

}
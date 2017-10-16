package com.huebelancer.timehound.ModelLayer.Database.Models

import io.realm.RealmObject
import java.util.Date

/**
 * Created by mahuebel on 9/4/17.
 */
open class ClockEvent : RealmObject() {
    var type: String? = null
    var time: Date? = null
    var client: String? = null
}
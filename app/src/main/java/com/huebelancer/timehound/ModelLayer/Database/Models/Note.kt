package com.huebelancer.timehound.ModelLayer.Database.Models

import io.realm.RealmObject
import java.util.*

/**
 * Created by mahuebel on 10/8/17.
 */
open class Note : RealmObject() {
    var date: Date = Date()
    var text: String = ""
}
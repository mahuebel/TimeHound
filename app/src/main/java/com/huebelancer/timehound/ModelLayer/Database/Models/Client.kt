package com.huebelancer.timehound.ModelLayer.Database.Models

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by mahuebel on 9/4/17.
 */
open class Client : RealmObject() {
    var name: String? = null
    var hidden: Boolean? = null
    var periods: RealmList<BillPeriod> = RealmList()
    var notes: RealmList<Note> = RealmList()

    fun lastOpenPeriod() : BillPeriod {
        return periods[periods.lastIndex]
    }
}
package com.huebelancer.timehound.ModelLayer.Database

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by mahuebel on 9/24/17.
 */
class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        val schema = realm?.schema

        var curVersion = oldVersion

        if (curVersion == 0L) {
            schema?.get("Client")
                    ?.addRealmListField(
                            "clockEvents",
                            schema.get("ClockEvent")
                    )

            curVersion++
        }
    }

}
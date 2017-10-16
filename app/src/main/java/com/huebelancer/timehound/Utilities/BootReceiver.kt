package com.huebelancer.timehound.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.ModelLayer.Database.DataLayer
import com.huebelancer.timehound.ModelLayer.Database.Models.Client
import com.huebelancer.timehound.ModelLayer.Translation.TranslationLayer
import io.realm.Realm

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val realm = Realm.getDefaultInstance()
            val translationLayer = TranslationLayer()
            val coordinator = Coordinator()

            val clients: MutableList<Client> = DataLayer.loadClientsFromRealm(realm)
            val result = translationLayer.translate(clients)
            realm.close()

            result.forEach { client ->
                if (client.isOnTheClock())
                    coordinator.handleStartNotification(client, context)
            }
        }
    }
}

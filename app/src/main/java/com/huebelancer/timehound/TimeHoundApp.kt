package com.huebelancer.timehound

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatDelegate
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.Constants
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by mahuebel on 9/4/17.
 */
class TimeHoundApp : Application() {
    var registry: DependencyRegistry? = null
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        Realm.init(this)

        val config = RealmConfiguration.Builder()
                .schemaVersion(1)
//                .migration(Migration()) //this will be production
                .deleteRealmIfMigrationNeeded() //while schema is maturing
                .build()

        Realm.setDefaultConfiguration(config)

        registry = DependencyRegistry.shared

        setupNotificationChannel()
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name: CharSequence = "TimeHound Main Channel"
            val description = "Display active clients"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_ONGOING, name, importance)
            channel.description = description
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
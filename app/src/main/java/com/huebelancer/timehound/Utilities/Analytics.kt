package com.huebelancer.timehound.Utilities

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.crashlytics.android.core.CrashlyticsListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.BillPeriodDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import io.fabric.sdk.android.Fabric

/**
 * Created by mahuebel on 10/18/17.
 */
class Analytics(/*val context: Context*/) {

    val analytics = Answers.getInstance()


    fun appLaunch() {
        analytics.logCustom(
                CustomEvent(Events.APP_LAUNCH)
        )
    }

    fun clientAdded(client: ClientDTO) {
        analytics.logCustom(
                CustomEvent(Events.CLIENT_ADDED)
                        .putCustomAttribute(
                                Events.CLIENT_NAME,
                                client.name
                        )
        )
    }

    fun noteAdded() {
        analytics.logCustom(
                CustomEvent(Events.NOTE_ADDED)
        )
    }

    fun periodBilled(period: BillPeriodDTO) {
        analytics.logCustom(
                CustomEvent(Events.PERIOD_BILLED)
                        .putCustomAttribute(
                                Events.CLIENT_NAME,
                                period.client
                        )
        )
    }

    fun clockIn(client: String) {
        analytics.logCustom(
                CustomEvent(Events.CLOCK_EVENTS)
                        .putCustomAttribute(
                                Events.CLOCK_TYPE,
                                Events.TIME_START
                        )
                        .putCustomAttribute(
                                Events.CLIENT_NAME,
                                client
                        )
        )
    }

    fun clockOut(client: String) {
        analytics.logCustom(
                CustomEvent(Events.CLOCK_EVENTS)
                        .putCustomAttribute(
                                Events.CLOCK_TYPE,
                                Events.TIME_STOP
                        )
                        .putCustomAttribute(
                                Events.CLIENT_NAME,
                                client
                        )
        )
    }

    fun viewedClientList() {
        analytics.logCustom(
                CustomEvent(Events.VIEWED_CLIENTS_LIST)
        )
    }

    fun viewedClient(client: String) {
        analytics.logCustom(
                CustomEvent(Events.VIEWED_CLIENT)
                        .putCustomAttribute(
                                Events.CLIENT_NAME,
                                client
                        )
        )
    }



    companion object {

        class Events {
            companion object {
                val APP_LAUNCH = "App Launch"

                val VIEWED_CLIENTS_LIST = "Viewed Clients List"
                val VIEWED_CLIENT = "Viewed Client"
                val CLIENT_ADDED = "Client Added"
                val NOTE_ADDED = "Client Note Added"
                val PERIOD_BILLED = "Client Period Finished"
                val TIME_START = "Clock Ins"
                val TIME_STOP = "Clock Outs"

                val CLIENT_NAME = "Client Name"
                val CLOCK_EVENTS = "Clock Events"
                val CLOCK_TYPE = "Type"
            }
        }

        var sInstance: Analytics? = null;

        fun getInstance(/*context: Context*/) : Analytics {
            if (sInstance == null) {
                sInstance = Analytics()
            }
            return sInstance!!
        }

        fun initialize(app: Application) {
            Fabric.with(app, Answers(), Crashlytics())
            Analytics.getInstance()
        }
    }


}
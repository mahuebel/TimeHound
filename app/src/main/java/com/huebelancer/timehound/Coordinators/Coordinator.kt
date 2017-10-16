package com.huebelancer.timehound.Coordinators

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.Activities.ClientDetails.ClientFragment
import com.huebelancer.timehound.Helpers.Constants
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.R
import com.huebelancer.timehound.Utilities.NotificationService

/**
 * Created by mahuebel on 9/4/17.
 */
class Coordinator {
    companion object {
        val TAG = Coordinator::class.java.simpleName
    }

    fun handleClientClick(activity: AppCompatActivity, clientName: String, frame: Int, clearClient: Boolean) {


        val intent = Intent(activity, ClientDetailActivity::class.java)
        intent.putExtra(Constants.EXTRA_CLIENT_NAME, clientName)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)

        /*
        val count = activity.supportFragmentManager.backStackEntryCount
        Log.d(TAG, "number of backstack entries $count")
        if (count > 1) {
            for (i in 0..(count - 1)) {
                activity.supportFragmentManager.popBackStack()
            }
        }

        val fragment = ClientFragment.newInstance(clientName)
        Helpers.replaceFragmentInActivity(
                activity.supportFragmentManager,
                fragment,
                frame,
                clearClient
        )
        */
    }

    /*
    fun handleClientHistoryClick(activity: AppCompatActivity, clientName: String, frame: Int) {
        val fragment = ClientHistoryFragment.newInstance(clientName)
        Helpers.replaceFragmentInActivity(
                activity.supportFragmentManager,
                fragment,
                frame,
                false
        )
    }

    fun handleClientNotesClick(activity: AppCompatActivity, clientName: String, frame: Int) {
        val fragment = ClientNotesFragment.newInstance(clientName)
        Helpers.replaceFragmentInActivity(
                activity.supportFragmentManager,
                fragment,
                frame,
                false
        )
    }
    */

    fun handleStopNotification(client: ClientDTO, context: Context) {
        Log.d(ClientFragment.TAG, "${client.name} name hashcode: ${client.name?.hashCode()}")

        val notificationIntent = Intent(context, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.ACTIVE_NAME, client.name)
        notificationIntent.putExtra(NotificationService.IS_CANCEL, true)
        notificationIntent.putExtra(NotificationService.REQUEST_CODE, client.name!!.hashCode())
        context.startService(notificationIntent)
    }

    fun handleStopNotification(client: String, context: Context) {
        Log.d(ClientFragment.TAG, "$client name hashcode: ${client.hashCode()}")

        val notificationIntent = Intent(context, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.ACTIVE_NAME, client)
        notificationIntent.putExtra(NotificationService.IS_CANCEL, true)
        notificationIntent.putExtra(NotificationService.REQUEST_CODE, client.hashCode())
        context.startService(notificationIntent)
    }

    fun handleStartNotification(client: ClientDTO, context: Context) {
        Log.d(ClientFragment.TAG, "${client!!.name} name hashcode: ${client.name?.hashCode()}")

        val notificationIntent = Intent(context, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.ACTIVE_NAME, client.name)
//        notificationIntent.putExtra(NotificationService.START_TIME, client?.events!![client!!.events.size - 1].time)
        notificationIntent.putExtra(NotificationService.START_TIME, client.lastOpenPeriod().lastClockEvent().time?.time)
        notificationIntent.putExtra(NotificationService.IS_CANCEL, false)
        notificationIntent.putExtra(NotificationService.REQUEST_CODE, client.name!!.hashCode())
        context.startService(notificationIntent)
    }
}
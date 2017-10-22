package com.huebelancer.timehound.Utilities

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.huebelancer.timehound.Activities.ClientsActivity
import com.huebelancer.timehound.Helpers.Constants
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.R
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /*val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")
        wakeLock.acquire()*/

        val activeName = intent.getStringExtra(NotificationService.ACTIVE_NAME)
        val code = intent.getIntExtra(NotificationService.REQUEST_CODE, 0)
        val start = intent.getLongExtra(NotificationService.START_TIME, 0)
        var content = ""
        if (start > 0)
            content = "Clocked in since ${Helpers.displayDateFormat(Date(start))}"

        val pi: PendingIntent = generateClientIntent(context, activeName, code)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_ONGOING)
                .setContentTitle(activeName)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pi)
                .setOngoing(true)
                .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(code, notification)

//        wakeLock.release()
    }

    private fun generateClientIntent(context: Context, activeName: String, code: Int) : PendingIntent {
        val clientIntent = Intent(context, ClientsActivity::class.java)
        clientIntent.putExtra(NotificationService.ACTIVE_NAME, activeName)

        return PendingIntent.getActivity(
                context,
                code,
                clientIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
    }


    fun startNotification(context: Context, activeName: String, start: Long, code: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(NotificationService.START_TIME, start)
        intent.putExtra(NotificationService.ACTIVE_NAME, activeName)
        intent.putExtra(NotificationService.REQUEST_CODE, code)

        val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        manager.setExact(
                AlarmManager.RTC_WAKEUP,
                Date().time + 500,
                pendingIntent
        )
    }

    fun cancelNotification(context: Context, activeName: String, code: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

/*
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(NotificationService.ACTIVE_NAME, activeName)
*/

/*
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
*/

        manager.cancel(code)
    }
}

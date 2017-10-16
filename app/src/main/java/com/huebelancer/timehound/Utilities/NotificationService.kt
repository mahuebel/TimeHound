package com.huebelancer.timehound.Utilities

import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationService : Service() {

    companion object {
        val START_TIME = "start"
        val ACTIVE_NAME = "active"
        val IS_CANCEL = "isCancel"
        val REQUEST_CODE = "requestCode"
    }

    val receiver = NotificationReceiver()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val start = intent.getLongExtra(START_TIME, 0)
            val name = intent.getStringExtra(ACTIVE_NAME)
            val isCancel = intent.getBooleanExtra(IS_CANCEL, false)
            val code = intent.getIntExtra(REQUEST_CODE, 0)
            if (isCancel) {
                receiver.cancelNotification(this, name, code)
            } else {
                receiver.startNotification(this, name, start, code)
            }
        }

        return START_STICKY
    }

    override fun onStart(intent: Intent?, startId: Int) {

    }
}

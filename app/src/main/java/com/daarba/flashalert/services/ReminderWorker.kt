package com.daarba.flashalert.services

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.daarba.flashalert.helper.Utils

class ReminderWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        /*val serviceIntent = Intent(context, NotificationForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }*/
        try {
            Utils.toggleNotificationListenerService(context)
            val nfIntent = Intent(context, NotificationService::class.java)
            context.startService(nfIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Result.success()
    }

}
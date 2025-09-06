package com.daarba.flashalert.helper

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.daarba.flashalert.services.NotificationForegroundService
import com.daarba.flashalert.services.NotificationService


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("Bootreciever", "Boot completed detected.")

            /*val serviceIntent = Intent(context, NotificationForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }*/


            try {
                // Re-enable notification listener
                Utils.toggleNotificationListenerService(context)
                // Start your service if needed
                val nfIntent = Intent(context, NotificationService::class.java)
                context.startService(nfIntent)

            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

}

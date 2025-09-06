package com.daarba.flashalert.services


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.content.ContextCompat
import com.daarba.flashalert.helper.FlashLightManager
import com.daarba.flashalert.helper.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationService : NotificationListenerService() {

    /*private lateinit var telephonyManager: TelephonyManager
    private var telephonyCallback: TelephonyCallback = @RequiresApi(Build.VERSION_CODES.S)
    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(
                        "CallState",
                        "Phone is ringing"
                    )
                    if (shouldFlash(applicationContext) && !doNotDisturb()) {
                        FlashLightManager.startBlinking(
                            applicationContext,
                            defaultBlinkSpeed
                        )
                    }
                }

                else -> {
                    FlashLightManager.stopBlinking()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private var phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> Log.d(
                    "CallState",
                    "Phone is ringing: $incomingNumber"
                )

                TelephonyManager.CALL_STATE_OFFHOOK -> Log.d(
                    "CallState",
                    "Call answered or outgoing"
                )

                TelephonyManager.CALL_STATE_IDLE -> Log.d("CallState", "Call ended or idle")
            }
        }
    }*/

    private val defaultBlinkSpeed: Long =
        SessionManager.getInt(SessionManager.KEY_FLASH_BLINK_SPEED, 300).toLong()

    override fun onListenerConnected() {
        super.onListenerConnected()
        //telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        Log.d("TestFlash", "Notified from: ${sbn.packageName}")
//        FlashLightManager.flashOnce(applicationContext, 200)
        val packageName = sbn.packageName
        val selectedAppPackage = getSelectedAppPackage(this)
        val isNotificationEnabled = SessionManager.getBool(SessionManager.INCOMING_SMS, false)

        Log.d("NotificationService", "Notification received from: $packageName")

        val extras = sbn.notification.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d("BundleContent", "$key → $value")
            }
        }

        val hasAllPermissions = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        /*if (hasAllPermissions){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback!!)
            } else {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }*/

        if (packageName == "com.google.android.apps.messaging") {
            if (shouldFlash(applicationContext) && !doNotDisturb()) {
                Log.d("NotificationService", "Flashing for app notification: $packageName")
                if (shouldFlash(applicationContext) && !doNotDisturb()) {
                    FlashLightManager.flashOnce(applicationContext, 200)
                }
            }
        }
        if (packageName == "com.whatsapp") {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") // sender name
            val text = extras.getCharSequence("android.text")?.toString()
            Log.d("NotifListener", "WhatsApp Message from $title: $text")

            if (title == null && text == null) {
                Log.d("NotifListener", "WhatsApp Audio message detected!")
                return
            }

        }

        if (!isNotificationEnabled) {
            Log.d("NotificationService", "Notification Flash Disabled")
            return
        }

        if (selectedAppPackage.contains(packageName)) {
            val extras = sbn.notification.extras
            //val text = extras.getCharSequence("android.text")?.toString()
            val callType = extras.getInt("android.callType")
            if (callType == 1){
                Log.d("abc", callType.toString())
                if (shouldFlash(applicationContext) && !doNotDisturb()) {
                    FlashLightManager.startBlinking(applicationContext, 200)
                    return
                }
            }else if (extras.getInt("android.progressMax") == 0){
                if (shouldFlash(applicationContext) && !doNotDisturb()) {
                    Log.d("whatsapp", "Flashing for app notification: $packageName")
                    FlashLightManager.flashOnce(applicationContext, 200)
                } else {
                    Log.d("whatsapp", "Battery too low, flashlight disabled.")
                    // FlashLightManager.stopBlinking()
                }
            }

            FlashLightManager.stopBlinking()



            /*if (text != null && text == "Incoming voice call") {
                Log.d("whatsapp", "Flashing for app notification: $packageName")
                if (shouldFlash(applicationContext) && !doNotDisturb()) {
                    FlashLightManager.startBlinking(applicationContext, 200)
                }
            }
            if (text!!.contains("Incoming video call", ignoreCase = true)) {
                Log.d("whatsapp", "Flashing for incoming video call $packageName")
                if (shouldFlash(applicationContext) && !doNotDisturb()) {
                    FlashLightManager.startBlinking(applicationContext, 200)
                }
            } else if (text == "Ongoing voice call") {
                Log.d("whatsapp", "voice Call answered, stopping flashlight.")
                FlashLightManager.stopBlinking()
            } else if (text.contains("Ongoing video call", ignoreCase = true)) {
                Log.d("whatsapp", "video call answered, stopping flashlight.")
                FlashLightManager.stopBlinking()
            } else*/

        }

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val text = sbn.notification.extras.getCharSequence("android.text")?.toString()

        // If the removed notification was from WhatsApp and we were flashing, stop it
        if (packageName == "com.whatsapp") {
            Log.d("NotificationService", "WhatsApp notification removed: $text")
            FlashLightManager.stopBlinking()
        }

        // Also handle custom selected apps (if any)
        if (getSelectedAppPackage(applicationContext).contains(packageName)) {
            Log.d("NotificationService", "Notification removed from: $packageName - $text")
            FlashLightManager.stopBlinking()
        }
    }

    private fun getSelectedAppPackage(context: Context): MutableSet<String> {
        return SessionManager.getSets(SessionManager.SELECT_APP)
    }

    private fun shouldFlash(context: Context): Boolean {
        val isMainToggleOn = SessionManager.getBool(SessionManager.MAIN_TOGGLE, false)
        val batteryLevel = getBatteryPercentage(context)
        val status = isMainToggleOn && batteryLevel
        Log.d(
            "FlashCheck",
            "MainToggle: $isMainToggleOn, Battery: $batteryLevel, ShouldFlash: $status"
        )
        return status
    }


    private fun doNotDisturb(): Boolean {
        val isDndEnabled = SessionManager.getBool(SessionManager.DND_SETTINGS_STATE, false)
        val fromTime = SessionManager.getString(SessionManager.DND_FROM_TIME, "")
        val toTime = SessionManager.getString(SessionManager.DND_TO_TIME, "")
        if (!isDndEnabled) {
            return false
        }

        if (fromTime.isNotEmpty() && toTime.isNotEmpty()) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Calendar.getInstance()
            val fromCal = Calendar.getInstance()
            val toCal = Calendar.getInstance()
            fromCal.time = sdf.parse(fromTime)!!
            toCal.time = sdf.parse(toTime)!!

            fromCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            fromCal.set(Calendar.MONTH, now.get(Calendar.MONTH))
            fromCal.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

            toCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            toCal.set(Calendar.MONTH, now.get(Calendar.MONTH))
            toCal.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

            // If toTime is before fromTime, it means the range crosses midnight
            if (toCal.before(fromCal)) {
                // Move toTime to next day
                toCal.add(Calendar.DATE, 1)

            }
            val status = now.timeInMillis in fromCal.timeInMillis..toCal.timeInMillis
            Log.d("NotificationService", "status = > $status")
            return status
        }
        return false
    }

    private fun getBatteryPercentage(context: Context): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val value = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val batteryStates = SessionManager.getBool(SessionManager.BATTERY_SAVER_STATE, false)
        val minBatteryThreshold = SessionManager.getInt(SessionManager.BATTERY_SAVER_LEVEL, 20)
        return !(batteryStates && value <= minBatteryThreshold)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationService", "Service destroyed")
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback.let {
                telephonyManager.unregisterTelephonyCallback(it)
            }
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }*/
    }
}

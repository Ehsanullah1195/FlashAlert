package com.daarba.flashalert.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.telephony.TelephonyManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CallReciever : BroadcastReceiver() {
    private val defaultBlinkSpeed: Long = SessionManager.getInt(SessionManager.KEY_FLASH_BLINK_SPEED, 300).toLong()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("call_checker", "Phone is ringing ${intent.action}")
        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.d("call_checker", "Phone is ringing")

                if (shouldFlash(context) && !doNotDisturb()) {
                    Log.d("call_checker", "Flashing.")
                    context.let { FlashLightManager.startBlinking(it, defaultBlinkSpeed) }
                } else {
                    Log.d("call_checker", "Battery too low, flashlight disabled.")
                }
            } else {
                Log.d("call_checker", "Stop Flashing.")
                context.let { FlashLightManager.stopBlinking() }
            }
        }
    }

    private fun shouldFlash(context: Context?): Boolean {
        val callingState = SessionManager.getBool(SessionManager.INCOMING_CALL_TOGGLE_STATE, false)
        val isMainToggleOn = SessionManager.getBool(SessionManager.MAIN_TOGGLE, false)
        val batteryStatus = getBatteryPercentage(context)
        return callingState && isMainToggleOn && batteryStatus

    }

    private fun getBatteryPercentage(context: Context?): Boolean {
        val batteryManager = context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val value = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val batteryStates = SessionManager.getBool(SessionManager.BATTERY_SAVER_STATE, false)
        val minBatteryThreshold = SessionManager.getInt(SessionManager.BATTERY_SAVER_LEVEL, 20)
        return !(batteryStates && value <= minBatteryThreshold)
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

            // Set today's date for from and to
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
}

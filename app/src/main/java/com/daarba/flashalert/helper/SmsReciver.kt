package com.daarba.flashalert.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReciver : BroadcastReceiver() {
    private var speed: Long = 300

    override fun onReceive(context: Context, intent: Intent) {
        if (SessionManager.getBool(SessionManager.MAIN_TOGGLE, true)) {
            if (SessionManager.getBool(SessionManager.INCOMING_SMS, false)) {
                if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
                    if (shouldFlash(context)) {
                        val bundle: Bundle? = intent.extras
                        try {
                            if (bundle != null) {
                                val pdus = bundle["pdus"] as Array<*>?
                                if (pdus != null) {
                                    for (pdu in pdus) {
                                        val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                                        val messageBody = sms.messageBody
                                        val sender = sms.originatingAddress

                                        Log.d(
                                            "sms_checker",
                                            "SMS received from: $sender, Message: $messageBody"
                                        )
                                        FlashLightManager.flashOnce(context, speed)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("sms_checker", "Battery too low, flashlight disabled.")
                    }
                }
            }
        }
    }

    private fun shouldFlash(context: Context): Boolean {
        val batteryLevel = getBatteryPercentage(context)
        val minBatteryThreshold = SessionManager.getInt(SessionManager.BATTERY_SAVER_LEVEL, 20)
        return batteryLevel > minBatteryThreshold
    }

    private fun getBatteryPercentage(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}

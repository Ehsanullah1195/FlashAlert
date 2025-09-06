package com.daarba.flashalert.ui.fragments.setting

import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daarba.flashalert.BuildConfig
import com.daarba.flashalert.R
import com.daarba.flashalert.databinding.FragmentSettingBinding
import com.daarba.flashalert.helper.SessionManager
import com.daarba.flashalert.ui.MessageDialogBuilder
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        loadSavedSettings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvFrom.setOnClickListener { showTimePicker(binding.tvFrom) }
        binding.tvTo.setOnClickListener { showTimePicker(binding.tvTo) }

        binding.llChangelanguage.setOnClickListener {
            findNavController().navigate(R.id.action_Setting_to_language)
            /*val intent=Intent(requireContext(),SettingLanguageActivity::class.java)
            startActivity(intent)*/
        }

        binding.switchDoNotDisturb.setOnCheckedChangeListener { _, isChecked ->
            SessionManager.putBool(SessionManager.DND_SETTINGS_STATE, isChecked)
            enableTimeFields(isChecked)
        }

        binding.switchBatterySave.setOnCheckedChangeListener { _, isChecked ->
            SessionManager.putBool(SessionManager.BATTERY_SAVER_STATE, isChecked)
            if (isChecked) {
                val selectedBatteryLevel = binding.sliderFlashSpeed.value.toInt()
                SessionManager.setInt(SessionManager.BATTERY_SAVER_LEVEL, selectedBatteryLevel)
                checkBatteryLevelForFlash()
            } else {
                // When Battery Saver is OFF, enable Flashlight regardless of battery level
                SessionManager.putBool(SessionManager.FLASH_ALLOWED, true)
                Toast.makeText(requireContext(), "Battery Saver disabled. Flashlight will work normally.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.sliderFlashSpeed.addOnChangeListener { _, value, _ ->
            val selectedLevel = value.toInt()
            SessionManager.setInt(SessionManager.BATTERY_SAVER_LEVEL, selectedLevel)

            if (binding.switchBatterySave.isChecked) {
                checkBatteryLevelForFlash()
            }
        }

        binding.ivRateUs.setOnClickListener{
            rateApp()
        }
        binding.ivShareApp.setOnClickListener{
            shareApp()
        }
        binding.ivMoreApps.setOnClickListener{
            showMoreAppsDialog()
        }
        binding.ivPrivacyPolicy.setOnClickListener {
            showPrivacyDialog()
        }
    }

    private fun enableTimeFields(enable: Boolean) {
        binding.tvFrom.isEnabled = enable
        binding.tvTo.isEnabled = enable
        val textColor = if (enable) R.color.active_color else R.color.in_active_color
        binding.tvFrom.setTextColor(resources.getColor(textColor, null))
        binding.tvTo.setTextColor(resources.getColor(textColor, null))
    }

    private fun showTimePicker(target: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            target.text = formattedTime
            if (target.id == R.id.tv_from) {
                SessionManager.setString(SessionManager.DND_FROM_TIME, formattedTime)
            }else{
                SessionManager.setString(SessionManager.DND_TO_TIME, formattedTime)
            }

        }, hour, minute, true)

        timePicker.show()
    }

    private fun checkBatteryLevelForFlash() {
        val batteryLevel = getBatteryPercentage()
        val selectedThreshold = SessionManager.getInt(SessionManager.BATTERY_SAVER_LEVEL, 20)

        if (binding.switchBatterySave.isChecked) {
            if (batteryLevel <= selectedThreshold) {
                // Prevent Flashlight if battery is low
                SessionManager.putBool(SessionManager.FLASH_ALLOWED, false)
                Toast.makeText(requireContext(), "Flashlight disabled due to low battery.", Toast.LENGTH_SHORT).show()
            } else {
                // Allow Flashlight if battery is fine
                SessionManager.putBool(SessionManager.FLASH_ALLOWED, true)
            }
        } else {
            // If Battery Saver is OFF, allow flashlight
            SessionManager.putBool(SessionManager.FLASH_ALLOWED, true)
        }
    }

    private fun getBatteryPercentage(): Int {
        val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun loadSavedSettings() {
        val isDndEnabled = SessionManager.getBool(SessionManager.DND_SETTINGS_STATE, false)
        val fromTime = SessionManager.getString(SessionManager.DND_FROM_TIME, "")
        val toTime = SessionManager.getString(SessionManager.DND_TO_TIME, "")

        binding.switchDoNotDisturb.isChecked = isDndEnabled
        if (fromTime.isNotEmpty()) binding.tvFrom.text = fromTime
        if (toTime.isNotEmpty()) binding.tvTo.text = toTime
        enableTimeFields(isDndEnabled)

        val isBatterySaverOn = SessionManager.getBool(SessionManager.BATTERY_SAVER_STATE, false)
        val savedBatteryLevel = SessionManager.getInt(SessionManager.BATTERY_SAVER_LEVEL, 20)

        binding.switchBatterySave.isChecked = isBatterySaverOn
        binding.sliderFlashSpeed.value = savedBatteryLevel.toFloat()

        checkBatteryLevelForFlash()
    }

    private fun rateApp() {
        MessageDialogBuilder.Builder(requireActivity())
            .withMessage("Please give your feedback about the application. We will consider your point of view at serious note.")
            .withTitle("Rate App")
            .withOkButtonListener("Rate us", object : MessageDialogBuilder.OnOkClick {
                override fun onClick(dialogs: AlertDialog) {
                    val uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    try {
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                            )
                        )
                    }
                    dialogs.dismiss()
                }
            })
            .withCancelListener("Cancel", object : MessageDialogBuilder.OnCancel {
                override fun onClick(dialogs: AlertDialog) {
                    dialogs.dismiss()
                }
            })
            .build()
    }

    private fun showPrivacyDialog() {
        MessageDialogBuilder.Builder(requireActivity())
            .withTitle("Privacy Policy")
            .withMessage(
                "You will be redirected to Browser to open privacy policy.\n" +
                        "Are you sure?"
            )
            .withOkButtonListener("REDIRECT", object : MessageDialogBuilder.OnOkClick {
                override fun onClick(dialogs: AlertDialog) {
                    val privacyUrl = getString(R.string.policy_url)
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
                    startActivity(browserIntent)
                    dialogs.dismiss()
                }
            })
            .withCancelListener("CANCEL", object : MessageDialogBuilder.OnCancel {
                override fun onClick(dialogs: AlertDialog) {
                    dialogs.dismiss()
                }
            }).build()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        val shareBody = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(intent, "Share Using"))
    }

    private fun showMoreAppsDialog() {
        MessageDialogBuilder.Builder(requireActivity())
            .withTitle("More Apps")
            .withMessage("You will be redirected to check our more apps.\nAre you sure?")
            .withOkButtonListener("REDIRECT", object : MessageDialogBuilder.OnOkClick {
                override fun onClick(dialogs: AlertDialog) {
                    val moreAppsUrl = "https://play.google.com/store/search?q=${getString(R.string.developer_account_link)}&c=apps"
                    try {
                        // Try to open in Play Store app
                        startActivity(Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://search?q=pub:" + getString(R.string.developer_account_link))
                            )
                        ) //"market://dev?id=5776642466940311170"

                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(moreAppsUrl)))
                    }
                    dialogs.dismiss()
                }
            })
            .withCancelListener("Cancel", object : MessageDialogBuilder.OnCancel {
                override fun onClick(dialogs: AlertDialog) {
                    dialogs.dismiss()
                }
            }).build()
    }

}

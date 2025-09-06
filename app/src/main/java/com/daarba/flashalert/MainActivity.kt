package com.daarba.flashalert

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.daarba.flashalert.databinding.ActivityMainBinding
import com.daarba.flashalert.services.NotificationForegroundService
import com.daarba.flashalert.services.ReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        setSupportActionBar(binding.toolbar)
        //startOverlayService()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        startWorkManager();

    }

    private fun startOverlayService() {
        Log.d("TAG11", "Starting NotificationForegroundService...")
        val intent = Intent(this, NotificationForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /*override fun attachBaseContext(ctx: Context) {
        val wrappedContext = LocaleHelper.wrap(ctx)
        super.attachBaseContext(wrappedContext)
    }*/

    private fun startWorkManager() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(4, TimeUnit.HOURS)
            .setConstraints(Constraints.NONE)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "check_flash_alert_service",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

}
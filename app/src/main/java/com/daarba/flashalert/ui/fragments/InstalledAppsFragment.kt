package com.daarba.flashalert.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daarba.flashalert.adapters.InstalledAppAdapter
import com.daarba.flashalert.databinding.FragmentInstalledFragmentBinding
import com.daarba.flashalert.helper.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstalledAppsFragment : Fragment() {

    private lateinit var binding: FragmentInstalledFragmentBinding
    private lateinit var installAppsAdapter: InstalledAppAdapter
    private val installedApps:MutableList<ApplicationInfo> = ArrayList()
    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstalledFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog.setMessage("Fetching installed apps")
        progressDialog.show()

        installAppsAdapter = InstalledAppAdapter(installedApps) { selectedApp ->
            //handleAppSelection(selectedApp)
            //val packageName = selectedApp.packageName
            val selectedApps = SessionManager.getSets(SessionManager.SELECT_APP)
            if (selectedApps.contains(selectedApp.packageName)){
                selectedApps.remove(selectedApp.packageName)
                SessionManager.putSets(SessionManager.SELECT_APP, selectedApps)
            }else{
                selectedApps.add(selectedApp.packageName)
                SessionManager.putSets(SessionManager.SELECT_APP, selectedApps)
            }
            //SessionManager.putSets(SessionManager.SELECT_APP, packageName)
        }

        CoroutineScope(Dispatchers.IO).launch {
            getInstalledApps()
        }


        binding.recyclerViewInstalledApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = installAppsAdapter
        }

    }

    private suspend fun getInstalledApps() {
        val packageManager = requireContext().packageManager
        val allApps = packageManager.getInstalledApplications(PackageManager.MATCH_ALL)

        /*for (appInfo in allApps) {
            Log.d("InstalledAppsFragment", "Installed package: ${appInfo.packageName}")
            Log.d("InstalledAppsFragment", "Source dir: ${appInfo.sourceDir}")
            Log.d(
                "InstalledAppsFragment",
                "Launch Activity: ${packageManager.getLaunchIntentForPackage(appInfo.packageName)}"
            )
        }*/

        val filteredApps = allApps.filter { app ->
            (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
        }

        withContext(Dispatchers.Main){
            installedApps.addAll(filteredApps)
            installAppsAdapter.notifyDataSetChanged()
            progressDialog.dismiss()
        }

    }

    private fun handleAppSelection(appInfo: ApplicationInfo) {
        val packageManager = requireContext().packageManager
        val appName = appInfo.loadLabel(packageManager).toString()
        val packageName = appInfo.packageName
        SessionManager.setString(SessionManager.SELECT_APP,packageName)
        val sharedPreferences = requireContext().getSharedPreferences("FlashlightPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("SELECTED_APP", packageName).apply()

        // Log the selected app
        Log.d("InstalledAppsFragment", "Selected app saved: $appName ($packageName)")

    }
}

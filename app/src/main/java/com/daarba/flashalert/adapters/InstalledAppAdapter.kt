package com.daarba.flashalert.adapters

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daarba.flashalert.databinding.ItemInstalledAppBinding
import com.daarba.flashalert.helper.SessionManager

class InstalledAppAdapter(
    private val appsList: List<ApplicationInfo>,
    private val onAppSelected: (ApplicationInfo) -> Unit
) : RecyclerView.Adapter<InstalledAppAdapter.AppViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemInstalledAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val info = appsList[position]
        holder.binding.rbSelectApp.isChecked = if (SessionManager.getSets(SessionManager.SELECT_APP).contains(appsList[position].packageName)){
            true
        }else{
            false
        }

        val pm = holder.binding.ivAppIcon.context.packageManager
        // Set app name
        holder.binding.tvAppName.text = info.loadLabel(pm).toString()

        Glide.with(holder.binding.ivAppIcon.context)
            .load(info.loadIcon(pm))
            .into(holder.binding.ivAppIcon)

        holder.binding.rbSelectApp.setOnClickListener {
            onAppSelected(info)
        }
        /*holder.binding.rbSelectApp.setOnCheckedChangeListener { _, isChecked ->
            Log.d(" OnChange", "On Checked Chage = > ${info.packageName}:$isChecked")
            onAppSelected(info)
        }*/
    }

    override fun getItemCount(): Int = appsList.size

    inner class AppViewHolder(val binding: ItemInstalledAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}

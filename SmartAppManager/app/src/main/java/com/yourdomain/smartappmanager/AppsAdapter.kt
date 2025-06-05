package com.yourdomain.smartappmanager

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppsAdapter(
    private val apps: List<ApplicationInfo>,
    private val blockedApps: MutableSet<String>,
    private val onCheckedChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    private val selectionMap = mutableMapOf<String, Boolean>()

    inner class AppViewHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val name: TextView = view.findViewById(R.id.appName)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_checkbox, parent, false)
        return AppViewHolder(view)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        val context = holder.view.context
        val pm = context.packageManager

        holder.icon.setImageDrawable(pm.getApplicationIcon(app))
        holder.name.text = pm.getApplicationLabel(app)
        val packageName = app.packageName

        val isChecked = selectionMap[packageName] ?: blockedApps.contains(packageName)
        holder.checkBox.isChecked = isChecked
        selectionMap[packageName] = isChecked

        holder.checkBox.setOnCheckedChangeListener { _, checked ->
            selectionMap[packageName] = checked
            onCheckedChanged(packageName, checked)
        }
    }

    fun saveSelection() {
        // Save already handled in onCheckedChanged, but you can add logic here if needed
    }
}
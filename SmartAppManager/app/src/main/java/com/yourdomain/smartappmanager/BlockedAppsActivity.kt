package com.yourdomain.smartappmanager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BlockedAppsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppsAdapter
    private val blockedAppsPrefsKey = "blocked_apps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_apps)

        recyclerView = findViewById(R.id.recyclerViewApps)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val installedApps = getInstalledApps()
        val blockedApps = getBlockedApps()

        adapter = AppsAdapter(installedApps, blockedApps) { packageName, isChecked ->
            updateBlockedApps(packageName, isChecked)
        }
        recyclerView.adapter = adapter

        val btnSave = findViewById<Button>(R.id.btnSaveBlockedApps)
        btnSave.setOnClickListener {
            adapter.saveSelection()
            finish()
        }
    }

    private fun getInstalledApps(): List<ApplicationInfo> {
        val pm = packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { pm.getApplicationLabel(it).toString() }
    }

    private fun getBlockedApps(): MutableSet<String> {
        val prefs = getSharedPreferences("focus_mode_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet(blockedAppsPrefsKey, mutableSetOf()) ?: mutableSetOf()
    }

    private fun updateBlockedApps(packageName: String, isChecked: Boolean) {
        val prefs = getSharedPreferences("focus_mode_prefs", Context.MODE_PRIVATE)
        val blocked = prefs.getStringSet(blockedAppsPrefsKey, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if (isChecked) {
            blocked.add(packageName)
        } else {
            blocked.remove(packageName)
        }
        prefs.edit().putStringSet(blockedAppsPrefsKey, blocked).apply()
    }
}
package com.yourdomain.smartappmanager

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.IBinder
import android.util.Log

class UsageMonitorService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                checkUsage()
                Thread.sleep(60 * 1000) // every 1 minute
            }
        }.start()
        return START_STICKY
    }

    private fun checkUsage() {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 5 // last 5 minutes
        val usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

        usageStats?.maxByOrNull { it.totalTimeInForeground }?.let {
            Log.d("UsageCheck", "Top App: ${it.packageName}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
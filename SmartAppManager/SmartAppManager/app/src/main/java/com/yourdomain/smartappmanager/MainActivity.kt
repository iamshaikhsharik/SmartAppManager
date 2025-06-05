package com.yourdomain.smartappmanager

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPermissions = findViewById<Button>(R.id.btnPermissions)
        val btnStartMonitor = findViewById<Button>(R.id.btnStartMonitor)

        btnPermissions.setOnClickListener {
            requestAllPermissions()
        }

        btnStartMonitor.setOnClickListener {
            startService(Intent(this, UsageMonitorService::class.java))
            Toast.makeText(this, "Monitoring Started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestAllPermissions() {
        if (!hasUsageAccess()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivity(intent)
        }

        val devicePolicyIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        val componentName = ComponentName(this, DeviceAdminReceiver::class.java)
        devicePolicyIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        devicePolicyIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable admin to lock screen.")
        startActivity(devicePolicyIntent)
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            Binder.getCallingUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
package com.scanbug.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private lateinit var appSpinner: Spinner
    private val prefs by lazy { getSharedPreferences(ScanBugAccessibilityService.PREFS, Context.MODE_PRIVATE) }
    private var apps: List<LaunchableApp> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)

        content.addView(TextView(this).apply {
            text = "ScanBug — read-only Android UI explorer"
            textSize = 22f
        })
        content.addView(TextView(this).apply {
            text = "Inspect accessible controls in an app you choose. ScanBug does not tap controls, enter text, take screenshots, or save UI snapshots. You perform all actions yourself."
            textSize = 14f
            setPadding(0, 12, 0, 16)
        })
        appSpinner = Spinner(this)
        content.addView(appSpinner)
        content.addView(actionButton("Refresh app list") { loadApps() })
        content.addView(actionButton("Review access & enable service") { showAccessibilityDisclosure() })
        content.addView(actionButton("Open selected app") { launchSelectedApp() })
        content.addView(TextView(this).apply {
            text = "The Accessibility Service can inspect UI content exposed by the selected app while enabled. Use the ScanBug overlay and tap Explore screen. Inspection stays on this device and is not saved or sent by ScanBug."
            textSize = 13f
            setPadding(0, 18, 0, 18)
        })
        content.addView(actionButton("Stop ScanBug session") {
            prefs.edit().remove(ScanBugAccessibilityService.KEY_TARGET_PACKAGE).apply()
            Toast.makeText(this, "ScanBug session stopped", Toast.LENGTH_SHORT).show()
        })
        loadApps()
        if (!prefs.getBoolean(KEY_ACCESS_DISCLOSURE_ACCEPTED, false)) {
            root.post { showAccessibilityDisclosure() }
        }
    }

    private fun showAccessibilityDisclosure() {
        val message = """
            ScanBug uses Android Accessibility Service to inspect the visible interface of the app you select, including accessible control labels, descriptions and control types.

            This access can expose sensitive information displayed by that app. ScanBug uses the information only to show a read-only list on this device. This version does not tap, type, scroll, take screenshots, save screen contents, or send inspected content to a server.

            Enable the service only for apps you own or are authorized to test. You can decline now and enable it later from this screen. You can turn the service off at any time in Android Accessibility Settings.
        """.trimIndent()
        android.app.AlertDialog.Builder(this)
            .setTitle("Important: Accessibility access")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("I understand — continue") { _, _ ->
                prefs.edit().putBoolean(KEY_ACCESS_DISCLOSURE_ACCEPTED, true).apply()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton("Not now") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 8 }
    }

    private fun loadApps() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = packageManager.queryIntentActivities(intent, 0)
            .map { LaunchableApp(it.loadLabel(packageManager).toString(), it.activityInfo.packageName) }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
        appSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            apps.map { "${it.label}  (${it.packageName})" }
        )
        Toast.makeText(this, "Found ${apps.size} launchable apps", Toast.LENGTH_SHORT).show()
    }

    private fun launchSelectedApp() {
        val app = apps.getOrNull(appSpinner.selectedItemPosition) ?: return
        val launch = packageManager.getLaunchIntentForPackage(app.packageName)
        if (launch == null) {
            Toast.makeText(this, "No launch activity found", Toast.LENGTH_SHORT).show()
            return
        }
        prefs.edit().putString(ScanBugAccessibilityService.KEY_TARGET_PACKAGE, app.packageName).apply()
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launch)
    }

    companion object {
        private const val KEY_ACCESS_DISCLOSURE_ACCEPTED = "accessibility_disclosure_accepted"
    }

    data class LaunchableApp(val label: String, val packageName: String)
}

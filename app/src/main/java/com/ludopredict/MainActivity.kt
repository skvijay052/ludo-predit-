package com.ludopredict

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            setBackgroundColor(Color.rgb(248, 249, 252))
        }
        layout.addView(TextView(this).apply {
            text = "Ludo-Predict"
            textSize = 28f
            setTextColor(Color.rgb(24, 33, 55))
        })
        layout.addView(TextView(this).apply {
            text = "A move-advice assistant for your Ludo match. It does not predict random dice rolls."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(12), 0, dp(20))
        })
        val permission = Button(this).apply {
            text = "Allow floating assistant"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else startAssistant()
            }
        }
        layout.addView(permission)
        layout.addView(Button(this).apply {
            text = "Start assistant"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) startAssistant()
                else permission.performClick()
            }
        })
        layout.addView(TextView(this).apply {
            text = "\nHow to use\n1. Allow overlay permission.\n2. Start the assistant.\n3. Open Ludo King.\n4. Tap the floating L button for move-planning guidance.\n\nThis starter version uses a manual strategy checklist. Screenshot-based board recognition is a later phase."
            textSize = 15f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(18), 0, 0)
        })
        setContentView(layout)
    }

    private fun startAssistant() {
        startService(Intent(this, FloatingAssistantService::class.java))
        finish()
    }
}

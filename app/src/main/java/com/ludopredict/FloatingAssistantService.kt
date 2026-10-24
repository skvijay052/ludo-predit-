package com.ludopredict

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingAssistantService : Service() {
    private lateinit var windowManager: WindowManager
    private var bubble: View? = null
    private var panel: View? = null
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun params(width: Int, height: Int, gravity: Int = Gravity.TOP or Gravity.START) =
        WindowManager.LayoutParams(width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply { this.gravity = gravity; x = dp(12); y = dp(180) }

    private fun showBubble() {
        if (bubble != null) return
        val b = Button(this).apply {
            text = "L"
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(30, 90, 170)))
            setOnClickListener { if (panel == null) showPanel() else hidePanel() }
            setOnTouchListener(object : View.OnTouchListener {
                var startX = 0; var startY = 0; var downX = 0f; var downY = 0f
                override fun onTouch(v: View, e: MotionEvent): Boolean {
                    val p = bubble?.layoutParams as? WindowManager.LayoutParams ?: return false
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> { startX = p.x; startY = p.y; downX = e.rawX; downY = e.rawY; return false }
                        MotionEvent.ACTION_MOVE -> {
                            p.x = startX + (e.rawX - downX).toInt()
                            p.y = startY + (e.rawY - downY).toInt()
                            windowManager.updateViewLayout(v, p)
                            return true
                        }
                    }
                    return false
                }
            })
        }
        bubble = b
        windowManager.addView(b, params(dp(58), dp(58)))
    }

    private fun showPanel() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setBackgroundColor(Color.WHITE)
        }
        box.addView(TextView(this).apply {
            text = "Ludo-Predict • Move coach"
            textSize = 18f
            setTextColor(Color.rgb(25, 35, 55))
            setTypeface(null, Typeface.BOLD)
        })
        box.addView(TextView(this).apply {
            text = "Before moving, check these priorities:"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(8), 0, dp(6))
        })
        box.addView(TextView(this).apply {
            text = "1. Can you finish a token safely?\n2. Can you capture a valuable opponent token?\n3. Is one of your tokens in capture range?\n4. Can you reach a safe square?\n5. Otherwise, advance the token with the best overall position.\n\nDice rolls are not predictable from previous rolls."
            textSize = 14f
            setTextColor(Color.DKGRAY)
        })
        box.addView(Button(this).apply {
            text = "Choose move situation"
            setOnClickListener {
                val choices = arrayOf("I can finish a token", "I can capture an opponent", "My token may be captured", "No immediate threat")
                android.app.AlertDialog.Builder(this@FloatingAssistantService).setTitle("What is happening on the board?")
                    .setItems(choices) { _, which ->
                        val message = when (which) {
                            0 -> "Usually finish it if the move is legal and safe. Compare against a high-value capture."
                            1 -> "Capture if it meaningfully delays the opponent and doesn't expose a more valuable token."
                            2 -> "Prefer escaping to safety when the risk of losing progress outweighs your alternative move."
                            else -> "Advance toward home, keep multiple useful tokens active, and avoid unnecessary exposure."
                        }
                        Toast.makeText(this@FloatingAssistantService, message, Toast.LENGTH_LONG).show()
                    }.show()
            }
        })
        box.addView(Button(this).apply { text = "Close advice"; setOnClickListener { hidePanel() } })
        panel = box
        val p = params(dp(300), WindowManager.LayoutParams.WRAP_CONTENT)
        p.x = dp(12); p.y = dp(245)
        windowManager.addView(box, p)
    }

    private fun hidePanel() {
        panel?.let { try { windowManager.removeView(it) } catch (_: Exception) {} }
        panel = null
    }

    override fun onDestroy() {
        hidePanel()
        bubble?.let { try { windowManager.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

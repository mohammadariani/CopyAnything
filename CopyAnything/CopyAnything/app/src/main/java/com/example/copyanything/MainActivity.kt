package com.example.copyanything

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var output: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }

        val enableBtn = Button(this).apply {
            text = "فعال‌سازی سرویس دسترسی‌پذیری"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val grabBtn = Button(this).apply {
            text = "خواندن متن صفحهٔ بعدی (۵ ثانیه تأخیر)"
            setOnClickListener { grabWithDelay() }
        }

        val copyBtn = Button(this).apply {
            text = "کپی همه"
            setOnClickListener {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("grabbed", output.text.toString()))
                Toast.makeText(this@MainActivity, "کپی شد", Toast.LENGTH_SHORT).show()
            }
        }

        output = EditText(this).apply {
            hint = "متن اینجا نمایش داده می‌شود؛ می‌توانید بخشی را انتخاب و کپی کنید"
            gravity = Gravity.TOP
            setTextIsSelectable(true)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f)
        }

        layout.addView(enableBtn, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        layout.addView(grabBtn, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        layout.addView(copyBtn, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        layout.addView(output)
        setContentView(layout)
    }

    private fun grabWithDelay() {
        val service = TextGrabService.instance
        if (service == null) {
            Toast.makeText(this, "اول سرویس را فعال کنید", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, "به لینکدین بروید…", Toast.LENGTH_SHORT).show()
        moveTaskToBack(true)

        output.postDelayed({
            val text = service.grabScreenText()
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
            output.setText(text)
        }, 5000)
    }
}

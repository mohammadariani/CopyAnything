package com.example.copyanything

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TextGrabService : AccessibilityService() {

    companion object {
        var instance: TextGrabService? = null
    }

    override fun onServiceConnected() {
        instance = this
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    /** متن همهٔ المان‌های صفحهٔ فعلی را جمع می‌کند */
    fun grabScreenText(): String {
        val root = rootInActiveWindow ?: return ""
        val lines = mutableListOf<String>()
        collect(root, lines)
        return lines.distinct().joinToString("\n")
    }

    private fun collect(node: AccessibilityNodeInfo?, out: MutableList<String>) {
        if (node == null) return
        val text = node.text?.toString()?.trim()
        val desc = node.contentDescription?.toString()?.trim()
        if (!text.isNullOrEmpty()) out.add(text)
        else if (!desc.isNullOrEmpty() && node.childCount == 0) out.add(desc)

        for (i in 0 until node.childCount) {
            collect(node.getChild(i), out)
        }
    }
}

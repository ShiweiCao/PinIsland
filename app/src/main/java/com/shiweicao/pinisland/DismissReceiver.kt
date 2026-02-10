package com.shiweicao.pinisland

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 获取传递过来的 ID，默认为 -1
        val notificationId = intent.getIntExtra("notification_id", -1)

        if (notificationId != -1) {
            manager.cancel(notificationId)
        }
    }
}
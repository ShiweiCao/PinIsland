package com.shiweicao.pinisland

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "simple_pickup_channel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        findViewById<Button>(R.id.btnPin).setOnClickListener {
            val restaurant = findViewById<EditText>(R.id.etRestaurant).text.toString()
            val orderNum = findViewById<EditText>(R.id.etOrderNumber).text.toString()

            if (restaurant.isNotBlank() && orderNum.isNotBlank()) {
                postSimplePickupNotification(restaurant, orderNum)
            }
        }
    }

    private fun postSimplePickupNotification(restaurant: String, orderNumber: String) {
        val manager = getSystemService(NotificationManager::class.java)

        // 1. 权限检查
        if (Build.VERSION.SDK_INT >= 36) {
            if (!manager.canPostPromotedNotifications()) {
                Toast.makeText(this, "请在设置中允许 Live Updates", Toast.LENGTH_LONG).show()
                return
            }
        }

        // 2. 构建通知 (移除所有 ProgressStyle 代码)
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_map)

            // --- 展开后的样子 ---
            .setContentTitle(restaurant)             // 标题：麦当劳
            .setContentText("取餐号：$orderNumber")   // 内容：取餐号：A25

            // --- 折叠后的样子 (状态栏 Chip) ---
            .setShortCriticalText(orderNumber)       // <--- 核心！这里填 "A25"

            // .setStyle(...) <--- 删掉了！不设置 Style 就是标准样式

            .setOngoing(true)                        // 必须：常驻
            .setColorized(false)                     // 必须：非彩色背景
            .setCategory(Notification.CATEGORY_EVENT)// 改为 EVENT 或 STATUS

        // 3. 核心：请求提升为灵动岛 (Live Update)
        val extras = Bundle()
        extras.putBoolean("android.requestPromotedOngoing", true)
        builder.addExtras(extras)

        // 4. 添加关闭按钮
        val dismissIntent = Intent(this, DismissReceiver::class.java)
        val pendingDismiss = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE)
        val action = Notification.Action.Builder(
            Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel),
            "取餐完成",
            pendingDismiss
        ).build()
        builder.addAction(action)

        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, "取餐提醒", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
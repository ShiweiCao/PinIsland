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
    // 删掉了固定的 NOTIFICATION_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        val etRestaurant = findViewById<EditText>(R.id.etRestaurant)
        val etOrderNum = findViewById<EditText>(R.id.etOrderNumber)
        val btnPin = findViewById<Button>(R.id.btnPin)

        btnPin.setOnClickListener {
            val restaurant = etRestaurant.text.toString()
            val orderNum = etOrderNum.text.toString()

            if (restaurant.isNotBlank() && orderNum.isNotBlank()) {
                postSimplePickupNotification(restaurant, orderNum)

                // 3. 体验优化：成功后清空输入框
                etRestaurant.text.clear()
                etOrderNum.text.clear()
            } else {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show()
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

        // 2. 关键改动：生成唯一 ID
        // 使用当前时间戳作为 ID，确保每条通知互不干扰
        val notificationId = System.currentTimeMillis().toInt()

        // 3. 构建通知
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_map)

            // 展开后
            .setContentTitle(restaurant)
            .setContentText("取餐号：$orderNumber")

            // 折叠后 (状态栏 Chip)
            .setShortCriticalText(orderNumber)

            .setOngoing(true)
            .setColorized(false)
            .setCategory(Notification.CATEGORY_EVENT)

        // 请求提升为灵动岛
        val extras = Bundle()
        extras.putBoolean("android.requestPromotedOngoing", true)
        builder.addExtras(extras)

        // 添加关闭按钮 (这里的 ID 也需要唯一，或者让它可以处理不同 ID)
        // 我们把 notificationId 传给 Receiver，这样点击“取餐完成”时只消除当前这一条
        val dismissIntent = Intent(this, DismissReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
        }
        val pendingDismiss = PendingIntent.getBroadcast(
            this,
            notificationId, // RequestCode 也需要唯一，否则 PendingIntent 会被复用
            dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val action = Notification.Action.Builder(
            Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel),
            "取餐完成",
            pendingDismiss
        ).build()
        builder.addAction(action)

        manager.notify(notificationId, builder.build())

        // 4. 成功反馈
        Toast.makeText(this, "已生成：$restaurant $orderNumber", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, "取餐提醒", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
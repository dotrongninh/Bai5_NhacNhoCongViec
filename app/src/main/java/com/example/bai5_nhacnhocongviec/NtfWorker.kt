package com.example.bai5_nhacnhocongviec

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NtfWorker(context : Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {
    override fun doWork(): Result {
        val workData = inputData.getString("check") // Dữ liệu công việc từ input
        var notificationText : String = workData ?: "Rỗng"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "work_reminder_channel"
        val notificationId = 1


        var i = Intent(applicationContext,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, notificationChannelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nhắc nhở công việc")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        // Hiển thị notification
        notificationManager.notify(notificationId, notification)

        return Result.success()
    }
}
package com.example.bai5_nhacnhocongviec

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val tasks = ArrayList<Task>()
    private lateinit var adapter: ArrayAdapter<String>

    val uri = Uri.parse("content://com.example.bai4_quanlycongviec/tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        listView = findViewById(R.id.taskListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter


        var check : String
        val cursor: Cursor? = contentResolver.query(Uri.parse("content://com.example.bai4_quanlycongviec/tasks"), null, null, null, null)
        if (cursor!=null) { Log.d("MainActivity", "Cursor not null aaa")
        check = "yes"}
        else  {Log.d("MainActivity", "nulllllllllllllllllll")
            check = "no"}
        cursor?.let {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val date = it.getString(it.getColumnIndexOrThrow("date"))
                tasks.add(Task(id, name, date))
                adapter.add("Tên: $name - Năm sinh: $date")
            }
        }
        var taskString = tasks.joinToString(separator = "\n")
        adapter.notifyDataSetChanged()
       createNotificationChannel()
        scheduleDailyReminder(taskString)

    }
    override fun onResume() {
        super.onResume()

        //adapter.clear()

    }


    fun scheduleDailyReminder(check : String) {
        val currentTime = System.currentTimeMillis()

        // Tính toán thời gian còn lại đến 6h sáng hôm sau
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        calendar.set(Calendar.MINUTE, 55)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < currentTime) {
            calendar.add(Calendar.DATE, 1) // Nếu thời gian 6h sáng hôm nay đã qua thì chọn 6h sáng ngày mai
        }

        val initialDelay = calendar.timeInMillis - currentTime


        val inputData = androidx.work.Data.Builder()
            .putString("check",check)
            .build()


        val reminderWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<NtfWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(reminderWorkRequest)
    }
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Work Reminder Channel"
            val descriptionText = "Channel for daily work reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("work_reminder_channel", name, importance)
            channel.description = descriptionText

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
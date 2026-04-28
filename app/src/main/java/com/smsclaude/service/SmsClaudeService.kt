package com.smsclaude.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.smsclaude.MainActivity
import com.smsclaude.data.repository.SettingsRepository
import com.smsclaude.engine.SendingEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsClaudeService : LifecycleService() {

    companion object {
        const val CHANNEL_ID        = "sms_claude_channel"
        const val NOTIFICATION_ID   = 1001
        const val ACTION_STOP       = "com.smsclaude.ACTION_STOP"
        const val ACTION_PROCESS_SMS = "com.smsclaude.ACTION_PROCESS_SMS"
        const val EXTRA_SENDER      = "extra_sender"
        const val EXTRA_BODY        = "extra_body"
        const val EXTRA_TIMESTAMP   = "extra_timestamp"
    }

    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        createNotificationChannel()
      
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      
        startForeground(NOTIFICATION_ID, buildNotification())

        when (intent?.action) {
            ACTION_STOP -> {
                CoroutineScope(Dispatchers.IO).launch {
                    userInitiatedStop()
                }
                return START_NOT_STICKY
            }

            ACTION_PROCESS_SMS -> {
              
                val sender    = intent.getStringExtra(EXTRA_SENDER)    ?: return START_STICKY
                val body      = intent.getStringExtra(EXTRA_BODY)      ?: return START_STICKY
                val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())

                CoroutineScope(Dispatchers.IO).launch {
                    SendingEngine(applicationContext).process(sender, body, timestamp)
                }
            }

            else -> {
               
                CoroutineScope(Dispatchers.IO).launch {
                    settingsRepository.setUserStopped(false)
                    settingsRepository.setServiceRunning(true)
                }
            }
        }

        return START_STICKY
    }

    private suspend fun userInitiatedStop() {
        settingsRepository.setUserStopped(true)
        settingsRepository.setServiceRunning(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
      
        val restartIntent = Intent(applicationContext, SmsClaudeService::class.java)
        val pendingIntent = PendingIntent.getService(
            applicationContext, 1, restartIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 500,
            pendingIntent
        )
    }

    override fun onDestroy() {
        super.onDestroy()
 
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("SMS Claude")
            .setContentText("Running in background")
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SMS Claude Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps SMS actions active in background"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}

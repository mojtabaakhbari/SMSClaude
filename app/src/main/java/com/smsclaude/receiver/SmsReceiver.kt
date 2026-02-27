package com.smsclaude.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.smsclaude.data.repository.SettingsRepository
import com.smsclaude.service.SmsForwarderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages  = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val sender    = messages.firstOrNull()?.originatingAddress ?: return
        val body      = messages.joinToString("") { it.messageBody }
        val timestamp = System.currentTimeMillis()

      
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = SettingsRepository(context).getSettings()
            
                if (settings.isUserStopped) return@launch

         
                val serviceIntent = Intent(context, SmsForwarderService::class.java).apply {
                    action = SmsForwarderService.ACTION_PROCESS_SMS
                    putExtra(SmsForwarderService.EXTRA_SENDER,    sender)
                    putExtra(SmsForwarderService.EXTRA_BODY,      body)
                    putExtra(SmsForwarderService.EXTRA_TIMESTAMP, timestamp)
                }
                context.startForegroundService(serviceIntent)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

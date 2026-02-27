package com.smsclaude.engine

import android.content.Context
import android.telephony.SmsManager
import com.smsclaude.data.model.LogEntry
import com.smsclaude.data.model.LogStatus
import com.smsclaude.data.model.RecentActivity
import com.smsclaude.data.repository.ActivityRepository
import com.smsclaude.data.repository.LogRepository
import com.smsclaude.data.repository.RulesRepository
import com.smsclaude.data.repository.SettingsRepository
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ForwardingEngine(private val context: Context) {

    suspend fun process(sender: String, body: String, timestamp: Long) {
        val rulesRepo = RulesRepository(context)
        val settingsRepo = SettingsRepository(context)
        val logRepo = LogRepository(context)
        val activityRepo = ActivityRepository(context)

        val rules = rulesRepo.getRules()
        val settings = settingsRepo.getSettings()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        if (rules.isEmpty()) {
           
            return
        }

        var matched = false
        for (rule in rules.filter { it.enabled }) {
            val sourceMatch = rule.sources.contains("ANY") || rule.sources.any { src ->
                src.trim() == sender.trim()
            }
            val keywordMatch = rule.keyword.isBlank() || body.contains(rule.keyword, ignoreCase = true)

            if (sourceMatch && keywordMatch) {
                matched = true
                if (settings.forwardDelay > 0) {
                    delay(settings.forwardDelay * 1000L)
                }

                for (destination in rule.destinations) {
                    val prefix = if (settings.prefix.isNotBlank()) settings.prefix else ""
                    val suffix = if (settings.suffix.isNotBlank()) settings.suffix else ""
                    val forwardedBody = "${prefix}$body${suffix}"

                    val status = sendSms(destination, forwardedBody)

                    val logEntry = LogEntry(timestamp, sender, destination, body.take(80), status)
                    logRepo.addEntry(logEntry)

                    val activity = RecentActivity(
                        timestamp = timestamp,
                        sender = sender,
                        destination = destination,
                        preview = body.take(60),
                        status = status
                    )
                    activityRepo.addActivity(activity)

                    if (status == LogStatus.FORWARDED) {
                        settingsRepo.incrementForwardedCount(today)
                    }
                }
            }
        }

        if (!matched) {
       
        }
    }

    private fun sendSms(to: String, message: String): LogStatus {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)
                ?: SmsManager.getDefault()

       
            val dividedParts = smsManager.divideMessage(message)
            

            var smsCharCount = 0
            for (char in message) {
                smsCharCount += when {
                    char.code in 0x0000..0x007F -> 1 
                    char.code in 0x0600..0x06FF || char.code in 0x0750..0x077F || char.code in 0xFB50..0xFDFF || char.code in 0xFE70..0xFEFF -> 2 
                    char.code in 0x0590..0x05FF -> 2 
                    else -> 2 
                }
            }
            
            println("SMS Forward Debug: Java length: ${message.length}, SMS chars: $smsCharCount, Parts: ${dividedParts.size}")
            println("SMS Forward Debug: To: $to, Message preview: ${message.take(30)}...")
            
            if (dividedParts.size > 1) {
                
                println("SMS Forward Debug: Sending multipart SMS with ${dividedParts.size} parts")
                smsManager.sendMultipartTextMessage(to, null, dividedParts, null, null)
            } else {
             
                println("SMS Forward Debug: Sending single part SMS")
                smsManager.sendTextMessage(to, null, message, null, null)
            }
            LogStatus.FORWARDED
        } catch (e: Exception) {
            println("SMS Forward Debug: Failed to send SMS - ${e.message}")
            e.printStackTrace()
            LogStatus.FAILED
        }
    }
}

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

class SendingEngine(private val context: Context) {

    suspend fun process(sender: String, body: String, timestamp: Long) {
        val rulesRepo = RulesRepository(context)
        val settingsRepo = SettingsRepository(context)
        val logRepo = LogRepository(context, settingsRepo)
        val activityRepo = ActivityRepository(context)
        val rules = rulesRepo.getRules()
        val settings = settingsRepo.getSettings()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        if (rules.isNotEmpty()) {
            for (rule in rules.filter { it.enabled }) {
                val sourceMatch = rule.sources.contains("ANY") || rule.sources.any { src ->
                    src.trim() == sender.trim()
                }
                val keywordMatch = rule.keyword.isBlank() || body.contains(rule.keyword, ignoreCase = true)
                val default_text = rule.default_text
                if (sourceMatch && keywordMatch) {
                    if (settings.sentDelay > 0) {
                        delay(settings.sentDelay * 1000L)
                    }

                    for (destination in rule.destinations) {
                        val prefix = if (settings.prefix.isNotBlank()) settings.prefix else ""
                        val suffix = if (settings.suffix.isNotBlank()) settings.suffix else ""

                        val smsBody = if (default_text.isNotBlank()) "${prefix}$default_text${suffix}" else "${prefix}$body${suffix}"


                        val status = sendSms(destination, smsBody, default_text.isNotBlank())

                        val logEntry = LogEntry(timestamp, sender, destination, smsBody.take(80), status)
                        logRepo.addEntry(logEntry)

                        val activity = RecentActivity(
                            timestamp = timestamp,
                            sender = sender,
                            destination = destination,
                            preview = smsBody.take(60),
                            status = status
                        )
                        activityRepo.addActivity(activity)

                        if (status == LogStatus.FORWARDED || status == LogStatus.REPLIED) {
                            settingsRepo.incrementSentCount(today)
                        }
                    }
                }
            }
        }
    }

    private fun sendSms(to: String, message: String, replied: Boolean): LogStatus {
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

            if (dividedParts.size > 1) {
                smsManager.sendMultipartTextMessage(to, null, dividedParts, null, null)
            } else {
                smsManager.sendTextMessage(to, null, message, null, null)
            }
            if (replied) LogStatus.REPLIED else LogStatus.FORWARDED
        } catch (e: Exception) {
            e.printStackTrace()
            if (replied) LogStatus.RPL_FAILED else LogStatus.FWD_FAILED
        }
    }
}

package com.smsclaude.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SmsRule(
    val id: String = UUID.randomUUID().toString(),
    val sources: List<String> = listOf("ANY"),
    val destinations: List<String>,
    val keyword: String = "",
    val default_text: String = "",
    val enabled: Boolean = true
)

@Serializable
data class LogEntry(
    val timestamp: Long,
    val sender: String,
    val destination: String,
    val preview: String,
    val status: LogStatus
)

@Serializable
data class RecentActivity(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long,
    val sender: String,
    val destination: String,
    val preview: String,
    val status: LogStatus
)

@Serializable
enum class LogStatus { FORWARDED, REPLIED, FWD_FAILED, RPL_FAILED }

@Serializable
data class AppSettings(
    val startOnBoot: Boolean = false,
    val sentDelay: Int = 0,
    val prefix: String = "",
    val suffix: String = "",
    val isUserStopped: Boolean = true,
    val isServiceRunning: Boolean = false,
    val totalSent: Int = 0,
    val todaySent: Int = 0,
    val lastSentTimestamp: Long = 0L,
    val lastSentDate: String = "",
    val lastCheckTodayCountDate: String = ""
)

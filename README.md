# 📱 SMSClaude — Smart SMS Forwarder & Auto-Responder

> **Intelligent SMS handling made simple** - A powerful native Android app that automatically forwards and replies to your SMS messages based on customizable rules with a sleek dark interface.

![Android](https://img.shields.io/badge/Android-8.0%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Modern-purple?logo=jetpackcompose)
![API](https://img.shields.io/badge/API-26%2B-orange)

---

## ✨ Key Features

🚀 **Smart Forwarding** - Create custom rules with sender filters and keyword matching  
💬 **Auto-Reply** - Send automated replies with customizable message templates  
🎨 **Beautiful UI** - Dark industrial theme with Material 3 and smooth animations  
🔋 **Battery Optimized** - Foreground service with intelligent power management  
🔐 **Secure & Private** - All data stored locally with DataStore persistence  
📊 **Real-time Monitoring** - Live activity feed and detailed SMS handling logs  
⚡ **Auto-restart** - Survives system kills and device reboots  
🎯 **Rule Validation** - Real-time regex validation and error prevention  
📱 **Modern Architecture** - MVVM with Jetpack Compose and Coroutines

## � Quick Start

### 📋 Prerequisites

- **Android Studio Hedgehog (2023.1.1) or later**
- **JDK 11+**
- **Android device or emulator** running Android 8.0+ (API 26+)
- ⚠️ **Real device required** for SMS testing (emulators cannot receive SMS)

### 🔧 Installation Steps

1. **📂 Open Project**

   ```bash
   File → Open → select the SMSClaude folder
   ```

2. **🔄 Sync Gradle**
   - Auto-sync on open, or manually: `File → Sync Project with Gradle Files`

3. **🎨 Add Launcher Icons** (required before first build)
   - `File → New → Image Asset`
   - Type: Launcher Icons
   - Name: `ic_launcher`

4. **▶️ Build & Run**

   ```
   Run → Run 'app'  (Shift+F10)
   ```

5. **🔐 Grant Permissions** when prompted on device

---

## 🏗️ Architecture Overview

```
📁 app/src/main/java/com/smsclaude/
├── 📂 data/
│   ├── 📄 model/          ForwardingRule, LogEntry, RecentActivity, AppSettings
│   └── 📄 repository/     DataStore-backed repositories
├── ⚙️ engine/             SendingEngine.kt — SMS matching & sending logic
├── 🔐 permission/         PermissionManager.kt — runtime permission checks
├── 📡 receiver/           SmsReceiver.kt — BroadcastReceiver for incoming SMS
├── 🔄 service/            SmsClaudeService.kt, BootReceiver.kt
├── 🎨 ui/
│   ├── 🧩 components/     PulsingDot, StatusBar, ActivityCard, RuleCard
│   ├── 📱 screens/        Dashboard, Rules, Logs, Settings
│   └── 🎨 theme/          Dark teal aesthetic with Material 3
├── 🧠 viewmodel/          Dashboard, Rules, Logs, Settings ViewModels
└── 🏠 MainActivity.kt     NavHost, permission dialogs, bottom nav
```

---

## 🎯 Core Features

| Feature                 | Description                                                              |
| ----------------------- | ------------------------------------------------------------------------ |
| **🔄 Service Survival** | `START_STICKY` + AlarmManager reschedule + persistent foreground service |
| **🚀 Boot Start**       | Automatic startup on device boot with user preference respect            |
| **🔐 Permission Gate**  | Comprehensive permission checks with non-dismissible dialogs             |
| **📊 Real-time Feed**   | StateFlow-driven activity feed with smooth card animations               |
| **💬 Auto-Reply Logic** | Conditional replies based on rule templates vs. original message forward |
| **🎨 Custom Toasts**    | Animated pill toasts with queue management and progress indicators       |
| **✅ Rule Validation**  | Real-time regex validation with disabled save until valid                |
| **⏰ Timestamps**       | Beautiful `dd MMM · HH:mm:ss` format on every activity card              |
| **🔍 Log Filtering**    | Filter by ALL / FORWARDED / REPLIED / FAILED statuses                    |
| **🗑️ Clear Dialogs**    | Confirmation dialogs for clearing logs and activity history              |
| **🌙 Dark UI**          | `#0f0f14` background with `#00e5cc` teal accent and monospace fonts      |

---

## ⚙️ Technical Implementation

### 🔄 Service Lifecycle

- **In-app toggle only** can stop the service (`isUserStopped = true`)
- System kills → `START_STICKY` restarts → checks user stop flag
- Task removal → `AlarmManager` reschedules restart in 500ms

### 📨 SMS Handling Flow

```
📡 SmsReceiver → ⚙️ SendingEngine.process()
  → 📂 Load rules from DataStore
  → 🎯 Match sender + keyword patterns
  → ⏱️ Apply delay (if configured)
  → 💬 Check: Reply template set?
    ✅ Yes → Send custom reply (LogStatus.REPLIED)
    ❌ No → Forward original message (LogStatus.FORWARDED)
  → 📤 SmsManager.sendTextMessage()
  → 📝 Write to LogRepository + ActivityRepository
  → 📈 Increment stats in SettingsRepository
```

### 🔐 Permission System (every onResume)

```
📱 RECEIVE_SMS + READ_SMS + SEND_SMS + POST_NOTIFICATIONS (Android 13+)
🔋 + Battery Optimization disabled
→ ❌ If any missing: show non-dismissible permission dialog
→ ✅ canStartService() gates the service toggle functionality
```

---

## 📱 App Screens

1. **🏠 Dashboard** - Service toggle with pulsing dot, statistics (today/total/last), live activity feed
2. **📋 Rules** - Create/manage forwarding & reply rules with ModalBottomSheet and real-time validation
3. **📜 Logs** - Complete event log with FORWARDED/REPLIED/FAILED status filters and clear confirmation
4. **⚙️ Settings** - Boot toggle, SMS handling delay slider (0–60s), prefix/suffix, about section

---

## 🧪 Testing Guide

### 📱 On Real Device:

1. ✅ Grant all permissions when prompted
2. 🔋 Disable battery optimization for the app
3. 📝 Create test rule: Sources = `ANY`, Destination = your second phone number
4. 💬 **Reply Test**: Add "Reply Message" like "I'm busy, will reply later"
5. 🎯 Toggle service ON
6. 📨 Send an SMS to the device:
   - **No reply template** → Original message forwarded
   - **With reply template** → Custom reply sent instead

---

## �️ Tech Stack

- **🎨 UI Framework**: Jetpack Compose with Material 3
- **🔧 Language**: Kotlin 1.9+
- **📱 Architecture**: MVVM with Repository pattern
- **💾 Storage**: DataStore Preferences (no SQLite needed)
- **⚡ Async**: Coroutines & StateFlow
- **🔐 Permissions**: Runtime permission handling
- **🔋 Services**: Foreground Service with BootReceiver

---

## 📝 Development Notes

- 🎨 Launcher icon (`@mipmap/ic_launcher`) must exist before building
- 📱 Service notification uses placeholder icon - replace with custom `ic_sms_forward` for production
- 💾 DataStore handles all persistence - no Room/SQLite dependency
- 📊 Auto-trimming: Max 500 log entries, max 50 activity entries (oldest removed first)
- 🔒 All data stored locally - no network calls or external dependencies
- 🔄 Service renamed from `SmsForwarderService` to `SmsClaudeService` for better branding
- ⚙️ Engine refactored from `ForwardingEngine` to `SendingEngine` with improved SMS processing logic
- 💬 Reply functionality: Rules can send custom replies instead of forwarding original messages
- 📝 LogStatus enum includes REPLIED and RPL_FAILED for reply tracking

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

<div align="center">

**📱 Made with ❤️ using Kotlin & Jetpack Compose**

[🔝 Back to top](#-smsclaude--smart-sms-forwarder)

</div>

-keep class com.smsclaude.data.model.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

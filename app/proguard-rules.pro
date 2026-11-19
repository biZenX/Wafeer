# Project ProGuard configuration for Wafeer

# Glance resolves which receivers belong to a GlanceAppWidget through a persisted map keyed by
# the widget class' canonical name (GlanceAppWidgetManager.getGlanceIds).
-keep class * extends androidx.glance.appwidget.GlanceAppWidget
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver

-keep class com.googlecode.tesseract.android.** { *; }
-keep class com.googlecode.leptonica.android.** { *; }

# Compose Animation rules
-keep class androidx.compose.animation.** { *; }
-dontwarn androidx.compose.animation.**

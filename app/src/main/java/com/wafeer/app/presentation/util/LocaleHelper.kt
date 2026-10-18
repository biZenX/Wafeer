package com.wafeer.app.presentation.util

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object LocaleHelper {

    fun currentLanguageCode(): String {
        val appLang = runCatching {
            val appLocales = AppCompatDelegate.getApplicationLocales()
            if (appLocales != null && !appLocales.isEmpty) {
                appLocales.get(0)?.language
            } else {
                null
            }
        }.getOrNull()

        val raw = if (!appLang.isNullOrBlank()) {
            appLang
        } else {
            runCatching { Locale.getDefault()?.language }.getOrNull().orEmpty()
        }
        val clean = raw.lowercase(Locale.ROOT)
            .substringBefore('-')
            .substringBefore('_')
            .trim()
        return if (clean.startsWith("ar")) "ar" else clean.ifBlank { "en" }
    }

    fun isArabic(): Boolean {
        val code = currentLanguageCode()
        return code == "ar" || code.startsWith("ar")
    }

    fun isEnglish(): Boolean = currentLanguageCode() == "en"
}

package com.wafeer.app.presentation.ui.settings.categories

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wafeer.app.domain.model.Category

object CategoryLocalizationHelper {

    fun getDisplayName(context: Context, category: Category): String {
        val key = category.nameResKey
        if (!key.isNullOrBlank()) {
            val resId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resId != 0) {
                return context.getString(resId)
            }
        }
        return category.name
    }

    @Composable
    fun getDisplayName(category: Category): String {
        val key = category.nameResKey
        if (!key.isNullOrBlank()) {
            val context = LocalContext.current
            val resId = remember(key) {
                context.resources.getIdentifier(key, "string", context.packageName)
            }
            if (resId != 0) {
                return stringResource(resId)
            }
        }
        return category.name
    }
}

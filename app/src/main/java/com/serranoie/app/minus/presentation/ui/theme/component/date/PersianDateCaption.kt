package com.serranoie.app.minus.presentation.ui.theme.component.date

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.serranoie.app.minus.domain.time.CalendarSystem
import com.serranoie.app.minus.presentation.util.font.format.formatLocalized
import java.time.Instant
import java.time.ZoneOffset

@Composable
fun PersianDateCaption(selectedDateMillis: Long?, modifier: Modifier = Modifier) {
    val locale = LocalConfiguration.current.locales[0]
    if (selectedDateMillis == null || CalendarSystem.of(locale) != CalendarSystem.PERSIAN) return
    val date = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
    Text(
        text = date.formatLocalized("EEEE d MMMM yyyy", locale),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

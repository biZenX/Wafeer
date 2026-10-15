package com.serranoie.app.minus.presentation.ui.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.serranoie.app.minus.presentation.ui.theme.MinusTheme
import com.serranoie.app.minus.presentation.ui.theme.component.date.PersianDateCaption
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

class PersianDateCaptionScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(locale = "fa-rIR"),
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 5.0,
    )

    private val previousLocale = Locale.getDefault()

    @After
    fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun persianCaptionShowsJalaliSelection() {
        Locale.setDefault(Locale.forLanguageTag("fa-IR"))
        val millis = LocalDate.of(2026, 10, 13).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        paparazzi.snapshot {
            MinusTheme {
                Surface {
                    Box(Modifier.width(320.dp).heightIn(min = 24.dp).padding(16.dp)) {
                        PersianDateCaption(selectedDateMillis = millis)
                    }
                }
            }
        }
    }
}

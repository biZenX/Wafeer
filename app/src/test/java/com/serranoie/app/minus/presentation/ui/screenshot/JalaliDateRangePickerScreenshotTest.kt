package com.serranoie.app.minus.presentation.ui.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.serranoie.app.minus.presentation.ui.theme.MinusTheme
import com.serranoie.app.minus.presentation.ui.theme.component.date.JalaliDateRangePicker
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class JalaliDateRangePickerScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(locale = "fa-rIR"),
        maxPercentDifference = 5.0,
    )

    private val previousLocale = Locale.getDefault()

    @After
    fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun persianRangeAcrossMehrAndAban() {
        Locale.setDefault(Locale.forLanguageTag("fa-IR"))
        paparazzi.snapshot {
            MinusTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(Modifier.fillMaxSize()) {
                        JalaliDateRangePicker(
                            startDate = LocalDate.of(2026, 10, 16),
                            endDate = LocalDate.of(2026, 10, 26),
                            onSelectionChange = { _, _ -> },
                            modifier = Modifier.fillMaxSize(),
                            today = LocalDate.of(2026, 10, 16),
                        )
                    }
                }
            }
        }
    }
}

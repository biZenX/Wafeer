package com.serranoie.app.minus.presentation.ui.screenshot

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.serranoie.app.minus.presentation.ui.theme.MinusTheme
import com.serranoie.app.minus.presentation.ui.theme.component.date.HistoryDateDivider
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale

class HistoryDateDividerScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    @Test
    fun historyDateDividerExpanded() {
        Locale.setDefault(Locale.US)
        paparazzi.snapshot {
            MinusTheme {
                HistoryDateDivider(
                    date = LocalDate.of(2026, 1, 15),
                    isExpanded = true,
                    onToggleClick = {},
                    totalAmount = BigDecimal("125.75"),
                    currencyCode = "\$"
                )
            }
        }
    }
}
class HistoryDateDividerPersianScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(locale = "fa-rIR"),
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 10.0,
    )

    private val previousLocale = Locale.getDefault()

    @After
    fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun historyDateDividerShowsJalaliDate() {
        Locale.setDefault(Locale.forLanguageTag("fa-IR"))
        paparazzi.snapshot {
            MinusTheme {
                HistoryDateDivider(
                    date = LocalDate.of(2026, 1, 15),
                    isExpanded = true,
                    onToggleClick = {},
                    totalAmount = BigDecimal("125.75"),
                    currencyCode = "\$"
                )
            }
        }
    }
}

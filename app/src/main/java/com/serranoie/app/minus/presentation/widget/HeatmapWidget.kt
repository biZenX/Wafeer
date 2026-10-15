@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.serranoie.app.minus.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.serranoie.app.minus.R
import com.serranoie.app.minus.domain.time.CalendarSystem
import com.serranoie.app.minus.presentation.util.font.format.formatLocalized
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

data class DailySpending(
    val dayOfMonth: Int,
    val spending: BigDecimal,
    val budget: BigDecimal,
    val transactionCount: Int,
)

data class MonthHeatmapData(
    val monthStart: LocalDate,
    val days: List<DailySpending>,
)

internal data class MonthCellState(
    val dayNumber: Int,
    val ratio: Float,
    val hasSpending: Boolean,
)

private data class CalendarCellState(
    val dayNumber: Int?,
    val ratio: Float,
    val hasSpending: Boolean,
)

private const val MONTHS_TO_RENDER = 4
private const val GRID_COLUMNS = 7
private const val GRID_ROWS = 6
private const val MAX_DAYS_PER_MONTH = 31

private fun monthStartKey(index: Int) = longPreferencesKey("heatmap_month_start_$index")
private fun ratioKey(monthIndex: Int, day: Int) =
    floatPreferencesKey("heatmap_ratio_${monthIndex}_$day")

private fun hasSpendingKey(monthIndex: Int, day: Int) =
    intPreferencesKey("heatmap_has_spending_${monthIndex}_$day")

private fun heatColorRes(ratio: Float, hasSpending: Boolean): Int {
    if (!hasSpending) return R.color.widget_heatmap_empty_dark

    val normalized = ratio.coerceIn(0f, 1.4f)
    return when {
        normalized <= 0.2f -> R.color.widget_heatmap_good_1
        normalized <= 0.45f -> R.color.widget_heatmap_good_2
        normalized <= 0.7f -> R.color.widget_heatmap_warn_1
        normalized <= 0.95f -> R.color.widget_heatmap_warn_2
        normalized <= 1.2f -> R.color.widget_heatmap_bad_1
        else -> R.color.widget_heatmap_bad_2
    }
}

private fun calculateSpendingRatio(
    spending: BigDecimal,
    budget: BigDecimal,
    transactionCount: Int,
    maxTransactions: Int,
): Float {
    val amountRatio = if (budget > BigDecimal.ZERO) {
        (spending.toFloat() / budget.toFloat()).coerceAtLeast(0f)
    } else {
        0f
    }

    val countRatio = if (maxTransactions > 0) {
        (transactionCount.toFloat() / maxTransactions.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    return (amountRatio * 0.6f + countRatio * 0.4f).coerceIn(0f, 1.4f)
}

private fun monthTitle(monthStart: LocalDate): String {
    return monthStart.formatLocalized("LLLL").uppercase(Locale.getDefault())
}

private fun firstDayOffset(monthStart: LocalDate): Int {
    val first = monthStart.dayOfWeek
    return (first.value - DayOfWeek.MONDAY.value + 7) % 7
}

private fun buildCalendarCells(
    monthStart: LocalDate,
    monthCells: List<MonthCellState>,
): List<CalendarCellState> {
    val daysInMonth = CalendarSystem.current().lengthOfMonth(monthStart)
    val offset = firstDayOffset(monthStart)
    val byDay = monthCells.associateBy { it.dayNumber }

    return List(GRID_COLUMNS * GRID_ROWS) { index ->
        val day = index - offset + 1
        if (day in 1..daysInMonth) {
            val cell = byDay[day]
            CalendarCellState(
                dayNumber = day,
                ratio = cell?.ratio ?: 0f,
                hasSpending = cell?.hasSpending == true,
            )
        } else {
            CalendarCellState(dayNumber = null, ratio = 0f, hasSpending = false)
        }
    }
}

@Composable
private fun DayCell(cell: CalendarCellState) {
    val hasDay = cell.dayNumber != null

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(2.dp)
            .cornerRadius(4.dp)
            .background(
                if (hasDay) heatColorRes(
                    cell.ratio,
                    cell.hasSpending
                ) else android.R.color.transparent
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (hasDay) {
            Text(
                text = String.format(Locale.getDefault(), "%d", cell.dayNumber),
                style = TextStyle(
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (cell.hasSpending) {
                        GlanceTheme.colors.surface
                    } else {
                        GlanceTheme.colors.onSurfaceVariant
                    },
                ),
            )
        }
    }
}

@Composable
private fun MonthGrid(
    monthStart: LocalDate,
    monthCells: List<MonthCellState>,
    modifier: GlanceModifier = GlanceModifier,
) {
    val calendarCells = buildCalendarCells(monthStart, monthCells)

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = monthTitle(monthStart),
            style = TextStyle(
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onSurfaceVariant,
            ),
            modifier = GlanceModifier.padding(bottom = 2.dp),
        )

        Column(modifier = GlanceModifier.fillMaxSize()) {
            repeat(GRID_ROWS) { row ->
                Row(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                ) {
                    repeat(GRID_COLUMNS) { column ->
                        val cell = calendarCells[row * GRID_COLUMNS + column]
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .fillMaxHeight()
                                .padding(1.dp),
                        ) {
                            DayCell(cell)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun HeatmapContent(
    months: List<LocalDate>,
    monthCells: List<List<MonthCellState>>,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
    ) {
        repeat(2) { row ->
            Row(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxWidth(),
            ) {
                repeat(2) { column ->
                    val index = row * 2 + column
                    val monthStart = months.getOrNull(index)
                    val cells = monthCells.getOrNull(index).orEmpty()

                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .padding(2.dp),
                    ) {
                        if (monthStart != null) {
                            MonthGrid(
                                monthStart = monthStart,
                                monthCells = cells,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun defaultMonthStart(index: Int): LocalDate {
    val calendar = CalendarSystem.current()
    return calendar.plusMonths(calendar.firstDayOfMonth(LocalDate.now()), index - MONTHS_TO_RENDER + 1L)
}

class HeatmapWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val months = (0 until MONTHS_TO_RENDER).map { index ->
                    prefs[monthStartKey(index)]?.let(LocalDate::ofEpochDay) ?: defaultMonthStart(index)
                }

                val monthCells = (0 until MONTHS_TO_RENDER).map { monthIndex ->
                    (1..MAX_DAYS_PER_MONTH).map { day ->
                        MonthCellState(
                            dayNumber = day,
                            ratio = prefs[ratioKey(monthIndex, day)] ?: 0f,
                            hasSpending = (prefs[hasSpendingKey(monthIndex, day)] ?: 0) == 1,
                        )
                    }
                }

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .clickable(actionRunCallback<OpenAppAction>())
                        .padding(8.dp)
                ) {
                    HeatmapContent(
                        months = months,
                        monthCells = monthCells,
                    )
                }
            }
        }
    }
}

class HeatmapWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HeatmapWidget()
}

suspend fun updateHeatmapWidget(
    context: Context,
    monthsData: List<MonthHeatmapData>,
) {
    val maxTransactions = monthsData
        .flatMap { it.days }
        .maxOfOrNull { it.transactionCount }
        ?.coerceAtLeast(1) ?: 1

    val normalizedMonths = monthsData.take(MONTHS_TO_RENDER)
    val glanceIds = boundGlanceIds(context, HeatmapWidgetReceiver::class.java, HeatmapWidget::class.java)

    glanceIds.forEach { glanceId ->
        updateAppWidgetState(context, glanceId) { prefs ->
            for (monthIndex in 0 until MONTHS_TO_RENDER) {
                val month = normalizedMonths.getOrNull(monthIndex)
                prefs[monthStartKey(monthIndex)] = (month?.monthStart ?: defaultMonthStart(monthIndex)).toEpochDay()

                for (day in 1..MAX_DAYS_PER_MONTH) {
                    val spending = month?.days?.firstOrNull { it.dayOfMonth == day }
                    val ratio = if (spending != null) {
                        calculateSpendingRatio(
                            spending = spending.spending,
                            budget = spending.budget,
                            transactionCount = spending.transactionCount,
                            maxTransactions = maxTransactions,
                        )
                    } else {
                        0f
                    }
                    prefs[ratioKey(monthIndex, day)] = ratio
                    prefs[hasSpendingKey(monthIndex, day)] =
                        if ((spending?.transactionCount ?: 0) > 0) 1 else 0
                }
            }
        }
        HeatmapWidget().update(context, glanceId)
    }
}

@Preview(widthDp = 320, heightDp = 220)
@Composable
private fun HeatmapContentPreview() {
    val months = listOf(
        LocalDate.of(2026, 2, 1),
        LocalDate.of(2026, 3, 1),
        LocalDate.of(2026, 4, 1),
        LocalDate.of(2026, 5, 1),
    )

    val monthCells = months.mapIndexed { monthIndex, ym ->
        (1..ym.lengthOfMonth()).map { day ->
            val ratio = when {
                day % 11 == 0 -> 1.3f
                day % 7 == 0 -> 0.9f
                day % 5 == 0 -> 0.65f
                day % 3 == 0 -> 0.35f
                else -> 0.1f
            }
            MonthCellState(
                dayNumber = day,
                ratio = ratio,
                hasSpending = (day + monthIndex) % 4 != 0,
            )
        }
    }

    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface),
        ) {
            HeatmapContent(
                months = months,
                monthCells = monthCells,
            )
        }
    }
}
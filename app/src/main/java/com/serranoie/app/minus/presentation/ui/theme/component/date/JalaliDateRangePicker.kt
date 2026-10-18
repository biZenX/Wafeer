package com.serranoie.app.minus.presentation.ui.theme.component.date

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.serranoie.app.minus.domain.time.CalendarSystem
import com.serranoie.app.minus.presentation.util.font.format.formatLocalized
import com.serranoie.app.minus.presentation.util.font.format.toDate
import java.time.LocalDate
import java.util.Locale

private const val PAST_MONTHS = 12L
private const val FUTURE_MONTHS = 24L

@Composable
fun JalaliDateRangePicker(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onSelectionChange: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val months = remember(today, locale) {
        val calendar = CalendarSystem.of(locale)
        val currentMonthStart = calendar.firstDayOfMonth(today)
        CalendarState(
            context = context,
            disableBeforeDate = calendar.plusMonths(currentMonthStart, -PAST_MONTHS).toDate(),
            disableAfterDate = calendar.lastDayOfMonth(calendar.plusMonths(currentMonthStart, FUTURE_MONTHS)).toDate(),
            locale = locale,
        ).listMonths
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = PAST_MONTHS.toInt())

    Column(modifier) {
        DaysOfWeek(Modifier.padding(horizontal = 12.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        ) {
            items(months, key = { it.monthStart.toEpochDay() }) { month ->
                Column {
                    MonthHeader(monthStart = month.monthStart)
                    month.weeks.forEach { week ->
                        Row(Modifier.fillMaxWidth()) {
                            for (offset in 0..6) {
                                val day = week.startDate.plusDays(offset.toLong())
                                Box(Modifier.weight(1f).aspectRatio(1f)) {
                                    if (week.isInMonth(day)) {
                                        RangeDayCell(
                                            day = day,
                                            isEdge = day == startDate || day == endDate,
                                            inRange = startDate != null && endDate != null &&
                                                !day.isBefore(startDate) && !day.isAfter(endDate),
                                            isToday = day == today,
                                            locale = locale,
                                            onClick = {
                                                val (start, end) = nextRangeSelection(startDate, endDate, day)
                                                onSelectionChange(start, end)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RangeDayCell(
    day: LocalDate,
    isEdge: Boolean,
    inRange: Boolean,
    isToday: Boolean,
    locale: Locale,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val label = day.formatLocalized("EEEE d MMMM yyyy", locale)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .then(if (inRange) Modifier.background(colors.secondaryContainer) else Modifier)
            .padding(2.dp)
            .clip(CircleShape)
            .then(if (isEdge) Modifier.background(colors.primary) else Modifier)
            .then(if (isToday && !isEdge) Modifier.border(1.dp, colors.primary, CircleShape) else Modifier)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = label
                selected = isEdge || inRange
            },
    ) {
        Text(
            text = day.formatLocalized("d", locale),
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                isEdge -> colors.onPrimary
                inRange -> colors.onSecondaryContainer
                else -> colors.onSurface
            },
        )
    }
}

internal fun nextRangeSelection(
    start: LocalDate?,
    end: LocalDate?,
    tapped: LocalDate,
): Pair<LocalDate?, LocalDate?> = when {
    start == null || end != null || tapped.isBefore(start) -> tapped to null
    else -> start to tapped
}

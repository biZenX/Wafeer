package com.serranoie.app.minus.domain.model

import com.serranoie.app.minus.domain.time.CalendarSystem
import java.time.DayOfWeek
import java.time.LocalDate

data class CreditCard(
    val cutoffDay: Int,
    val gracePeriodDays: Long = 20
)

fun calculatePaymentDueDate(
    card: CreditCard,
    targetDate: LocalDate = LocalDate.now(),
    calendar: CalendarSystem = CalendarSystem.current(),
): LocalDate {
    val rawDueDate = lastCutoffDate(card.cutoffDay, targetDate, calendar).plusDays(card.gracePeriodDays)

    return when (rawDueDate.dayOfWeek) {
        DayOfWeek.SATURDAY -> rawDueDate.plusDays(2)
        DayOfWeek.SUNDAY -> rawDueDate.plusDays(1)
        else -> rawDueDate
    }
}

fun lastCutoffDate(
    cutoffDay: Int,
    targetDate: LocalDate,
    calendar: CalendarSystem = CalendarSystem.current(),
): LocalDate {
    val currentMonthCutoff = calendar.withDayOfMonth(targetDate, cutoffDay)
    return if (targetDate.isAfter(currentMonthCutoff)) {
        currentMonthCutoff
    } else {
        calendar.withDayOfMonth(calendar.plusMonths(targetDate, -1), cutoffDay)
    }
}

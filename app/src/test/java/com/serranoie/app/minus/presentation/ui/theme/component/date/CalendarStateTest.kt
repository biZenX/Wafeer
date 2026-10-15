package com.serranoie.app.minus.presentation.ui.theme.component.date

import com.google.common.truth.Truth.assertThat
import com.serranoie.app.minus.presentation.util.font.format.getWeek
import com.serranoie.app.minus.presentation.util.font.format.toDate
import io.mockk.mockk
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

class CalendarStateTest {

    private fun months(start: LocalDate, end: LocalDate, locale: Locale) =
        CalendarState(mockk(relaxed = true), start.toDate(), end.toDate(), locale).listMonths

    @Test
    fun `gregorian locale groups the period into gregorian months`() {
        val months = months(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 5), Locale.US)

        assertThat(months.map { it.monthStart })
            .containsExactly(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1)).inOrder()
        assertThat(months.first().weeks.first().isInMonth(LocalDate.of(2026, 9, 30))).isFalse()
        assertThat(months.first().weeks.first().isInMonth(LocalDate.of(2026, 10, 1))).isTrue()
    }

    @Test
    fun `persian locale groups the period into persian months starting on saturday`() {
        val months = months(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), Locale.forLanguageTag("fa-IR"))

        assertThat(months.map { it.monthStart })
            .containsExactly(LocalDate.of(2026, 9, 23), LocalDate.of(2026, 10, 23)).inOrder()
        val mehr = months.first()
        assertThat(mehr.weeks.first().startDate.dayOfWeek).isEqualTo(DayOfWeek.SATURDAY)
        assertThat(mehr.weeks.last().isInMonth(LocalDate.of(2026, 10, 22))).isTrue()
        assertThat(mehr.weeks.last().isInMonth(LocalDate.of(2026, 10, 23))).isFalse()
    }

    @Test
    fun `week header starts on the locale first day of week`() {
        assertThat(getWeek(Locale.forLanguageTag("fa-IR")).first()).isEqualTo(DayOfWeek.SATURDAY)
        assertThat(getWeek(Locale.US).first()).isEqualTo(DayOfWeek.SUNDAY)
        assertThat(getWeek(Locale.GERMANY).first()).isEqualTo(DayOfWeek.MONDAY)
        assertThat(getWeek(Locale.forLanguageTag("fa-IR")).toSet()).hasSize(7)
    }
}

package com.serranoie.app.minus.domain.time

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class CalendarSystemTest {

    private val persian = CalendarSystem.PERSIAN
    private val gregorian = CalendarSystem.GREGORIAN

    @Test
    fun `locale picks persian only for fa or the persian calendar extension`() {
        assertThat(CalendarSystem.of(Locale.forLanguageTag("fa-IR"))).isEqualTo(persian)
        assertThat(CalendarSystem.of(Locale.forLanguageTag("fa"))).isEqualTo(persian)
        assertThat(CalendarSystem.of(Locale.forLanguageTag("en-US-u-ca-persian"))).isEqualTo(persian)
        assertThat(CalendarSystem.of(Locale.forLanguageTag("fa-IR-u-ca-gregory"))).isEqualTo(gregorian)
        assertThat(CalendarSystem.of(Locale.US)).isEqualTo(gregorian)
        assertThat(CalendarSystem.of(Locale.forLanguageTag("ar-SA"))).isEqualTo(gregorian)
    }

    @Test
    fun `persian day of month matches known dates`() {
        assertThat(persian.dayOfMonth(LocalDate.of(2026, 10, 13))).isEqualTo(21)
        assertThat(persian.dayOfMonth(LocalDate.of(2026, 3, 21))).isEqualTo(1)
        assertThat(persian.dayOfMonth(LocalDate.of(2026, 3, 20))).isEqualTo(29)
        assertThat(persian.dayOfMonth(LocalDate.of(2025, 3, 20))).isEqualTo(30)
        assertThat(persian.dayOfMonth(LocalDate.of(2026, 9, 23))).isEqualTo(1)
    }

    @Test
    fun `persian month lengths follow 31 30 and leap esfand`() {
        assertThat(persian.lengthOfMonth(LocalDate.of(2026, 4, 1))).isEqualTo(31)
        assertThat(persian.lengthOfMonth(LocalDate.of(2026, 10, 13))).isEqualTo(30)
        assertThat(persian.lengthOfMonth(LocalDate.of(2026, 3, 1))).isEqualTo(29)
        assertThat(persian.lengthOfMonth(LocalDate.of(2025, 3, 1))).isEqualTo(30)
    }

    @Test
    fun `persian with day of month stays in the same persian month and clamps`() {
        val midMehr = LocalDate.of(2026, 10, 13)
        assertThat(persian.withDayOfMonth(midMehr, 1)).isEqualTo(LocalDate.of(2026, 9, 23))
        assertThat(persian.withDayOfMonth(midMehr, 30)).isEqualTo(LocalDate.of(2026, 10, 22))
        assertThat(persian.withDayOfMonth(midMehr, 31)).isEqualTo(LocalDate.of(2026, 10, 22))
    }

    @Test
    fun `persian plus months keeps the persian day and crosses nowruz`() {
        assertThat(persian.plusMonths(LocalDate.of(2026, 10, 13), 1)).isEqualTo(LocalDate.of(2026, 11, 12))
        assertThat(persian.plusMonths(LocalDate.of(2026, 9, 22), 1)).isEqualTo(LocalDate.of(2026, 10, 22))
        assertThat(persian.plusMonths(LocalDate.of(2026, 3, 5), 1)).isEqualTo(LocalDate.of(2026, 4, 3))
        assertThat(persian.plusMonths(LocalDate.of(2026, 3, 21), -1)).isEqualTo(LocalDate.of(2026, 2, 20))
        assertThat(persian.plusMonths(LocalDate.of(2026, 10, 13), 12)).isEqualTo(LocalDate.of(2027, 10, 13))
    }

    @Test
    fun `persian round trips every day across a century`() {
        var date = LocalDate.of(1990, 1, 1)
        val end = LocalDate.of(2090, 12, 31)
        while (!date.isAfter(end)) {
            val day = persian.dayOfMonth(date)
            assertThat(persian.withDayOfMonth(date, day)).isEqualTo(date)
            assertThat(day).isIn(1..persian.lengthOfMonth(date))
            date = date.plusDays(1)
        }
    }

    @Test
    fun `gregorian matches java time`() {
        val date = LocalDate.of(2024, 1, 31)
        assertThat(gregorian.dayOfMonth(date)).isEqualTo(31)
        assertThat(gregorian.lengthOfMonth(LocalDate.of(2024, 2, 10))).isEqualTo(29)
        assertThat(gregorian.withDayOfMonth(LocalDate.of(2024, 2, 10), 31)).isEqualTo(LocalDate.of(2024, 2, 29))
        assertThat(gregorian.plusMonths(date, 1)).isEqualTo(LocalDate.of(2024, 2, 29))
    }
}

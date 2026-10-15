package com.serranoie.app.minus.presentation.ui.budget

import com.google.common.truth.Truth.assertThat
import com.serranoie.app.minus.domain.model.Transaction
import com.serranoie.app.minus.domain.time.CalendarSystem
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class WidgetHeatmapMonthsTest {

    private fun spend(date: LocalDate) = Transaction(
        id = date.toEpochDay(),
        amount = BigDecimal("10.00"),
        comment = "",
        date = date.atTime(12, 0),
    )

    @Test
    fun `gregorian heatmap covers the current and three previous gregorian months`() {
        val months = buildHeatmapMonths(
            transactions = listOf(spend(LocalDate.of(2026, 9, 30))),
            now = LocalDate.of(2026, 10, 13),
            budget = BigDecimal.ONE,
            calendar = CalendarSystem.GREGORIAN,
        )

        assertThat(months.map { it.monthStart }).containsExactly(
            LocalDate.of(2026, 7, 1),
            LocalDate.of(2026, 8, 1),
            LocalDate.of(2026, 9, 1),
            LocalDate.of(2026, 10, 1),
        ).inOrder()
        assertThat(months.last().days).hasSize(31)
        assertThat(months[2].days.single { it.transactionCount > 0 }.dayOfMonth).isEqualTo(30)
    }

    @Test
    fun `persian heatmap follows persian month boundaries and lengths`() {
        val months = buildHeatmapMonths(
            transactions = listOf(spend(LocalDate.of(2026, 10, 22)), spend(LocalDate.of(2026, 10, 23))),
            now = LocalDate.of(2026, 10, 23),
            budget = BigDecimal.ONE,
            calendar = CalendarSystem.PERSIAN,
        )

        assertThat(months.map { it.monthStart }).containsExactly(
            LocalDate.of(2026, 7, 23),
            LocalDate.of(2026, 8, 23),
            LocalDate.of(2026, 9, 23),
            LocalDate.of(2026, 10, 23),
        ).inOrder()
        assertThat(months[2].days).hasSize(30)
        assertThat(months[2].days.single { it.transactionCount > 0 }.dayOfMonth).isEqualTo(30)
        assertThat(months.last().days.single { it.transactionCount > 0 }.dayOfMonth).isEqualTo(1)
    }
}

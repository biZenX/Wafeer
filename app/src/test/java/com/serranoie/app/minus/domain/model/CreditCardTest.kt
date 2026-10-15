package com.serranoie.app.minus.domain.model

import org.junit.Assert.assertEquals
import com.serranoie.app.minus.domain.time.CalendarSystem
import org.junit.Test
import java.time.LocalDate

class CreditCardTest {

    @Test
    fun `previous gregorian cutoff reapplies the requested day and clamps when needed`() {
        val cases = listOf(
            LocalDate.of(2026, 2, 10) to LocalDate.of(2026, 1, 31),
            LocalDate.of(2024, 2, 29) to LocalDate.of(2024, 1, 31),
            LocalDate.of(2026, 4, 15) to LocalDate.of(2026, 3, 31),
            LocalDate.of(2026, 3, 15) to LocalDate.of(2026, 2, 28),
            LocalDate.of(2024, 3, 15) to LocalDate.of(2024, 2, 29),
            LocalDate.of(2026, 1, 10) to LocalDate.of(2025, 12, 31),
        )

        for ((targetDate, expected) in cases) {
            assertEquals(expected, lastCutoffDate(31, targetDate, CalendarSystem.GREGORIAN))
        }
    }

    @Test
    fun `previous persian cutoff reapplies the requested day and clamps when needed`() {
        val cases = listOf(
            LocalDate.of(2026, 10, 1) to LocalDate.of(2026, 9, 22),
            LocalDate.of(2026, 3, 20) to LocalDate.of(2026, 2, 19),
            LocalDate.of(2026, 4, 1) to LocalDate.of(2026, 3, 20),
            LocalDate.of(2025, 4, 1) to LocalDate.of(2025, 3, 20),
        )

        for ((targetDate, expected) in cases) {
            assertEquals(expected, lastCutoffDate(31, targetDate, CalendarSystem.PERSIAN))
        }
    }

    @Test
    fun `test calculatePaymentDueDate - normal case`() {
        val card = CreditCard(cutoffDay = 15, gracePeriodDays = 20)
        val today = LocalDate.of(2024, 7, 10)
        val dueDate = calculatePaymentDueDate(card, today)
        
        assertEquals(LocalDate.of(2024, 7, 5), dueDate)
    }

    @Test
    fun `test calculatePaymentDueDate - saturday adjustment`() {
        val card = CreditCard(cutoffDay = 5, gracePeriodDays = 20)
        val today = LocalDate.of(2024, 5, 26)
        val dueDate = calculatePaymentDueDate(card, today)
        
        assertEquals(LocalDate.of(2024, 5, 27), dueDate)
    }

    @Test
    fun `test calculatePaymentDueDate - sunday adjustment`() {
        val card = CreditCard(cutoffDay = 6, gracePeriodDays = 20)
        val today = LocalDate.of(2024, 5, 28)
        val dueDate = calculatePaymentDueDate(card, today)
        
        assertEquals(LocalDate.of(2024, 5, 27), dueDate)
    }

    @Test
    fun `test calculatePaymentDueDate - transition to next month`() {
        val card = CreditCard(cutoffDay = 15, gracePeriodDays = 20)
        val today = LocalDate.of(2024, 6, 10)
        val dueDate = calculatePaymentDueDate(card, today)
        
        assertEquals(LocalDate.of(2024, 6, 4), dueDate)
    }

    @Test
    fun `test calculatePaymentDueDate - persian cutoff already passed this persian month`() {
        val card = CreditCard(cutoffDay = 15, gracePeriodDays = 20)

        assertEquals(
            LocalDate.of(2026, 10, 27),
            calculatePaymentDueDate(card, LocalDate.of(2026, 10, 13), CalendarSystem.PERSIAN),
        )
    }

    @Test
    fun `test calculatePaymentDueDate - persian cutoff ahead uses previous persian month`() {
        val card = CreditCard(cutoffDay = 15, gracePeriodDays = 20)

        assertEquals(
            LocalDate.of(2026, 9, 28),
            calculatePaymentDueDate(card, LocalDate.of(2026, 10, 1), CalendarSystem.PERSIAN),
        )
    }
}

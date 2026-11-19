package com.wafeer.app.domain.calculator

import com.google.common.truth.Truth.assertThat
import com.wafeer.app.domain.model.SavingsPocket
import com.wafeer.app.domain.model.Transaction
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class SavingsPocketsTest {

    @Test
    fun `pocketBalances sums transactions per pocketId`() {
        val tx1 = Transaction(amount = BigDecimal("50.00"), date = LocalDateTime.now(), pocketId = 1L)
        val tx2 = Transaction(amount = BigDecimal("25.00"), date = LocalDateTime.now(), pocketId = 1L)
        val tx3 = Transaction(amount = BigDecimal("100.00"), date = LocalDateTime.now(), pocketId = 2L)
        val txDeleted = Transaction(amount = BigDecimal("999.00"), date = LocalDateTime.now(), pocketId = 1L, isDeleted = true)

        val balances = pocketBalances(listOf(tx1, tx2, tx3, txDeleted))

        assertThat(balances[1L]).isEqualTo(BigDecimal("75.00"))
        assertThat(balances[2L]).isEqualTo(BigDecimal("100.00"))
    }

    @Test
    fun `pocketStatuses maps pockets with accurate balance`() {
        val pocket1 = SavingsPocket(id = 1L, name = "Emergency")
        val pocket2 = SavingsPocket(id = 2L, name = "Vacation")
        val tx = Transaction(amount = BigDecimal("300.00"), date = LocalDateTime.now(), pocketId = 1L)

        val statuses = pocketStatuses(listOf(pocket1, pocket2), listOf(tx))

        assertThat(statuses).hasSize(2)
        assertThat(statuses[0].pocket.id).isEqualTo(1L)
        assertThat(statuses[0].balance).isEqualTo(BigDecimal("300.00"))
        assertThat(statuses[1].pocket.id).isEqualTo(2L)
        assertThat(statuses[1].balance).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `periodPocketFlow tracks deposits and withdrawals correctly`() {
        val deposit = Transaction(amount = BigDecimal("200.00"), date = LocalDateTime.now(), pocketId = 1L)
        val withdrawal = Transaction(amount = BigDecimal("-50.00"), date = LocalDateTime.now(), pocketId = 1L)

        val flow = periodPocketFlow(listOf(deposit, withdrawal))

        assertThat(flow).isNotNull()
        assertThat(flow!!.added).isEqualTo(BigDecimal("200.00"))
        assertThat(flow.withdrawn).isEqualTo(BigDecimal("50.00"))
    }
}

package com.wafeer.app.domain.calculator

import com.wafeer.app.domain.model.SavingsPocket
import com.wafeer.app.domain.model.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime

data class SavingsPocketStatus(val pocket: SavingsPocket, val balance: BigDecimal)

fun pocketBalances(transactions: List<Transaction>): Map<Long, BigDecimal> =
    transactions
        .filter { it.pocketId != null && !it.isDeleted }
        .groupBy { it.pocketId!! }
        .mapValues { (_, rows) -> rows.sumOf { it.amount } }

fun pocketStatuses(pockets: List<SavingsPocket>, transactions: List<Transaction>): List<SavingsPocketStatus> {
    val balances = pocketBalances(transactions)
    return pockets.map { SavingsPocketStatus(it, balances[it.id] ?: BigDecimal.ZERO) }
}

fun pocketEntry(
    pocket: SavingsPocket,
    amount: BigDecimal,
    withdraw: Boolean,
    periodId: Long,
    date: LocalDateTime,
    label: String = pocket.name,
    clientGeneratedId: String? = null,
): Transaction = Transaction(
    amount = if (withdraw) amount.abs().negate() else amount.abs(),
    comment = label,
    date = date,
    periodId = periodId,
    isAdjustment = true,
    pocketId = pocket.id,
    clientGeneratedId = clientGeneratedId,
)

data class PocketPeriodFlow(val added: BigDecimal, val withdrawn: BigDecimal)

fun periodPocketFlow(transactions: List<Transaction>): PocketPeriodFlow? {
    val rows = transactions.filter { it.pocketId != null && !it.isDeleted }
    if (rows.isEmpty()) return null
    return PocketPeriodFlow(
        added = rows.filter { it.amount.signum() > 0 }.sumOf { it.amount },
        withdrawn = rows.filter { it.amount.signum() < 0 }.sumOf { it.amount.abs() },
    )
}

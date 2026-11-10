package com.wafeer.app.domain.model.smart

import java.math.BigDecimal
import java.util.UUID

data class GameyaItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val monthlyShare: BigDecimal,
    val totalMonths: Int,
    val payoutMonth: Int, // The month number (1..totalMonths) when the user collects
    val currentMonth: Int = 1, // How many months have been paid
    val totalPayout: BigDecimal = monthlyShare.multiply(BigDecimal(totalMonths)),
    val isCollected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
) {
    val remainingMonths: Int get() = (totalMonths - currentMonth).coerceAtLeast(0)
    val isDueForPayout: Boolean get() = currentMonth >= payoutMonth && !isCollected
    val isCompleted: Boolean get() = currentMonth >= totalMonths && isCollected
    val progressPercent: Float get() = if (totalMonths > 0) (currentMonth.toFloat() / totalMonths.toFloat()).coerceIn(0f, 1f) else 1f
}

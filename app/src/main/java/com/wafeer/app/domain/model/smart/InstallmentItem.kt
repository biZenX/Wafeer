package com.wafeer.app.domain.model.smart

import java.math.BigDecimal
import java.util.UUID

data class InstallmentItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val totalAmount: BigDecimal,
    val monthlyAmount: BigDecimal,
    val totalMonths: Int,
    val paidMonths: Int = 0,
    val dueDayOfMonth: Int = 1,
    val category: String = "أقساط",
    val provider: String = "", // e.g. ValU, Tabby, Tamara, Aman, Bank
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = paidMonths >= totalMonths,
) {
    val remainingMonths: Int get() = (totalMonths - paidMonths).coerceAtLeast(0)
    val remainingAmount: BigDecimal get() = monthlyAmount.multiply(BigDecimal(remainingMonths)).coerceAtLeast(BigDecimal.ZERO)
    val progressPercent: Float get() = if (totalMonths > 0) (paidMonths.toFloat() / totalMonths.toFloat()).coerceIn(0f, 1f) else 1f
}

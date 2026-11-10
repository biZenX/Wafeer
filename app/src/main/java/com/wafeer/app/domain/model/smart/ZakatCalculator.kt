package com.wafeer.app.domain.model.smart

import java.math.BigDecimal
import java.math.RoundingMode

data class ZakatState(
    val customNisabAmount: BigDecimal? = null,
    val goldPriceForNisab: BigDecimal = BigDecimal.ZERO, // Gram 21k or 24k price
    val isHawlCompleted: Boolean = true, // Whether 1 full lunar year has elapsed
)

data class ZakatResult(
    val totalWealth: BigDecimal,
    val nisabThreshold: BigDecimal,
    val isEligible: Boolean,
    val zakatDue: BigDecimal, // 2.5% of total wealth if eligible
)

object ZakatCalculator {
    // Sharia Gold Nisab is 85 grams of pure gold (approx. 97g of 21k or 85g of 24k)
    val GOLD_NISAB_GRAMS_21K = BigDecimal("97.14")
    val GOLD_NISAB_GRAMS_24K = BigDecimal("85.00")
    val ZAKAT_RATE = BigDecimal("0.025") // 2.5%

    fun calculate(
        liquidCash: BigDecimal,
        goldHoldingsValue: BigDecimal,
        goldPrice21k: BigDecimal,
        customNisab: BigDecimal? = null,
        isHawlCompleted: Boolean = true,
    ): ZakatResult {
        val totalWealth = (liquidCash + goldHoldingsValue).coerceAtLeast(BigDecimal.ZERO)

        val nisab = when {
            customNisab != null && customNisab > BigDecimal.ZERO -> customNisab
            goldPrice21k > BigDecimal.ZERO -> goldPrice21k.multiply(GOLD_NISAB_GRAMS_21K)
            else -> BigDecimal("150000") // Fallback indicative Nisab in EGP/local currency
        }

        val isEligible = isHawlCompleted && totalWealth >= nisab
        val zakatDue = if (isEligible) {
            totalWealth.multiply(ZAKAT_RATE).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return ZakatResult(
            totalWealth = totalWealth,
            nisabThreshold = nisab,
            isEligible = isEligible,
            zakatDue = zakatDue,
        )
    }
}

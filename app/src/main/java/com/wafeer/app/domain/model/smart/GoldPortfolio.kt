package com.wafeer.app.domain.model.smart

import java.math.BigDecimal
import java.math.RoundingMode

data class GoldPortfolio(
    val grams21k: BigDecimal = BigDecimal.ZERO,
    val grams24k: BigDecimal = BigDecimal.ZERO,
    val goldCoins: Int = 0, // Traditional 8-gram 21k sovereign coins (جنيه ذهب)
    val averagePurchasePricePerGram: BigDecimal = BigDecimal.ZERO,
    val currentPricePerGram21k: BigDecimal = BigDecimal.ZERO,
    val lastUpdated: Long = System.currentTimeMillis(),
) {
    // Total equivalent 21k grams (each gold coin = 8 grams 21k)
    val totalEquivalent21kGrams: BigDecimal
        get() = grams21k + grams24k.multiply(BigDecimal("24")).divide(BigDecimal("21"), 2, RoundingMode.HALF_UP) +
            BigDecimal(goldCoins * 8)

    val currentTotalMarketValue: BigDecimal
        get() = if (currentPricePerGram21k > BigDecimal.ZERO) {
            totalEquivalent21kGrams.multiply(currentPricePerGram21k)
        } else {
            BigDecimal.ZERO
        }

    val totalPurchaseCost: BigDecimal
        get() = if (averagePurchasePricePerGram > BigDecimal.ZERO) {
            totalEquivalent21kGrams.multiply(averagePurchasePricePerGram)
        } else {
            BigDecimal.ZERO
        }

    val profitOrLoss: BigDecimal
        get() = if (totalPurchaseCost > BigDecimal.ZERO && currentTotalMarketValue > BigDecimal.ZERO) {
            currentTotalMarketValue - totalPurchaseCost
        } else {
            BigDecimal.ZERO
        }

    val profitPercentage: Float
        get() = if (totalPurchaseCost > BigDecimal.ZERO) {
            val diff = profitOrLoss
            (diff.toDouble() / totalPurchaseCost.toDouble() * 100.0).toFloat()
        } else {
            0f
        }

    val hasHoldings: Boolean
        get() = totalEquivalent21kGrams > BigDecimal.ZERO
}

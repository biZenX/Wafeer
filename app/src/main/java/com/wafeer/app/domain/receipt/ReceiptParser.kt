package com.wafeer.app.domain.receipt

import com.wafeer.app.domain.notification.ExpenseNotificationParser
import java.math.BigDecimal

data class ReceiptItem(
    val rawName: String,
    val name: String,
    val price: BigDecimal,
    val quantity: Int = 1,
    val suggestedCategory: String? = null,
)

enum class ScanMode {
    RECEIPT,
    QR_CODE,
}

data class ReceiptScan(
    val merchant: String?,
    val total: BigDecimal,
    val items: List<ReceiptItem> = emptyList(),
    val subtotal: BigDecimal? = null,
    val tax: BigDecimal? = null,
    val scanMode: ScanMode = ScanMode.RECEIPT,
    val qrPayload: String? = null,
)

object ReceiptParser {

    private const val MERCHANT_LOOKAHEAD = 6
    private const val MERCHANT_MAX_LENGTH = 40
    private const val MERCHANT_MIN_LETTER_WEIGHT = 3

    private val AMOUNT = Regex("""(?<![\d.,])(?:\d{1,3}(?:[.,]\d{3})+|\d+)[.,]\d{1,2}(?![.,]?\d)""")
    private val TOTAL_AMOUNT = Regex("""(?<![\d.,])(?:\d{1,3}(?:[.,]\d{3})+|\d+)(?:[.,]\d{1,2})?(?![.,]?\d)""")
    private val SPACED_DECIMAL = Regex("""(\d)\s*([.,])\s*(\d{1,2})(?!\d)""")
    private val ONE_OR_ZERO_NEAR_DIGIT = Regex("""(?<=[\d.,])[OoIl|]|[OoIl|](?=\d)""")
    private val FIVE_OR_EIGHT_AFTER_DIGIT = Regex("""(?<=\d|\d[.,])[SB]""")

    fun parse(text: String): ReceiptScan? {
        val lines = ReceiptVocabulary.asciiDigits(text).lines().map { it.trim() }.filter { it.isNotEmpty() }
        val totalIndex = lines.indices.firstOrNull { isTotalLine(lines[it]) }
        val total = totalFrom(lines)
        val items = itemsFrom(lines, totalIndex)
        val calculatedSum = if (items.isNotEmpty()) {
            items.fold(BigDecimal.ZERO) { acc, item ->
                acc + item.price.multiply(BigDecimal(item.quantity))
            }
        } else {
            null
        }
        val finalTotal = when {
            totalIndex != null && total != null -> total
            calculatedSum != null && items.isNotEmpty() -> {
                if (total != null && total > calculatedSum) total else calculatedSum
            }
            else -> total ?: calculatedSum ?: return null
        }

        return ReceiptScan(
            merchant = merchantFrom(lines),
            total = finalTotal,
            items = items,
            subtotal = if (total != null && calculatedSum != null && calculatedSum < total) calculatedSum else null,
            scanMode = ScanMode.RECEIPT,
        )
    }

    private fun itemsFrom(lines: List<String>, totalLineIndex: Int?): List<ReceiptItem> {
        val endLimit = totalLineIndex ?: lines.size
        val items = mutableListOf<ReceiptItem>()
        var i = 0

        while (i < endLimit) {
            val line = lines[i]
            if (isTotalLine(line) || isSubtotalOrPayment(line) || ReceiptVocabulary.isNoise(line) || ReceiptVocabulary.isTaxOrCount(line)) {
                i++
                continue
            }
            if (i < 3 && amountsIn(line).isEmpty()) {
                i++
                continue
            }

            val amounts = amountsIn(line)
            if (amounts.isNotEmpty()) {
                val price = amounts.last()
                val rawName = repairOcrDigits(line)
                    .replace(AMOUNT, "")
                    .replace(TOTAL_AMOUNT, "")
                    .trim()
                if (rawName.length >= 2 && rawName.any { it.isLetter() }) {
                    val norm = ReceiptItemNormalizer.normalize(rawName)
                    items.add(
                        ReceiptItem(
                            rawName = rawName,
                            name = norm.canonicalName,
                            price = price,
                            quantity = 1,
                            suggestedCategory = norm.suggestedCategory,
                        ),
                    )
                }
                i++
            } else if (i + 1 < endLimit) {
                // Name on current line, price on next line
                val nextAmounts = amountsIn(lines[i + 1])
                if (nextAmounts.isNotEmpty() && line.length >= 2 && line.any { it.isLetter() } && !isTotalLine(lines[i + 1])) {
                    val price = nextAmounts.last()
                    val norm = ReceiptItemNormalizer.normalize(line)
                    items.add(
                        ReceiptItem(
                            rawName = line,
                            name = norm.canonicalName,
                            price = price,
                            quantity = 1,
                            suggestedCategory = norm.suggestedCategory,
                        ),
                    )
                    i += 2
                } else {
                    i++
                }
            } else {
                i++
            }
        }
        return items
    }

    private fun amountsIn(line: String, pattern: Regex = AMOUNT): List<BigDecimal> =
        pattern.findAll(repairOcrDigits(line)).mapNotNull { ExpenseNotificationParser.normalize(it.value) }.toList()

    private fun totalAmountsIn(line: String) = amountsIn(line, TOTAL_AMOUNT)

    private fun repairOcrDigits(line: String): String {
        val digits = ONE_OR_ZERO_NEAR_DIGIT.replace(line) { if (it.value in "Oo") "0" else "1" }
        val fivesAndEights = FIVE_OR_EIGHT_AFTER_DIGIT.replace(digits) { if (it.value == "S") "5" else "8" }
        return SPACED_DECIMAL.replace(fivesAndEights, "$1$2$3")
    }

    private fun isTotalLine(line: String) = ReceiptVocabulary.isTotal(line) && !isSubtotalOrPayment(line)

    private fun isSubtotalOrPayment(line: String) =
        ReceiptVocabulary.isSubtotal(line) || ReceiptVocabulary.isPayment(line)

    private fun isFallbackLine(line: String) = !isSubtotalOrPayment(line) && !ReceiptVocabulary.isTaxOrCount(line)

    private fun totalFrom(lines: List<String>): BigDecimal? {
        val keyed = lines.indices
            .filter { isTotalLine(lines[it]) }
            .flatMap { i ->
                totalAmountsIn(lines[i]).ifEmpty {
                    lines.getOrNull(i + 1)?.takeIf(::isFallbackLine)?.let(::totalAmountsIn).orEmpty()
                }
            }
        val candidates = keyed.ifEmpty { fallbackAmounts(lines) }
        return candidates.filter { it.signum() > 0 }.maxOrNull()
    }

    private fun fallbackAmounts(lines: List<String>): List<BigDecimal> {
        val eligible = lines.filter(::isFallbackLine)
        val withCurrency = eligible.filter(ReceiptVocabulary::hasCurrency).flatMap(::amountsIn)
        return withCurrency.ifEmpty { eligible.flatMap(::amountsIn) }
    }

    private fun merchantFrom(lines: List<String>): String? =
        lines.take(MERCHANT_LOOKAHEAD).firstOrNull { line ->
            val letterLike = line.count { it.isLetterOrMark() }
            val weight = line.sumOf { c -> if (Character.isIdeographic(c.code)) 2 else if (c.isLetterOrMark()) 1 else 0 }
            weight >= MERCHANT_MIN_LETTER_WEIGHT &&
                letterLike * 2 > line.length &&
                !ReceiptVocabulary.isNoise(line) &&
                amountsIn(line).isEmpty()
        }?.take(MERCHANT_MAX_LENGTH)
}

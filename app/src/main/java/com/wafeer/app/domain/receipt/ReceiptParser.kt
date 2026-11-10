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

    private const val MERCHANT_LOOKAHEAD = 8
    private const val MERCHANT_MAX_LENGTH = 40
    private const val MERCHANT_MIN_LETTER_WEIGHT = 3

    private val MAX_ITEM_PRICE = BigDecimal(50000)
    private val MAX_INTEGER_ITEM_PRICE = BigDecimal(10000)
    private val MAX_TOTAL_AMOUNT = BigDecimal(500000)

    private val DECIMAL_AMOUNT = Regex("""(?<![\d.,])(?:\d{1,3}(?:[.,]\d{3})+|\d+)[.,]\d{1,2}(?![.,]?\d)""")
    private val INTEGER_AMOUNT = Regex("""(?<![\d.,])\b[1-9]\d{0,4}\b(?![\d.,])""")
    private val SPACED_DECIMAL = Regex("""(\d)\s*([.,])\s*(\d{1,2})(?!\d)""")
    private val ONE_OR_ZERO_NEAR_DIGIT = Regex("""(?<=[\d.,])[OoIl|]|[OoIl|](?=\d)""")
    private val FIVE_OR_EIGHT_AFTER_DIGIT = Regex("""(?<=\d|\d[.,])[SB]""")
    private val SYMBOLS_REGEX = Regex("""[#*_~|\\/\[\]{}()<>=+:;!?@$%^&-]""")

    private val UNIT_AND_WEIGHT_REGEX = Regex(
        """(?i)\b\d+(?:[.,]\d+)?\s*(?:kg|g|gm|gram|ltr|l|ml|pcs|pc|كجم|جم|جرام|لتر|مل|قطع|قطعة)\b""",
    )
    private val CODE_OR_SKU_REGEX = Regex("""(?i)\b(?:art|sku|ref|code|item|no|كود|رقم|صنف)[:#\-]?\s*\w+\b""")

    fun parse(text: String): ReceiptScan? {
        val lines = ReceiptVocabulary.asciiDigits(text).lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return null

        val total = totalFrom(lines)
        val items = itemsFrom(lines)

        val calculatedSum = if (items.isNotEmpty()) {
            items.fold(BigDecimal.ZERO) { acc, item ->
                acc + item.price.multiply(BigDecimal(item.quantity))
            }
        } else {
            null
        }

        val finalTotal = when {
            total != null && calculatedSum != null -> {
                // If total is wildly greater than calculated sum (e.g. > 3x sum, or total > sum + 1000 while having multiple items),
                // total is likely an invoice/serial/tax/phone number misidentified on a total line
                if (calculatedSum > BigDecimal.ZERO && (total > calculatedSum.multiply(BigDecimal(3)) || (items.size >= 2 && total > calculatedSum + BigDecimal(1000)))) {
                    calculatedSum
                } else {
                    total
                }
            }
            total != null -> total
            calculatedSum != null -> calculatedSum
            else -> fallbackTotal(lines) ?: return null
        }

        return ReceiptScan(
            merchant = merchantFrom(lines),
            total = finalTotal,
            items = items,
            subtotal = if (total != null && calculatedSum != null && calculatedSum < total) calculatedSum else null,
            scanMode = ScanMode.RECEIPT,
        )
    }

    private fun itemsFrom(lines: List<String>): List<ReceiptItem> {
        val items = mutableListOf<ReceiptItem>()
        val merchant = merchantFrom(lines)
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            // Skip total, payment, tax, noise, or metadata lines (tax IDs, CR, dates, phone, cashier, etc.)
            if (isTotalLine(line) || isSubtotalOrPayment(line) || ReceiptVocabulary.isNoise(line) ||
                ReceiptVocabulary.isTaxOrCount(line) || ReceiptVocabulary.isMetadata(line)
            ) {
                i++
                continue
            }

            val isMerchantLine = i < 3 && line == merchant
            if (isMerchantLine) {
                i++
                continue
            }

            val priceCandidate = extractPriceFromLine(line)
            if (priceCandidate != null) {
                val rawName = cleanItemName(line)
                if (isValidItemName(rawName)) {
                    val norm = ReceiptItemNormalizer.normalize(rawName)
                    items.add(
                        ReceiptItem(
                            rawName = rawName,
                            name = norm.canonicalName,
                            price = priceCandidate,
                            quantity = 1,
                            suggestedCategory = norm.suggestedCategory,
                        ),
                    )
                }
                i++
            } else if (i + 1 < lines.size) {
                // Check if product name is on current line and price is on the next line
                val nextLine = lines[i + 1]
                val nextPrice = extractPriceFromLine(nextLine)
                val currentCleanName = cleanItemName(line)

                if (nextPrice != null && isValidItemName(currentCleanName) &&
                    !isTotalLine(nextLine) && !ReceiptVocabulary.isMetadata(nextLine) && !isSubtotalOrPayment(nextLine) &&
                    !ReceiptVocabulary.isNoise(line) && line != merchant
                ) {
                    val norm = ReceiptItemNormalizer.normalize(currentCleanName)
                    items.add(
                        ReceiptItem(
                            rawName = currentCleanName,
                            name = norm.canonicalName,
                            price = nextPrice,
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

    private fun isValidItemName(name: String): Boolean {
        if (name.length < 2 || !name.any { it.isLetter() }) return false
        if (ReceiptVocabulary.isMetadata(name)) return false
        if (ReceiptVocabulary.isNoise(name)) return false
        if (ReceiptVocabulary.isTaxOrCount(name)) return false
        if (ReceiptVocabulary.isSubtotal(name) || ReceiptVocabulary.isPayment(name)) return false
        return true
    }

    private fun extractPriceFromLine(line: String): BigDecimal? {
        val cleaned = stripNonPriceTokens(line)

        // 1. Prefer decimal amounts (e.g. 350.00, 45.50)
        val decimals = amountsIn(cleaned)
            .filter { it.signum() > 0 && it <= MAX_ITEM_PRICE }
        if (decimals.isNotEmpty()) {
            return decimals.last()
        }

        // 2. Try integer amounts (e.g. 350, 120, 45) if no decimal amount present
        val integers = integerAmountsIn(cleaned)
            .filter { it.signum() > 0 && it <= MAX_INTEGER_ITEM_PRICE }
        if (integers.isNotEmpty()) {
            return integers.last()
        }

        return null
    }

    private fun cleanItemName(line: String): String {
        var result = stripNonPriceTokens(line)
        result = DECIMAL_AMOUNT.replace(result, "")
        result = INTEGER_AMOUNT.replace(result, "")
        result = SYMBOLS_REGEX.replace(result, " ")
        return result.trim().replace(Regex("""\s+"""), " ")
    }

    private fun stripNonPriceTokens(line: String): String {
        var s = repairOcrDigits(line)
        s = ReceiptVocabulary.DATE_REGEX.replace(s, " ")
        s = ReceiptVocabulary.TIME_REGEX.replace(s, " ")
        s = ReceiptVocabulary.PHONE_REGEX.replace(s, " ")
        s = ReceiptVocabulary.TAX_ID_REGEX.replace(s, " ")
        s = ReceiptVocabulary.LONG_ID_REGEX.replace(s, " ")
        s = UNIT_AND_WEIGHT_REGEX.replace(s, " ")
        s = CODE_OR_SKU_REGEX.replace(s, " ")
        return s.trim()
    }

    private fun amountsIn(line: String): List<BigDecimal> =
        DECIMAL_AMOUNT.findAll(repairOcrDigits(line))
            .mapNotNull { ExpenseNotificationParser.normalize(it.value) }
            .toList()

    private fun integerAmountsIn(line: String): List<BigDecimal> =
        INTEGER_AMOUNT.findAll(repairOcrDigits(line))
            .mapNotNull { it.value.toBigDecimalOrNull() }
            .toList()

    private fun repairOcrDigits(line: String): String {
        val digits = ONE_OR_ZERO_NEAR_DIGIT.replace(line) { if (it.value in "Oo") "0" else "1" }
        val fivesAndEights = FIVE_OR_EIGHT_AFTER_DIGIT.replace(digits) { if (it.value == "S") "5" else "8" }
        return SPACED_DECIMAL.replace(fivesAndEights, "$1$2$3")
    }

    private fun isTotalLine(line: String) = ReceiptVocabulary.isTotal(line) && !isSubtotalOrPayment(line)

    private fun isSubtotalOrPayment(line: String) =
        ReceiptVocabulary.isSubtotal(line) || ReceiptVocabulary.isPayment(line)

    private fun totalFrom(lines: List<String>): BigDecimal? {
        val keyed = lines.indices
            .filter { isTotalLine(lines[it]) }
            .mapNotNull { i ->
                extractTotalFromLine(lines[i]) ?: run {
                    val next = lines.getOrNull(i + 1)
                    if (next != null && !isSubtotalOrPayment(next) && !ReceiptVocabulary.isMetadata(next)) {
                        extractTotalFromLine(next)
                    } else {
                        null
                    }
                }
            }
            .filter { it.signum() > 0 && it <= MAX_TOTAL_AMOUNT }

        // The true receipt total is the LAST explicit total line (e.g. net grand total after discounts and tax)
        return keyed.lastOrNull()
    }

    private fun extractTotalFromLine(line: String): BigDecimal? {
        val cleaned = stripNonPriceTokens(line)
        val decimals = amountsIn(cleaned).filter { it.signum() > 0 && it <= MAX_TOTAL_AMOUNT }
        if (decimals.isNotEmpty()) return decimals.last()

        val integers = integerAmountsIn(cleaned).filter { it.signum() > 0 && it <= MAX_TOTAL_AMOUNT }
        if (integers.isNotEmpty()) return integers.last()

        return null
    }

    private fun fallbackTotal(lines: List<String>): BigDecimal? {
        val eligible = lines.filter { line ->
            !isSubtotalOrPayment(line) &&
                !ReceiptVocabulary.isTaxOrCount(line) &&
                !ReceiptVocabulary.isMetadata(line) &&
                !ReceiptVocabulary.isNoise(line)
        }

        // 1. Prefer lines with explicit currency (e.g. "150.00 EGP", "ج.م 250")
        val withCurrency = eligible.filter(ReceiptVocabulary::hasCurrency)
            .mapNotNull(::extractTotalFromLine)
            .filter { it.signum() > 0 && it <= MAX_TOTAL_AMOUNT }
        if (withCurrency.isNotEmpty()) return withCurrency.lastOrNull()

        // 2. Prefer decimal amounts from the bottom half of eligible lines (where receipt totals are located)
        val decimalCandidates = eligible.takeLast(maxOf(3, eligible.size / 2))
            .mapNotNull { line ->
                val cleaned = stripNonPriceTokens(line)
                amountsIn(cleaned).filter { it.signum() > 0 && it <= MAX_TOTAL_AMOUNT }.lastOrNull()
            }
        if (decimalCandidates.isNotEmpty()) return decimalCandidates.lastOrNull()

        // 3. Last resort: integer amounts in a sensible total range (<= 50,000) from the bottom lines
        val integerCandidates = eligible.takeLast(maxOf(2, eligible.size / 3))
            .mapNotNull { line ->
                val cleaned = stripNonPriceTokens(line)
                integerAmountsIn(cleaned).filter { it.signum() > 0 && it <= BigDecimal(50000) }.lastOrNull()
            }
        return integerCandidates.lastOrNull()
    }

    private fun merchantFrom(lines: List<String>): String? =
        lines.take(MERCHANT_LOOKAHEAD).firstOrNull { line ->
            val letterLike = line.count { it.isLetterOrMark() }
            val weight = line.sumOf { c -> if (Character.isIdeographic(c.code)) 2 else if (c.isLetterOrMark()) 1 else 0 }
            weight >= MERCHANT_MIN_LETTER_WEIGHT &&
                letterLike * 2 > line.length &&
                !ReceiptVocabulary.isNoise(line) &&
                !ReceiptVocabulary.isMetadata(line) &&
                !ReceiptVocabulary.isTotal(line) &&
                !isSubtotalOrPayment(line) &&
                amountsIn(line).isEmpty() &&
                integerAmountsIn(line).isEmpty()
        }?.take(MERCHANT_MAX_LENGTH)
}

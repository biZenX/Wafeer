package com.wafeer.app.domain.receipt

import java.math.BigDecimal
import org.json.JSONObject

object ReceiptQrParser {

    private val EXPLICIT_TOTAL_REGEX = Regex(
        """(?i)(?:^|[\s,;:])(?:total|grand\s+total|amount\s+due|balance\s+due|to\s+pay|المجموع(?:\s+الكلي)?|الإجمالي|الاجمالي|صافي\s+المطلوب|المبلغ\s+(?:المطلوب|الكلي|المستحق)|الحساب)\s*[:=]?\s*([1-9]\d{0,5}(?:[.,]\d{1,2})?)(?!\d)""",
    )

    private val CURRENCY_AMOUNT_REGEX = Regex(
        """(?i)(?:EGP|ج\.م|LE|L\.E|SAR|ر\.س|USD|\$|EUR|€)\s*([1-9]\d{0,5}(?:[.,]\d{1,2})?)|([1-9]\d{0,5}(?:[.,]\d{1,2})?)\s*(?:EGP|ج\.م|LE|L\.E|SAR|ر\.س|USD|\$|EUR|€)""",
    )

    private val STANDALONE_PRICE_REGEX = Regex("""^([1-9]\d{0,5}(?:[.,]\d{1,2})?)$""")

    /**
     * Parses a scanned QR payload, supporting:
     * 1. Saudi ZATCA e-invoicing QR codes (Base64 TLV format)
     * 2. EMVCo payment QR codes (InstaPay / Meeza / Arab POS QRs)
     * 3. JSON e-receipt payloads
     * 4. URLs with receipt amount parameters
     * 5. Structured text with explicit total keyword or currency
     */
    fun parse(raw: String): ReceiptScan? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        // 1. Try ZATCA TLV format
        val zatca = ZatcaQrParser.parse(trimmed)
        if (zatca?.invoiceTotal != null && zatca.invoiceTotal.signum() > 0) {
            return ReceiptScan(
                merchant = zatca.sellerName,
                total = zatca.invoiceTotal,
                tax = zatca.vatTotal,
                items = emptyList(),
                scanMode = ScanMode.QR_CODE,
                qrPayload = trimmed,
            )
        }

        // 2. Try EMVCo payment QR (InstaPay / Meeza)
        val emv = EmvCoQrParser.parse(trimmed)
        if (emv?.amount != null && emv.amount.signum() > 0) {
            return ReceiptScan(
                merchant = emv.merchantName,
                total = emv.amount,
                items = emptyList(),
                scanMode = ScanMode.QR_CODE,
                qrPayload = trimmed,
            )
        }

        // 3. Try JSON payload
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val jsonScan = runCatching { parseJson(trimmed) }.getOrNull()
            if (jsonScan != null) return jsonScan
        }

        // 4. Try URL with explicit amount parameters
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            val urlScan = parseUrlParameters(trimmed)
            if (urlScan != null) return urlScan
            // If the QR is a URL (like ETA verification link) without amount query params,
            // strictly return null so we don't extract numbers from UUIDs or paths!
            return null
        }

        // 5. Try structured text with explicit total keyword
        val totalMatch = EXPLICIT_TOTAL_REGEX.findAll(trimmed).lastOrNull()
        if (totalMatch != null) {
            val amountStr = totalMatch.groupValues[1]
            val total = amountStr.replace(",", ".").toBigDecimalOrNull()
            if (total != null && total.signum() > 0 && total <= BigDecimal(1000000)) {
                return ReceiptScan(
                    merchant = extractMerchantFromText(trimmed),
                    total = total,
                    scanMode = ScanMode.QR_CODE,
                    qrPayload = trimmed,
                )
            }
        }

        // 6. Try amount with explicit currency
        val currencyMatch = CURRENCY_AMOUNT_REGEX.find(trimmed)
        if (currencyMatch != null) {
            val amountStr = currencyMatch.groupValues[1].ifEmpty { currencyMatch.groupValues[2] }
            val total = amountStr.replace(",", ".").toBigDecimalOrNull()
            if (total != null && total.signum() > 0 && total <= BigDecimal(1000000)) {
                return ReceiptScan(
                    merchant = extractMerchantFromText(trimmed),
                    total = total,
                    scanMode = ScanMode.QR_CODE,
                    qrPayload = trimmed,
                )
            }
        }

        // 7. Try standalone price (the entire QR payload is purely a single valid number)
        val standaloneMatch = STANDALONE_PRICE_REGEX.matchEntire(trimmed)
        if (standaloneMatch != null) {
            val total = standaloneMatch.groupValues[1].replace(",", ".").toBigDecimalOrNull()
            if (total != null && total.signum() > 0 && total <= BigDecimal(1000000)) {
                return ReceiptScan(
                    merchant = null,
                    total = total,
                    scanMode = ScanMode.QR_CODE,
                    qrPayload = trimmed,
                )
            }
        }

        return null
    }

    private fun parseUrlParameters(url: String): ReceiptScan? {
        val query = url.substringAfter('?', "")
        if (query.isEmpty()) return null

        var total: BigDecimal? = null
        var merchant: String? = null

        val params = query.split('&')
        for (p in params) {
            val pair = p.split('=', limit = 2)
            if (pair.size != 2) continue
            val key = pair[0].lowercase().trim()
            val value = java.net.URLDecoder.decode(pair[1], "UTF-8").trim()

            when (key) {
                "total", "amount", "amt", "sum", "price", "invoicetotal" -> {
                    val candidate = value.replace(",", ".").toBigDecimalOrNull()
                    if (candidate != null && candidate.signum() > 0 && candidate <= BigDecimal(1000000)) {
                        total = candidate
                    }
                }
                "merchant", "store", "vendor", "seller", "shop", "name" -> {
                    if (value.isNotBlank()) merchant = value
                }
            }
        }

        return if (total != null) {
            ReceiptScan(
                merchant = merchant,
                total = total,
                scanMode = ScanMode.QR_CODE,
                qrPayload = url,
            )
        } else {
            null
        }
    }

    private fun extractMerchantFromText(text: String): String? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val merchantLine = lines.firstOrNull { line ->
            val lower = line.lowercase()
            lower.startsWith("merchant:") || lower.startsWith("store:") || lower.startsWith("seller:") ||
                lower.startsWith("متجر:") || lower.startsWith("محل:")
        }
        if (merchantLine != null) {
            return merchantLine.substringAfter(':').trim().take(40)
        }
        val firstLine = lines.firstOrNull()
        if (firstLine != null && !EXPLICIT_TOTAL_REGEX.containsMatchIn(firstLine) && firstLine.length in 3..40 && firstLine.any { it.isLetter() }) {
            return firstLine
        }
        return null
    }

    private fun parseJson(jsonStr: String): ReceiptScan? {
        val obj = JSONObject(jsonStr)
        val totalDouble = obj.optDouble("total", Double.NaN)
        val amountDouble = obj.optDouble("amount", Double.NaN)
        val grandTotalDouble = obj.optDouble("grandTotal", Double.NaN)

        val total = when {
            !totalDouble.isNaN() -> BigDecimal.valueOf(totalDouble)
            !amountDouble.isNaN() -> BigDecimal.valueOf(amountDouble)
            !grandTotalDouble.isNaN() -> BigDecimal.valueOf(grandTotalDouble)
            else -> {
                val str = obj.optString("total").ifBlank { obj.optString("amount").ifBlank { obj.optString("grandTotal") } }
                str.replace(",", ".").toBigDecimalOrNull()
            }
        } ?: return null

        if (total.signum() <= 0 || total > BigDecimal(1000000)) return null

        val merchant = obj.optString("merchant").takeIf { it.isNotBlank() }
            ?: obj.optString("seller").takeIf { it.isNotBlank() }
            ?: obj.optString("store").takeIf { it.isNotBlank() }

        val itemsArray = obj.optJSONArray("items")
        val items = mutableListOf<ReceiptItem>()
        if (itemsArray != null) {
            for (i in 0 until itemsArray.length()) {
                val itemObj = itemsArray.optJSONObject(i) ?: continue
                val rawName = itemObj.optString("name").ifBlank { itemObj.optString("title", "Item") }
                val price = BigDecimal.valueOf(itemObj.optDouble("price", 0.0))
                val qty = itemObj.optInt("quantity", itemObj.optInt("qty", 1))
                val norm = ReceiptItemNormalizer.normalize(rawName)
                items.add(
                    ReceiptItem(
                        rawName = rawName,
                        name = norm.canonicalName,
                        price = price,
                        quantity = qty,
                        suggestedCategory = norm.suggestedCategory,
                    ),
                )
            }
        }

        return ReceiptScan(
            merchant = merchant,
            total = total,
            items = items,
            scanMode = ScanMode.QR_CODE,
            qrPayload = jsonStr,
        )
    }
}

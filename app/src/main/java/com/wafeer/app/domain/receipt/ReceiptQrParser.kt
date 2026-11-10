package com.wafeer.app.domain.receipt

import java.math.BigDecimal
import org.json.JSONObject

object ReceiptQrParser {

    /**
     * Parses a scanned QR payload, supporting:
     * 1. Saudi ZATCA e-invoicing QR codes (Base64 TLV format)
     * 2. JSON e-receipt payloads
     * 3. Plain text / amount receipts
     */
    fun parse(raw: String): ReceiptScan? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        // 1. Try ZATCA TLV format
        val zatca = ZatcaQrParser.parse(trimmed)
        if (zatca?.invoiceTotal != null) {
            val items = listOfNotNull(
                zatca.sellerName?.let {
                    val norm = ReceiptItemNormalizer.normalize(it)
                    ReceiptItem(
                        rawName = it,
                        name = norm.canonicalName,
                        price = zatca.invoiceTotal,
                        quantity = 1,
                        suggestedCategory = norm.suggestedCategory,
                    )
                },
            )
            return ReceiptScan(
                merchant = zatca.sellerName,
                total = zatca.invoiceTotal,
                tax = zatca.vatTotal,
                items = items,
                scanMode = ScanMode.QR_CODE,
                qrPayload = trimmed,
            )
        }

        // 2. Try JSON payload
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val jsonScan = runCatching { parseJson(trimmed) }.getOrNull()
            if (jsonScan != null) return jsonScan
        }

        // 3. Try plain text amount with optional merchant
        val amountRegex = Regex("""(?i)(?:total|sum|المجموع|الإجمالي|مبلغ|amount)?\s*[:=]?\s*(\d+(?:[.,]\d{1,2})?)""")
        val match = amountRegex.findAll(trimmed).lastOrNull()
        if (match != null) {
            val total = match.groupValues[1].replace(",", ".").toBigDecimalOrNull()
            if (total != null && total.signum() > 0) {
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

    private fun parseJson(jsonStr: String): ReceiptScan? {
        val obj = JSONObject(jsonStr)
        val totalDouble = obj.optDouble("total", Double.NaN)
        val total = if (!totalDouble.isNaN()) {
            BigDecimal.valueOf(totalDouble)
        } else {
            obj.optString("total").replace(",", ".").toBigDecimalOrNull()
        } ?: return null

        val merchant = obj.optString("merchant").takeIf { it.isNotBlank() }
            ?: obj.optString("seller").takeIf { it.isNotBlank() }

        val itemsArray = obj.optJSONArray("items")
        val items = mutableListOf<ReceiptItem>()
        if (itemsArray != null) {
            for (i in 0 until itemsArray.length()) {
                val itemObj = itemsArray.optJSONObject(i) ?: continue
                val rawName = itemObj.optString("name", "Item")
                val price = BigDecimal.valueOf(itemObj.optDouble("price", 0.0))
                val qty = itemObj.optInt("quantity", 1)
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

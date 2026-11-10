package com.wafeer.app.domain.receipt

import java.math.BigDecimal

data class EmvCoInvoice(
    val amount: BigDecimal?,
    val merchantName: String?,
    val currencyCode: String?,
)

object EmvCoQrParser {

    /**
     * Decodes an EMVCo merchant-presented QR code (used widely in InstaPay, Meeza, and standard POS QR codes).
     * Standard TLV format starts with "000201":
     * Tag 54: Transaction Amount
     * Tag 59: Merchant Name
     * Tag 53: Transaction Currency (ISO 4217 numeric code)
     * Tag 60: Merchant City
     */
    fun parse(raw: String): EmvCoInvoice? {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("000201")) return null

        var index = 0
        var amount: BigDecimal? = null
        var merchantName: String? = null
        var currencyCode: String? = null

        while (index + 4 <= trimmed.length) {
            val tag = trimmed.substring(index, index + 2)
            val lenStr = trimmed.substring(index + 2, index + 4)
            val len = lenStr.toIntOrNull() ?: break
            index += 4
            if (index + len > trimmed.length) break
            val value = trimmed.substring(index, index + len)
            index += len

            when (tag) {
                "54" -> amount = value.replace(",", ".").toBigDecimalOrNull()
                "59" -> merchantName = value.trim()
                "53" -> currencyCode = value.trim()
            }
        }

        return if (amount != null && amount.signum() > 0) {
            EmvCoInvoice(
                amount = amount,
                merchantName = merchantName,
                currencyCode = currencyCode,
            )
        } else {
            null
        }
    }
}

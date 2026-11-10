package com.wafeer.app.domain.receipt

import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.Base64

data class ZatcaInvoice(
    val sellerName: String?,
    val vatRegistrationNumber: String?,
    val timestamp: String?,
    val invoiceTotal: BigDecimal?,
    val vatTotal: BigDecimal?,
)

object ZatcaQrParser {

    /**
     * Decodes a Saudi ZATCA (Fatoora) e-invoicing QR code according to the
     * official TLV (Tag-Length-Value) Base64 standard:
     * Tag 1: Seller's name
     * Tag 2: VAT registration number
     * Tag 3: Time stamp (ISO 8601)
     * Tag 4: Invoice total (including VAT)
     * Tag 5: VAT total
     */
    fun parse(raw: String): ZatcaInvoice? {
        val trimmed = raw.trim()
        val bytes = runCatching {
            Base64.getDecoder().decode(trimmed)
        }.getOrNull() ?: return null
        if (bytes.size < 4) return null

        var sellerName: String? = null
        var vatRegistrationNumber: String? = null
        var timestamp: String? = null
        var invoiceTotal: BigDecimal? = null
        var vatTotal: BigDecimal? = null

        var index = 0
        while (index + 2 <= bytes.size) {
            val tag = bytes[index].toInt() and 0xFF
            val length = bytes[index + 1].toInt() and 0xFF
            index += 2
            if (index + length > bytes.size) break
            val value = String(bytes, index, length, StandardCharsets.UTF_8)
            index += length

            when (tag) {
                1 -> sellerName = value.trim()
                2 -> vatRegistrationNumber = value.trim()
                3 -> timestamp = value.trim()
                4 -> invoiceTotal = value.trim().replace(",", ".").toBigDecimalOrNull()
                5 -> vatTotal = value.trim().replace(",", ".").toBigDecimalOrNull()
            }
        }

        return if (invoiceTotal != null || !sellerName.isNullOrBlank()) {
            ZatcaInvoice(
                sellerName = sellerName,
                vatRegistrationNumber = vatRegistrationNumber,
                timestamp = timestamp,
                invoiceTotal = invoiceTotal,
                vatTotal = vatTotal,
            )
        } else {
            null
        }
    }
}

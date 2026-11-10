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
        val candidate = extractBase64Payload(trimmed)
        val bytes = decodeBytes(candidate) ?: return null
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

        return if (invoiceTotal != null && invoiceTotal.signum() > 0) {
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

    private fun extractBase64Payload(raw: String): String {
        if (raw.startsWith("http://", ignoreCase = true) || raw.startsWith("https://", ignoreCase = true)) {
            val query = raw.substringAfter('?', "")
            val params = query.split('&')
            for (p in params) {
                val pair = p.split('=', limit = 2)
                if (pair.size == 2 && pair[0].lowercase() in listOf("data", "qr", "payload", "b64", "invoice")) {
                    return java.net.URLDecoder.decode(pair[1], "UTF-8")
                }
            }
        }
        return raw
    }

    private fun decodeBytes(str: String): ByteArray? {
        val clean = str.replace("\\s".toRegex(), "")
        if (clean.length < 4) return null

        // Try standard Base64
        runCatching { Base64.getDecoder().decode(clean) }.getOrNull()?.let { return it }

        // Try URL-safe Base64
        val normalized = clean.replace('-', '+').replace('_', '/')
        val padded = normalized + "=".repeat((4 - normalized.length % 4) % 4)
        runCatching { Base64.getDecoder().decode(padded) }.getOrNull()?.let { return it }

        // Try hex if even length
        if (clean.length % 2 == 0 && clean.all { it in "0123456789abcdefABCDEF" }) {
            return runCatching {
                ByteArray(clean.length / 2) { i ->
                    clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
                }
            }.getOrNull()
        }

        return null
    }
}

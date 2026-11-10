package com.wafeer.app.domain.receipt

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.Base64

class ZatcaQrParserTest {

    private fun buildTlv(
        sellerName: String,
        vatNumber: String,
        timestamp: String,
        totalWithVat: String,
        vatAmount: String,
    ): String {
        val out = ByteArrayOutputStream()
        fun writeTag(tag: Int, value: String) {
            val bytes = value.toByteArray(StandardCharsets.UTF_8)
            out.write(tag)
            out.write(bytes.size)
            out.write(bytes)
        }

        writeTag(1, sellerName)
        writeTag(2, vatNumber)
        writeTag(3, timestamp)
        writeTag(4, totalWithVat)
        writeTag(5, vatAmount)

        return Base64.getEncoder().encodeToString(out.toByteArray())
    }

    @Test
    fun decodes_zatca_tlv_base64_invoice() {
        val base64 = buildTlv(
            sellerName = "متجر مواظب ومقتنيات",
            vatNumber = "310122393500003",
            timestamp = "2026-11-10T14:30:00Z",
            totalWithVat = "575.00",
            vatAmount = "75.00",
        )

        val invoice = ZatcaQrParser.parse(base64)

        assertThat(invoice).isNotNull()
        assertThat(invoice?.sellerName).isEqualTo("متجر مواظب ومقتنيات")
        assertThat(invoice?.vatRegistrationNumber).isEqualTo("310122393500003")
        assertThat(invoice?.timestamp).isEqualTo("2026-11-10T14:30:00Z")
        assertThat(invoice?.invoiceTotal).isEqualTo(BigDecimal("575.00"))
        assertThat(invoice?.vatTotal).isEqualTo(BigDecimal("75.00"))
    }

    @Test
    fun decodes_zatca_with_whitespace_and_newlines() {
        val base64 = buildTlv(
            sellerName = "سوبرماركت التميمي",
            vatNumber = "310122393500003",
            timestamp = "2026-11-10T12:00:00Z",
            totalWithVat = "120.00",
            vatAmount = "15.65",
        )

        val formattedWithNewlines = "  \n" + base64.chunked(20).joinToString("\n") + "\n "
        val invoice = ZatcaQrParser.parse(formattedWithNewlines)

        assertThat(invoice).isNotNull()
        assertThat(invoice?.sellerName).isEqualTo("سوبرماركت التميمي")
        assertThat(invoice?.invoiceTotal).isEqualTo(BigDecimal("120.00"))
    }

    @Test
    fun decodes_zatca_from_url_query_parameter() {
        val base64 = buildTlv(
            sellerName = "بنده",
            vatNumber = "310122393500003",
            timestamp = "2026-11-10T12:00:00Z",
            totalWithVat = "85.50",
            vatAmount = "11.15",
        )

        val url = "https://zatca.gov.eg/verify?data=$base64"
        val invoice = ZatcaQrParser.parse(url)

        assertThat(invoice).isNotNull()
        assertThat(invoice?.sellerName).isEqualTo("بنده")
        assertThat(invoice?.invoiceTotal).isEqualTo(BigDecimal("85.50"))
    }

    @Test
    fun returns_null_on_invalid_or_non_zatca_input() {
        assertThat(ZatcaQrParser.parse("hello world")).isNull()
        assertThat(ZatcaQrParser.parse("")).isNull()
    }
}

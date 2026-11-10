package com.wafeer.app.domain.receipt

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class ReceiptQrParserTest {

    @Test
    fun parses_json_receipt_payload() {
        val json = """
            {
                "merchant": "Zara Store",
                "total": 350.50,
                "items": [
                    { "name": "dragon milton", "price": 350.50, "quantity": 1 }
                ]
            }
        """.trimIndent()

        val scan = ReceiptQrParser.parse(json)

        assertThat(scan).isNotNull()
        assertThat(scan?.merchant).isEqualTo("Zara Store")
        assertThat(scan?.total).isEqualTo(BigDecimal.valueOf(350.50))
        assertThat(scan?.scanMode).isEqualTo(ScanMode.QR_CODE)
        assertThat(scan?.items).hasSize(1)
        assertThat(scan?.items?.first()?.name).isEqualTo("بنطلون جينز")
        assertThat(scan?.items?.first()?.suggestedCategory).isEqualTo("ملابس")
    }

    @Test
    fun parses_plain_text_total_qr() {
        val text = "Invoice Total: 199.99 EGP"
        val scan = ReceiptQrParser.parse(text)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("199.99"))
        assertThat(scan?.scanMode).isEqualTo(ScanMode.QR_CODE)
    }
}

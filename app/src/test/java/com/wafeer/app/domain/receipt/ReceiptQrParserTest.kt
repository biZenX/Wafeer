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

    @Test
    fun parses_emvco_payment_qr_code() {
        // EMVCo format: 000201... 5406150.00 5915Carrefour Egypt
        val emvCoPayload = "0002010102125406150.0053038185915Carrefour Egypt6304ABCD"
        val scan = ReceiptQrParser.parse(emvCoPayload)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("150.00"))
        assertThat(scan?.merchant).isEqualTo("Carrefour Egypt")
        assertThat(scan?.scanMode).isEqualTo(ScanMode.QR_CODE)
    }

    @Test
    fun parses_url_with_amount_query_parameter() {
        val url = "https://pay.example.com/receipt?amount=245.50&store=Starbucks"
        val scan = ReceiptQrParser.parse(url)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("245.50"))
        assertThat(scan?.merchant).isEqualTo("Starbucks")
    }

    @Test
    fun parses_arabic_key_value_qr() {
        val text = """
            متجر: كارفور المعادي
            رقم الفاتورة: 987654
            المجموع: 450.00
            الضريبة: 60.00
        """.trimIndent()

        val scan = ReceiptQrParser.parse(text)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("450.00"))
        assertThat(scan?.merchant).isEqualTo("كارفور المعادي")
    }

    @Test
    fun rejects_eta_verification_url_without_amount() {
        val etaUrl = "https://invoicing.eta.gov.eg/receipts/search/38b82941-8402-48a1-9481-998248102847"
        val scan = ReceiptQrParser.parse(etaUrl)

        // Must NOT return huge numbers parsed from UUID!
        assertThat(scan).isNull()
    }

    @Test
    fun rejects_phone_numbers_and_barcodes() {
        assertThat(ReceiptQrParser.parse("01012345678")).isNull()
        assertThat(ReceiptQrParser.parse("6281001234567")).isNull()
        assertThat(ReceiptQrParser.parse("Welcome to our store, check #12345")).isNull()
    }
}

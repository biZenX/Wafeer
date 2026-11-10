package com.wafeer.app.domain.receipt

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class ReceiptParserMultiItemTest {

    @Test
    fun parses_receipt_with_multiple_items_and_reconciles_total() {
        val receiptText = """
            Zara Boutique
            123 Fashion Street
            بنطلون dragon milton  350.00
            skirt pink 38         200.00
            المجموع               550.00
            كاش                  600.00
            الباقي                50.00
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("550.00"))
        assertThat(scan?.merchant).isEqualTo("Zara Boutique")
        assertThat(scan?.items).hasSize(2)

        val item1 = scan!!.items[0]
        assertThat(item1.name).isEqualTo("بنطلون جينز")
        assertThat(item1.price).isEqualTo(BigDecimal("350.00"))
        assertThat(item1.suggestedCategory).isEqualTo("ملابس")

        val item2 = scan.items[1]
        assertThat(item2.name).isEqualTo("جيبة")
        assertThat(item2.price).isEqualTo(BigDecimal("200.00"))
        assertThat(item2.suggestedCategory).isEqualTo("ملابس")
    }

    @Test
    fun calculates_total_from_items_when_total_label_missing() {
        val receiptText = """
            سوبرماركت البركة
            جبنة دومتي فيتا 500جم  45.00
            وجبة برجر             120.00
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("165.00"))
        assertThat(scan?.items).hasSize(2)
        assertThat(scan?.items?.get(0)?.suggestedCategory).isEqualTo("بقالة")
        assertThat(scan?.items?.get(1)?.suggestedCategory).isEqualTo("مطاعم")
    }
}

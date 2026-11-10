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

    @Test
    fun ignores_tax_ids_cr_phones_and_extracts_correct_products_and_total() {
        val receiptText = """
            هايبر وان Hyper One
            سجل تجاري: 1010345678
            الرقم الضريبي: 300054321000003
            هاتف: 01012345678
            تاريخ: 2026/11/10 14:30
            رقم الفاتورة: 98765432
            كاشير: محمد
            بنطلون جينز  350.00
            قميص قطن    200.00
            المجموع الكلي 550.00
            الضريبة 14%  77.00
            صافي القيمة  627.00
            شكرا لزيارتكم
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.merchant).isEqualTo("هايبر وان Hyper One")
        assertThat(scan?.total).isEqualTo(BigDecimal("550.00"))
        assertThat(scan?.items).hasSize(2)

        val item1 = scan!!.items[0]
        assertThat(item1.name).isEqualTo("بنطلون جينز")
        assertThat(item1.price).isEqualTo(BigDecimal("350.00"))

        val item2 = scan.items[1]
        assertThat(item2.name).isEqualTo("قميص وملابس")
        assertThat(item2.price).isEqualTo(BigDecimal("200.00"))

        // Ensure metadata is never treated as products
        val itemNames = scan.items.map { it.rawName }
        assertThat(itemNames).doesNotContain("سجل تجاري")
        assertThat(itemNames).doesNotContain("الرقم الضريبي")
        assertThat(itemNames).doesNotContain("هاتف")
        assertThat(itemNames).doesNotContain("تاريخ")
    }

    @Test
    fun parses_receipt_with_integer_prices() {
        val receiptText = """
            سوبرماركت التوفير
            جبنة دومتي 35
            شاي ليبتون 25
            المجموع 60
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("60"))
        assertThat(scan?.items).hasSize(2)
        assertThat(scan?.items?.get(0)?.price).isEqualTo(BigDecimal("35"))
        assertThat(scan?.items?.get(1)?.price).isEqualTo(BigDecimal("25"))
    }

    @Test
    fun parses_receipt_with_split_lines_name_then_price() {
        val receiptText = """
            زارا
            بنطلون جينز
            350.00
            تيشيرت
            150.00
            المجموع
            500.00
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("500.00"))
        assertThat(scan?.items).hasSize(2)
        assertThat(scan?.items?.get(0)?.name).isEqualTo("بنطلون جينز")
        assertThat(scan?.items?.get(0)?.price).isEqualTo(BigDecimal("350.00"))
        assertThat(scan?.items?.get(1)?.name).isEqualTo("قميص وملابس")
        assertThat(scan?.items?.get(1)?.price).isEqualTo(BigDecimal("150.00"))
    }

    @Test
    fun rejects_inflated_total_and_reconciles_from_items() {
        val receiptText = """
            محل ملابس
            بنطلون جينز 300.00
            تيشيرت 200.00
            المجموع الكلي 1010345678
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        // Must reconcile to sum of items (500.00), not the commercial register number 1010345678!
        assertThat(scan?.total).isEqualTo(BigDecimal("500.00"))
        assertThat(scan?.items).hasSize(2)
    }

    @Test
    fun recognizes_arabic_with_tatweel_and_harakat() {
        val receiptText = """
            سوبرماركت المدينة
            فَاتُورَة ضريبية
            شَاي لِيبْتُون 30.00
            جُبْنَة دُومْتِي 45.00
            المَجْمُـوع 75.00
            الإجْمَـالِي 75.00
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("75.00"))
        assertThat(scan?.items).hasSize(2)
    }

    @Test
    fun rejects_boun_table_cashier_branch_and_street_from_becoming_items() {
        val receiptText = """
            مطعم الشرق
            فرع رقم 12
            شارع 9 المعادي
            كاشير: أحمد 3
            طاولة: 7
            بون رقم: 9845
            مسلسل: 12345
            ب.ض : 456-789-123
            وجبة برجر 120.00
            عصير برتقال 40.00
            المجموع 160.00
            شكرا لزيارتكم
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("160.00"))
        assertThat(scan?.items).hasSize(2)
        val itemNames = scan!!.items.map { it.rawName }
        assertThat(itemNames).doesNotContain("فرع")
        assertThat(itemNames).doesNotContain("شارع")
        assertThat(itemNames).doesNotContain("كاشير")
        assertThat(itemNames).doesNotContain("طاولة")
        assertThat(itemNames).doesNotContain("بون")
        assertThat(itemNames).doesNotContain("مسلسل")
        assertThat(itemNames).doesNotContain("ب.ض")
    }

    @Test
    fun selects_last_net_total_over_prediscount_subtotal() {
        val receiptText = """
            هايبر ماركت
            قميص قطن 300.00
            المجموع: 300.00
            خصم: 50.00
            صافي المطلوب: 250.00
            كاش: 300.00
            الباقي: 50.00
        """.trimIndent()

        val scan = ReceiptParser.parse(receiptText)

        assertThat(scan).isNotNull()
        assertThat(scan?.total).isEqualTo(BigDecimal("250.00"))
    }
}

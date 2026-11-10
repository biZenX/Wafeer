package com.wafeer.app.domain.receipt

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReceiptItemNormalizerTest {

    @Test
    fun strips_color_and_size_noise_from_skirt() {
        val raw = "skirt pink 38"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.cleanName).isEqualTo("skirt")
        assertThat(norm.canonicalName).isEqualTo("جيبة")
        assertThat(norm.suggestedCategory).isEqualTo("ملابس")
        assertThat(norm.suggestedCategoryEn).isEqualTo("Clothes")
    }

    @Test
    fun maps_dragon_milton_to_jeans_pants() {
        val raw = "بنطلون dragon milton"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("بنطلون جينز")
        assertThat(norm.suggestedCategory).isEqualTo("ملابس")
    }

    @Test
    fun maps_wpsw_to_pants() {
        val raw = "w.p.s.w"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("بنطلون جينز")
        assertThat(norm.suggestedCategory).isEqualTo("ملابس")
    }

    @Test
    fun cleans_and_normalizes_cheese_and_dairy() {
        val raw = "جبنة دومتي فيتا 500جم"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("جبنة ومنتجات ألبان")
        assertThat(norm.suggestedCategory).isEqualTo("بقالة")
    }

    @Test
    fun normalizes_dining_and_meals() {
        val raw = "وجبة برجر كومبو كبير"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("وجبة مطعم")
        assertThat(norm.suggestedCategory).isEqualTo("مطاعم")
    }

    @Test
    fun normalizes_transportation_and_fuel() {
        val raw = "بنزين 92"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("مواصلات ووقود")
        assertThat(norm.suggestedCategory).isEqualTo("مواصلات")
    }

    @Test
    fun cleans_sku_units_and_punctuation() {
        val raw = "Ref#1234 Jeans Blue XL 1kg"
        val cleaned = ReceiptItemNormalizer.cleanNoise(raw)

        assertThat(cleaned).isEqualTo("Jeans")
    }

    @Test
    fun normalizes_fruits_and_veggies() {
        val raw = "طماطم 2كجم"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("خضار وفاكهة")
        assertThat(norm.suggestedCategory).isEqualTo("بقالة")
    }

    @Test
    fun normalizes_electronics() {
        val raw = "شاحن سريع 20w"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("إلكترونيات وملحقاتها")
        assertThat(norm.suggestedCategory).isEqualTo("إلكترونيات")
    }

    @Test
    fun normalizes_telecom_and_bills() {
        val raw = "كارت شحن فودافون"
        val norm = ReceiptItemNormalizer.normalize(raw)

        assertThat(norm.canonicalName).isEqualTo("فواتير وشحن")
        assertThat(norm.suggestedCategory).isEqualTo("فواتير")
    }
}

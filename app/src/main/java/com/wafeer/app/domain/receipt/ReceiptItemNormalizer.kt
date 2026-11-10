package com.wafeer.app.domain.receipt

import java.util.Locale

data class NormalizedProduct(
    val cleanName: String,
    val canonicalName: String,
    val suggestedCategory: String,
    val suggestedCategoryEn: String,
)

object ReceiptItemNormalizer {

    // Noise tokens to strip from raw item descriptions
    private val COLOR_WORDS = setOf(
        "pink", "blue", "black", "white", "red", "green", "yellow", "grey", "gray", "brown", "purple", "orange",
        "dark", "light", "navy", "beige",
        "وردي", "بمبي", "أزرق", "ازرق", "كحلي", "أسود", "اسود", "أبيض", "ابيض", "أحمر", "احمر",
        "أخضر", "اخضر", "أصفر", "اصفر", "رمادي", "بني", "بيج", "فاتح", "غامق",
    )

    private val SIZE_WORDS = setOf(
        "xs", "s", "m", "l", "xl", "xxl", "2xl", "3xl", "4xl",
        "size", "مقاس", "حجم", "كبير", "صغير", "وسط",
        "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50",
    )

    private val UNIT_AND_WEIGHT_REGEX = Regex("""(?i)\b\d+(?:[.,]\d+)?\s*(?:kg|g|gm|gram|ltr|l|ml|pcs|pc|كجم|جم|جرام|لتر|مل|قطع|قطعة)\b""")
    private val CODE_OR_SKU_REGEX = Regex("""(?i)\b(?:art|sku|ref|code|item|no|كود|رقم|صنف)[:#\-]?\s*\w+\b""")
    private val SYMBOLS_REGEX = Regex("""[#*_~|\\/\[\]{}()<>=+]""")
    private val MULTI_SPACE = Regex("""\s+""")

    private data class ProductRule(
        val triggers: List<String>,
        val canonicalAr: String,
        val canonicalEn: String,
        val categoryAr: String,
        val categoryEn: String,
    )

    private val RULES = listOf(
        // Pants / Jeans / Trousers (including specific brands/styles mentioned by user: dragon milton, w.p.s.w, denim)
        ProductRule(
            triggers = listOf("dragon milton", "milton", "w.p.s.w", "wpsw", "jeans", "جينز", "بنطلون", "بنطال", "سروال", "denim", "trousers", "pants", "chinos", "jogger"),
            canonicalAr = "بنطلون جينز",
            canonicalEn = "Jeans",
            categoryAr = "ملابس",
            categoryEn = "Clothes",
        ),
        // Skirt
        ProductRule(
            triggers = listOf("skirt", "جيبة", "تنورة"),
            canonicalAr = "جيبة",
            canonicalEn = "Skirt",
            categoryAr = "ملابس",
            categoryEn = "Clothes",
        ),
        // Shirts & Tops
        ProductRule(
            triggers = listOf("shirt", "t-shirt", "tshirt", "قميص", "تيشيرت", "بلوزة", "blouse", "top", "hoodie", "هودي", "sweatshirt", "سويت شيرت", "jacket", "جاكيت", "coat", "معطف", "dress", "فستان", "عباية", "عباءة", "بدلة", "suit"),
            canonicalAr = "قميص وملابس",
            canonicalEn = "Shirt & Apparel",
            categoryAr = "ملابس",
            categoryEn = "Clothes",
        ),
        // Shoes & Footwear
        ProductRule(
            triggers = listOf("shoes", "sneakers", "حذاء", "كوتشي", "شوز", "boots", "بوت", "slipper", "شبشب", "صندل", "sandal"),
            canonicalAr = "حذاء",
            canonicalEn = "Footwear",
            categoryAr = "ملابس",
            categoryEn = "Clothes",
        ),
        // Dairy products
        ProductRule(
            triggers = listOf("cheese", "جبنة", "جبن", "domty", "دومتي", "feta", "فيتا", "cheddar", "شيدر", "رومي", "mozzarella", "موزاريلا", "dairy product", "dairy", "milk", "لبن", "حليب", "زبادي", "yogurt", "لبنة", "قشطة", "cream", "butter", "زبدة"),
            canonicalAr = "جبنة ومنتجات ألبان",
            canonicalEn = "Dairy Products",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Groceries & Bakery
        ProductRule(
            triggers = listOf("bread", "خبز", "عيش", "فينو", "توست", "toast", "كرواسون", "croissant", "bakery", "مخبوزات", "eggs", "بيض", "rice", "أرز", "رز", "oil", "زيت", "sugar", "سكر", "tea", "شاي", "coffee", "قهوة", "نسكافيه", "nescafe"),
            canonicalAr = "بقالة ومخبوزات",
            canonicalEn = "Groceries & Bakery",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Dining & Fast Food
        ProductRule(
            triggers = listOf("burger", "برجر", "pizza", "بيتزا", "shawarma", "شاورما", "kebab", "كباب", "chicken", "دجاج", "فراخ", "meat", "لحم", "sandwich", "ساندوتش", "وجبة", "meal", "fries", "بطاطس", "restaurant", "مطعم"),
            canonicalAr = "وجبة مطعم",
            canonicalEn = "Dining Meal",
            categoryAr = "مطاعم",
            categoryEn = "Food & Dining",
        ),
        // Beverages
        ProductRule(
            triggers = listOf("water", "مياه", "ماء", "juice", "عصير", "pepsi", "بيبسي", "cola", "كولا", "soda", "صودا", "latte", "لاتيه", "cappuccino", "كابتشينو", "espresso", "إسبريسو"),
            canonicalAr = "مشروبات",
            canonicalEn = "Beverages",
            categoryAr = "مشروبات",
            categoryEn = "Beverages",
        ),
        // Transport & Fuel
        ProductRule(
            triggers = listOf("gas", "بنزين", "fuel", "وقود", "metro", "مترو", "uber", "أوبر", "careem", "كريم", "taxi", "تاكسي", "bus", "باص", "أوتوبيس", "parking", "موقف"),
            canonicalAr = "مواصلات ووقود",
            canonicalEn = "Transportation",
            categoryAr = "مواصلات",
            categoryEn = "Transportation",
        ),
        // Fruits & Vegetables
        ProductRule(
            triggers = listOf("fruits", "vegetables", "خضار", "فاكهة", "طماطم", "خيار", "تفاح", "موز", "برتقال", "ليمون", "بصل", "ثوم"),
            canonicalAr = "خضار وفاكهة",
            canonicalEn = "Fruits & Veggies",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Meat & Poultry
        ProductRule(
            triggers = listOf("meat", "chicken", "beef", "fish", "tuna", "لحم", "لحوم", "فراخ", "دجاج", "كفتة", "سمك", "تونة"),
            canonicalAr = "لحوم ودواجن",
            canonicalEn = "Meat & Poultry",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Snacks & Confectionery
        ProductRule(
            triggers = listOf("chips", "chocolate", "biscuit", "candy", "cake", "ice cream", "cookies", "شيبسي", "شيبس", "شوكولاتة", "بسكويت", "كيك", "ايس كريم", "حلويات", "لب", "مكسرات"),
            canonicalAr = "تسالي وحلويات",
            canonicalEn = "Snacks & Sweets",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Household & Cleaning
        ProductRule(
            triggers = listOf("detergent", "soap", "shampoo", "tissue", "bleach", "منظفات", "مسحوق", "صابون", "كلور", "مناديل", "بريل", "اريال", "تايد"),
            canonicalAr = "منظفات ومستلزمات منزلية",
            canonicalEn = "Household & Cleaning",
            categoryAr = "بقالة",
            categoryEn = "Groceries",
        ),
        // Electronics & Accessories
        ProductRule(
            triggers = listOf("charger", "cable", "headphone", "airpods", "case", "mobile", "phone", "laptop", "battery", "شاحن", "كابل", "سلك", "سماعة", "جراب", "موبايل", "هاتف", "بطارية"),
            canonicalAr = "إلكترونيات وملحقاتها",
            canonicalEn = "Electronics",
            categoryAr = "إلكترونيات",
            categoryEn = "Electronics",
        ),
        // Utilities & Telecom
        ProductRule(
            triggers = listOf("vodafone", "orange", "etisalat", "we", "stc", "mobily", "zain", "recharge", "electricity", "water bill", "gas bill", "فودافون", "اورانج", "اتصالات", "وي", "شحن رصيد", "كهرباء", "غاز", "فاتورة نت"),
            canonicalAr = "فواتير وشحن",
            canonicalEn = "Bills & Top-up",
            categoryAr = "فواتير",
            categoryEn = "Bills",
        ),
        // Health & Personal Care
        ProductRule(
            triggers = listOf("pharmacy", "صيدلية", "medicine", "دواء", "علاج", "panadol", "بنادول", "vitamin", "فيتامين", "shampoo", "شامبو", "soap", "صابون", "toothpaste", "معجون"),
            canonicalAr = "صحة وعناية",
            canonicalEn = "Health & Care",
            categoryAr = "صحة وعناية",
            categoryEn = "Health & Pharmacy",
        ),
    )

    /**
     * Cleans noise (colors, sizes, codes, units) from a line item,
     * resolves canonical product name and infers category.
     */
    fun normalize(rawText: String): NormalizedProduct {
        val cleaned = cleanNoise(rawText)
        val normalizedLower = cleaned.lowercase(Locale.ROOT)
        val rawLower = rawText.lowercase(Locale.ROOT)

        for (rule in RULES) {
            val matched = rule.triggers.any { trigger ->
                val trigLower = trigger.lowercase(Locale.ROOT)
                containsWord(normalizedLower, trigLower) || containsWord(rawLower, trigLower)
            }
            if (matched) {
                return NormalizedProduct(
                    cleanName = if (cleaned.isNotBlank()) cleaned else rule.canonicalAr,
                    canonicalName = rule.canonicalAr,
                    suggestedCategory = rule.categoryAr,
                    suggestedCategoryEn = rule.categoryEn,
                )
            }
        }

        // Fallback when no rule matches
        val fallbackName = cleaned.ifBlank { rawText.trim() }
        return NormalizedProduct(
            cleanName = fallbackName,
            canonicalName = fallbackName,
            suggestedCategory = "عام",
            suggestedCategoryEn = "General",
        )
    }

    /**
     * Removes noise tokens (colors, sizes, codes, weight/units)
     */
    fun cleanNoise(text: String): String {
        var result = CODE_OR_SKU_REGEX.replace(text, " ")
        result = UNIT_AND_WEIGHT_REGEX.replace(result, " ")
        result = SYMBOLS_REGEX.replace(result, " ")

        val words = result.split(MULTI_SPACE)
        val filtered = words.filter { word ->
            val lower = word.lowercase(Locale.ROOT).trim('.', ',', '-', ':')
            lower.length > 1 &&
                lower !in COLOR_WORDS &&
                lower !in SIZE_WORDS &&
                !lower.all { it.isDigit() }
        }

        return filtered.joinToString(" ").trim()
    }

    private fun containsWord(haystack: String, needle: String): Boolean {
        if (haystack == needle) return true
        val index = haystack.indexOf(needle)
        if (index < 0) return false
        val beforeOk = index == 0 || !haystack[index - 1].isLetterOrDigit()
        val afterIndex = index + needle.length
        val afterOk = afterIndex >= haystack.length || !haystack[afterIndex].isLetterOrDigit()
        return beforeOk && afterOk
    }
}

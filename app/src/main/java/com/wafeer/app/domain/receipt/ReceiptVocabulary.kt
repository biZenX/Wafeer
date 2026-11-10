package com.wafeer.app.domain.receipt

import com.wafeer.app.domain.model.SupportedCurrency
import java.text.Normalizer
import java.util.Locale

internal object ReceiptVocabulary {

    private val TOTAL_WORDS = listOf(
        "total", "grand total", "amount due", "balance due", "to pay", "amount payable",
        "importe", "a pagar", "monto", "total a pagar",
        "summe", "gesamt", "gesamtsumme", "gesamtbetrag", "endsumme", "endbetrag", "zu zahlen", "betrag",
        "montant", "à payer", "net à payer",
        "totale", "importo", "da pagare", "totale complessivo",
        "valor total", "valor a pagar",
        "итог", "итого", "всего", "к оплате", "сумма",
        "общо", "сума", "обща сума", "за плащане",
        "σύνολο", "γενικό σύνολο", "πληρωτέο",
        "جمع کل", "جمع", "مبلغ کل", "قابل پرداخت", "مبلغ قابل پرداخت",
        "المجموع", "الإجمالي", "الاجمالي", "صافي المطلوب", "المستحق", "إجمالي الفاتورة", "اجمالي الفاتورة", "المجموع الكلي", "المبلغ الكلي", "الحساب", "المبلغ المطلوب",
        "कुल", "कुल राशि", "देय राशि", "योग",
        "合計", "総合計", "総額", "お会計", "ご請求額",
        "합계", "총액", "총계", "결제금액", "받을금액",
        "合计", "总计", "總計", "总额", "總額", "应付", "應付", "实付", "實付", "应收", "應收",
    )

    private val SUBTOTAL_WORDS = listOf(
        "subtotal", "sub total", "sub-total",
        "zwischensumme",
        "sous-total", "sous total",
        "subtotale",
        "подытог", "промежуточный итог",
        "междинна сума",
        "μερικό σύνολο", "υποσύνολο",
        "جمع جزء", "جمع فرعی",
        "المجموع الفرعي", "المجموع الجزئي", "صافي القيمة",
        "उप योग", "उप-योग", "उप कुल", "उपकुल",
        "小計", "小计",
        "소계", "공급가액",
    )

    private val PAYMENT_WORDS = listOf(
        "cash", "change", "tendered",
        "efectivo", "cambio", "pago con", "su cambio",
        "bar", "gegeben", "rückgeld",
        "espèces", "rendu", "monnaie",
        "contanti", "contante", "resto",
        "dinheiro", "troco",
        "наличные", "сдача", "получено",
        "в брой", "ресто",
        "μετρητά", "ρέστα",
        "نقد", "نقدی", "باقیمانده",
        "نقدا", "نقداً", "كاش", "مدى", "فيزا", "بطاقة", "الباقي", "الدفع",
        "नकद", "वापसी", "शेष",
        "お預り", "お預かり", "お釣り", "おつり", "釣銭",
        "현금", "거스름돈", "받은금액",
        "现金", "現金", "找零", "找贖", "实收", "實收",
    )

    private val TAX_OR_COUNT_WORDS = listOf(
        "tax", "vat", "gst", "cgst", "sgst", "items", "qty",
        "iva", "impuesto", "artículos", "cantidad",
        "mwst", "ust", "steuer", "artikel",
        "tva", "articles",
        "articoli",
        "icms", "itens",
        "ндс", "позиций",
        "ддс",
        "φπα",
        "مالیات", "ارزش افزوده",
        "ضريبة", "الضريبة", "ضريبة القيمة المضافة", "القيمة المضافة",
        "कर", "जीएसटी",
        "消費税", "点数",
        "부가세", "과세", "수량",
        "税额", "稅額", "税金", "数量",
    )

    private val METADATA_WORDS = listOf(
        // Commercial register
        "سجل تجاري", "س.ت", "ست:", "cr:", "c.r", "c.r.", "commercial reg", "registration no",
        // Tax & VAT
        "الرقم الضريبي", "رقم ضريبي", "بطاقة ضريبية", "تسجيل ضريبي", "رقم التسجيل", "ض.ق.م", "ضريبة مبيعات",
        "vat no", "vat reg", "vat id", "tax id", "trn", "tin", "rfc", "cif", "nif", "steuernummer",
        // Phone & Contact
        "هاتف", "تليفون", "موبايل", "جوال", "فاكس", "خدمة العملاء", "خدمه العملاء", "خدمة عملاء", "خط ساخن", "اتصل بنا",
        "tel:", "phone:", "mobile:", "hotline:", "call:",
        // Date & Time
        "تاريخ", "التاريخ", "وقت", "الوقت", "ساعة", "الساعة", "date:", "time:",
        // Invoice / Order / Reference / Cashier / Branch / Table
        "رقم الفاتورة", "فاتورة رقم", "رقم الإيصال", "ايصال رقم", "إيصال رقم", "رقم الطلب", "طلب رقم",
        "رقم الشيك", "شيك رقم", "رقم العملية", "رقم المرجع", "المرجع", "كود الفاتورة", "رقم الحساب",
        "كاشير", "الكاشير", "نقطة بيع", "وردية", "فرع", "الفرع", "طاولة", "ترابيزة", "صالة", "تيك اواي", "دليفري", "توصيل",
        "invoice no", "inv no", "invoice #", "receipt no", "receipt #", "order no", "order #", "bill no", "check no",
        "ref no", "reference", "cashier", "terminal", "pos", "branch", "table", "shift", "server", "account no",
        // Address & Location
        "street", "st.", "road", "rd.", "avenue", "ave.", "building", "bldg", "floor", "suite", "po box", "p.o. box",
        "شارع", "طريق", "ميدان", "حي", "عمارة", "مبنى", "ص.ب", "صندوق بريد",
        // Greetings & Policies & Web
        "شكرا لزيارتكم", "شكراً لزيارتكم", "شكرا لك", "شكراً لك", "البضاعة المباعة", "لا ترد ولا تستبدل", "يسعدنا خدمتكم",
        "thank you", "thanks for visiting", "welcome", "visit again", "www.", ".com", ".net", ".org", "facebook", "instagram",
    )

    private val NOISE_WORDS = listOf(
        "ticket", "receipt", "welcome", "invoice",
        "factura", "rfc", "recibo", "bienvenido", "bienvenida",
        "quittung", "kassenbon", "beleg", "rechnung",
        "reçu", "facture",
        "ricevuta", "scontrino",
        "cupom", "nota fiscal",
        "чек", "кассовый чек", "квитанция",
        "касов бон", "фискален бон",
        "απόδειξη",
        "رسید", "فاکتور",
        "فاتورة", "فاتورة ضريبية", "إيصال", "ايصال", "مرحباً", "مرحبا",
        "रसीद", "बिल",
        "レシート", "領収書",
        "영수증",
        "收据", "收據", "小票", "发票", "發票",
    )

    private val CURRENCY_WORDS = SupportedCurrency.ALL.flatMap { listOf(it.code, it.symbol) } +
        listOf("¥", "￥", "円", "元", "₩", "원", "₹", "₽", "руб", "лв", "ریال", "تومان", "ج.م", "ريال", "درهم", "د.ك", "د.إ", "ر.س", "ر.ع", "د.ب")

    val DATE_REGEX = Regex("""(?<!\d)\d{1,4}[/\-.](?:0?[1-9]|1[0-2])[/\-.](?:\d{2,4}|\d{1,2})(?!\d)""")
    val TIME_REGEX = Regex("""(?<!\d)(?:0?[1-9]|1[0-2]|2[0-3]):[0-5]\d(?::[0-5]\d)?(?:\s*(?:am|pm|ص|م))?(?!\d)""", RegexOption.IGNORE_CASE)
    val PHONE_REGEX = Regex("""(?<!\d)(?:01[0125]\d{8}|0[2345]\d{7,8}|\+?\d{1,3}[- ]?\d{7,10})(?!\d)""")
    val LONG_ID_REGEX = Regex("""(?<![\d.,])\d{8,16}(?![\d.,])""")

    private val totals = TOTAL_WORDS.map(::normalize)
    private val subtotals = SUBTOTAL_WORDS.map(::normalize)
    private val payments = PAYMENT_WORDS.map(::normalize)
    private val taxesOrCounts = TAX_OR_COUNT_WORDS.map(::normalize)
    private val metadata = METADATA_WORDS.map(::normalize)
    private val noise = NOISE_WORDS.map(::normalize)
    private val currencies = CURRENCY_WORDS.map(::normalize).filter { it.isNotBlank() }.distinct()

    fun isTotal(line: String): Boolean {
        if (isMetadata(line)) return false
        return containsAny(line, totals)
    }

    fun isSubtotal(line: String) = containsAny(line, subtotals)

    fun isPayment(line: String) = containsAny(line, payments)

    fun isTaxOrCount(line: String) = containsAny(line, taxesOrCounts)

    fun isMetadata(line: String): Boolean {
        if (containsAny(line, metadata)) return true
        val stripped = line.trim()
        if (DATE_REGEX.matches(stripped) || PHONE_REGEX.matches(stripped) || LONG_ID_REGEX.matches(stripped)) return true
        return false
    }

    fun isNoise(line: String) = containsAny(line, noise)

    fun hasCurrency(line: String) = containsAny(line, currencies)

    fun asciiDigits(text: String): String = buildString(text.length) {
        text.forEach { c ->
            append(
                when (c) {
                    in '٠'..'٩' -> '0' + (c - '٠')
                    in '۰'..'۹' -> '0' + (c - '۰')
                    in '०'..'९' -> '0' + (c - '०')
                    in '０'..'９' -> '0' + (c - '０')
                    '٫', '．' -> '.'
                    '٬', '，' -> ','
                    else -> c
                },
            )
        }
    }

    private fun containsAny(line: String, words: List<String>): Boolean {
        val haystack = normalize(line)
        return words.any { word -> occursAsWord(haystack, word) }
    }

    private fun occursAsWord(haystack: String, word: String): Boolean {
        var index = haystack.indexOf(word)
        while (index >= 0) {
            val end = index + word.length
            val startOk = !needsBoundary(word.first()) || !haystack.isWordCharAt(index - 1)
            val endOk = !needsBoundary(word.last()) || !haystack.isWordCharAt(end)
            if (startOk && endOk) return true
            index = haystack.indexOf(word, index + 1)
        }
        return false
    }

    private fun needsBoundary(c: Char): Boolean {
        if (!c.isLetterOrMark()) return false
        return when (Character.UnicodeScript.of(c.code)) {
            Character.UnicodeScript.HAN,
            Character.UnicodeScript.HIRAGANA,
            Character.UnicodeScript.KATAKANA,
            Character.UnicodeScript.HANGUL -> false
            else -> true
        }
    }

    private fun String.isWordCharAt(index: Int): Boolean = getOrNull(index)?.isLetterOrMark() == true

    private fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        val stripped = buildString(decomposed.length) {
            decomposed.forEach { c ->
                when (c) {
                    in '̀'..'ͯ' -> Unit
                    'ς' -> append('σ')
                    'ك' -> append('ک')
                    'ي', 'ى' -> append('ی')
                    'أ', 'إ', 'آ' -> append('ا')
                    'ة' -> append('ه')
                    else -> append(c)
                }
            }
        }
        return Normalizer.normalize(stripped, Normalizer.Form.NFC)
    }
}

internal fun Char.isLetterOrMark(): Boolean = isLetter() ||
    Character.getType(this).let {
        it == Character.NON_SPACING_MARK.toInt() || it == Character.COMBINING_SPACING_MARK.toInt()
    }

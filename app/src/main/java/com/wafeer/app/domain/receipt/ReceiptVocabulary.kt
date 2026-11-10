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
        "سجل تجاري", "سجل", "س.ت", "س.ت.", "س ت", "ست", "ست:", "cr", "cr:", "c.r", "c.r.", "commercial reg", "registration no", "reg no",
        // Tax & VAT
        "الرقم الضريبي", "رقم ضريبي", "بطاقة ضريبية", "بطاقه ضريبيه", "بطاقة ضريبيه", "تسجيل ضريبي", "رقم التسجيل", "ملف ضريبي", "مأمورية ضرائب", "مامورية ضرائب", "مأمورية", "مامورية", "ض.ق.م", "ضريبة مبيعات", "ضرائب", "الضرائب", "ب.ض", "ب.ض.", "ب ض",
        "vat no", "vat reg", "vat id", "tax id", "tax no", "tax #", "trn", "tin", "rfc", "cif", "nif", "steuernummer",
        // Phone & Contact
        "هاتف", "تليفون", "تلفون", "موبايل", "جوال", "فاكس", "واتساب", "whatsapp", "خدمة العملاء", "خدمه العملاء", "خدمة عملاء", "خدمه عملاء", "خط ساخن", "اتصل بنا", "تواصل معنا",
        "tel", "tel:", "phone", "phone:", "mob", "mobile", "mobile:", "hotline", "hotline:", "fax", "call", "call:",
        // Date & Time
        "تاريخ", "التاريخ", "بتاريخ", "وقت", "الوقت", "ساعة", "الساعة", "ساعه", "date", "date:", "time", "time:", "datetime",
        // Invoice / Order / Reference / Cashier / Branch / Table / Token / Boun
        "رقم الفاتورة", "فاتورة رقم", "فاتورة", "فاتوره", "الفاتورة", "الفاتوره", "رقم الإيصال", "ايصال رقم", "إيصال رقم", "إيصال", "ايصال", "الإيصال", "الايصال",
        "رقم الطلب", "طلب رقم", "طلب", "أوردر", "اوردر", "order", "order no", "order #",
        "رقم الشيك", "شيك رقم", "شيك", "check", "check no", "check #",
        "بون", "البون", "بون رقم", "رقم البون",
        "رقم العملية", "عملية رقم", "عملية", "رقم المرجع", "المرجع", "مرجع", "ref", "ref no", "reference",
        "كود الفاتورة", "كود", "رقم الكود", "كود:", "code", "code:",
        "مسلسل", "سيريال", "رقم المسلسل", "serial", "serial no", "seq", "sequence",
        "رقم الحساب", "حساب رقم", "حساب العميل", "كود العميل", "إذن صرف", "اذن صرف", "إذن استلام", "اذن استلام",
        "كاشير", "الكاشير", "كاشير:", "بائع", "مستخدم", "user", "clerk", "نقطة بيع", "نقطه بيع", "وردية", "ورديه", "فرع", "الفرع", "طاولة", "طاوله", "ترابيزة", "ترابيزه", "صالة", "صاله", "تيك اواي", "تيك أواي", "takeaway", "دليفري", "توصيل", "delivery",
        "invoice no", "inv no", "invoice #", "inv #", "receipt no", "receipt #", "bill no",
        "cashier", "terminal", "pos", "branch", "table", "shift", "server", "station", "account no",
        // Address & Location
        "street", "st.", "st", "road", "rd.", "rd", "avenue", "ave.", "ave", "building", "bldg", "floor", "suite", "po box", "p.o. box",
        "شارع", "طريق", "ميدان", "حي", "منطقة", "منطقه", "مدينة", "مدينه", "محافظة", "محافظه", "عمارة", "عماره", "مبنى", "دور", "شقة", "شقه", "مكتب", "ص.ب", "صندوق بريد", "العنوان", "عنوان",
        // Customer / Vendor
        "عميل", "العميل", "مورد", "المورد", "customer", "client", "guest",
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

    val DATE_REGEX = Regex("""(?<!\d)\d{1,4}\s*[/\-.]\s*(?:0?[1-9]|1[0-2])\s*[/\-.]\s*(?:\d{2,4}|\d{1,2})(?!\d)""")
    val TIME_REGEX = Regex("""(?<!\d)(?:0?[1-9]|1[0-2]|2[0-3])\s*:\s*[0-5]\d(?:\s*:\s*[0-5]\d)?(?:\s*(?:am|pm|ص|م))?(?!\d)""", RegexOption.IGNORE_CASE)
    val PHONE_REGEX = Regex("""(?<!\d)(?:01[0125][\s\-]?\d{3,4}[\s\-]?\d{4}|0[2345][\s\-]?\d{3,4}[\s\-]?\d{4}|\+?\d{1,3}[- ]?\d{7,10})(?!\d)""")
    val TAX_ID_REGEX = Regex("""(?<!\d)\d{3}[\s\-.]\d{3}[\s\-.]\d{3}(?!\d)""")
    val LONG_ID_REGEX = Regex("""(?<![\d.,])\d{7,16}(?![\d.,])""")

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
        if (DATE_REGEX.containsMatchIn(stripped) && (TIME_REGEX.containsMatchIn(stripped) || stripped.none { it.isLetter() })) return true
        if (PHONE_REGEX.containsMatchIn(stripped) && stripped.none { it.isLetter() }) return true
        if (LONG_ID_REGEX.containsMatchIn(stripped) && stripped.none { it.isLetter() }) return true
        if (TAX_ID_REGEX.containsMatchIn(stripped)) return true
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
                    // Arabic Tatweel / Kashida
                    'ـ' -> Unit
                    // Arabic Harakat (Fathatan, Dammatan, Kasratan, Fatha, Damma, Kasra, Shadda, Sukun)
                    in '\u064B'..'\u0652' -> Unit
                    // Arabic small vowels / Quranic marks
                    in '\u0653'..'\u0670' -> Unit
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

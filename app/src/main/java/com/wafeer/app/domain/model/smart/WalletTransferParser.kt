package com.wafeer.app.domain.model.smart

import java.math.BigDecimal
import java.util.regex.Pattern

data class ParsedWalletTransfer(
    val amount: BigDecimal,
    val isExpense: Boolean, // true = expense/transfer out, false = income/transfer in
    val party: String,     // Recipient or sender name/number
    val platform: String,  // InstaPay, Vodafone Cash, STC Pay, Bank, etc.
    val reference: String? = null,
    val rawText: String = "",
)

object WalletTransferParser {

    private val NUMBER_CLEANER = Regex("[^0-9.]")

    // InstaPay Arabic & English patterns
    private val INSTAPAY_EXPENSE_AR = Pattern.compile(
        """(?:تم\s+تحويل|خصم|دفع)\s+(?:مبلغ\s+)?([0-9,]+(?:\.[0-9]+)?)\s*(?:ج\.?م|جم|جنيه|EGP)?\s*(?:بنجاح\s+)?(?:إلى|الي|لـ|لصالح)\s+([^.\n]+?)(?:\s+(?:من\s+خلال|عبر|بواسطة|مرجع|رقم|بتاريخ|$))""",
        Pattern.CASE_INSENSITIVE
    )

    private val INSTAPAY_INCOME_AR = Pattern.compile(
        """(?:تم\s+استلام|إيداع|تحويل\s+لك|وصلك)\s+(?:مبلغ\s+)?([0-9,]+(?:\.[0-9]+)?)\s*(?:ج\.?م|جم|جنيه|EGP)?\s*(?:بنجاح\s+)?(?:من)\s+([^.\n]+?)(?:\s+(?:من\s+خلال|عبر|بواسطة|مرجع|رقم|بتاريخ|$))""",
        Pattern.CASE_INSENSITIVE
    )

    private val INSTAPAY_EXPENSE_EN = Pattern.compile(
        """(?:Successful\s+transfer\s+of|Transferred|Debited\s+with|Paid)\s+(?:EGP|LE|L\.E\.)?\s*([0-9,]+(?:\.[0-9]+)?)\s*(?:EGP|LE)?\s*(?:to)\s+([^.\n]+?)(?:\s+(?:via|ref|transaction|on|$))""",
        Pattern.CASE_INSENSITIVE
    )

    private val INSTAPAY_INCOME_EN = Pattern.compile(
        """(?:Received|Credited\s+with)\s+(?:EGP|LE|L\.E\.)?\s*([0-9,]+(?:\.[0-9]+)?)\s*(?:EGP|LE)?\s*(?:from)\s+([^.\n]+?)(?:\s+(?:via|ref|transaction|on|$))""",
        Pattern.CASE_INSENSITIVE
    )

    // Vodafone Cash & Egyptian Wallets (Orange, Etisalat, WE)
    private val VF_CASH_EXPENSE = Pattern.compile(
        """(?:تم\s+تحويل|تم\s+خصم|سحب|شراء|دفع)\s+([0-9,]+(?:\.[0-9]+)?)\s*(?:جنيه|ج\.?م)?\s*(?:لـ|لرقم|إلى|الي)\s*([0-9+]+|[^.\n]+)""",
        Pattern.CASE_INSENSITIVE
    )

    private val VF_CASH_INCOME = Pattern.compile(
        """(?:تم\s+استلام|تم\s+إيداع|استلمت)\s+([0-9,]+(?:\.[0-9]+)?)\s*(?:جنيه|ج\.?م)?\s*(?:من\s+رقم|من)\s*([0-9+]+|[^.\n]+)""",
        Pattern.CASE_INSENSITIVE
    )

    // STC Pay & Gulf Wallets
    private val GULF_WALLET_EXPENSE = Pattern.compile(
        """(?:تم\s+تحويل|شراء\s+بقيمة|خصم)\s+([0-9,]+(?:\.[0-9]+)?)\s*(?:ر\.?س|ريال|SAR|AED|KWD)?\s*(?:إلى|الي|لدى|من)\s+([^.\n]+)""",
        Pattern.CASE_INSENSITIVE
    )

    // Reference number pattern
    private val REF_PATTERN = Pattern.compile(
        """(?:مرجع|المرجع|رقم\s+العملية|Ref(?:erence)?|Txn(?:\s+ID)?)[ :#-]+([A-Za-z0-9]+)""",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(text: String): ParsedWalletTransfer? {
        if (text.isBlank()) return null
        val clean = text.replace("\r", " ").replace("\n", " ").trim()

        val ref = extractReference(clean)
        val platform = detectPlatform(clean)

        // 1. Try InstaPay / Arabic Wallet Expense
        val mInstaExpenseAr = INSTAPAY_EXPENSE_AR.matcher(clean)
        if (mInstaExpenseAr.find()) {
            val amount = parseAmount(mInstaExpenseAr.group(1)) ?: return null
            val party = cleanPartyName(mInstaExpenseAr.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = true,
                party = party.ifBlank { "تحويل خارج" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 2. Try InstaPay / Arabic Wallet Income
        val mInstaIncomeAr = INSTAPAY_INCOME_AR.matcher(clean)
        if (mInstaIncomeAr.find()) {
            val amount = parseAmount(mInstaIncomeAr.group(1)) ?: return null
            val party = cleanPartyName(mInstaIncomeAr.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = false,
                party = party.ifBlank { "تحويل وارد" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 3. Try English Expense
        val mInstaExpenseEn = INSTAPAY_EXPENSE_EN.matcher(clean)
        if (mInstaExpenseEn.find()) {
            val amount = parseAmount(mInstaExpenseEn.group(1)) ?: return null
            val party = cleanPartyName(mInstaExpenseEn.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = true,
                party = party.ifBlank { "Transfer Out" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 4. Try English Income
        val mInstaIncomeEn = INSTAPAY_INCOME_EN.matcher(clean)
        if (mInstaIncomeEn.find()) {
            val amount = parseAmount(mInstaIncomeEn.group(1)) ?: return null
            val party = cleanPartyName(mInstaIncomeEn.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = false,
                party = party.ifBlank { "Transfer In" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 5. Try Vodafone / Telco Cash Expense
        val mVfExpense = VF_CASH_EXPENSE.matcher(clean)
        if (mVfExpense.find()) {
            val amount = parseAmount(mVfExpense.group(1)) ?: return null
            val party = cleanPartyName(mVfExpense.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = true,
                party = party.ifBlank { "فودافون كاش" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 6. Try Vodafone / Telco Cash Income
        val mVfIncome = VF_CASH_INCOME.matcher(clean)
        if (mVfIncome.find()) {
            val amount = parseAmount(mVfIncome.group(1)) ?: return null
            val party = cleanPartyName(mVfIncome.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = false,
                party = party.ifBlank { "إيداع محفظة" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        // 7. Try Gulf Wallet (STC Pay, UrPay, etc.)
        val mGulf = GULF_WALLET_EXPENSE.matcher(clean)
        if (mGulf.find()) {
            val amount = parseAmount(mGulf.group(1)) ?: return null
            val party = cleanPartyName(mGulf.group(2))
            return ParsedWalletTransfer(
                amount = amount,
                isExpense = true,
                party = party.ifBlank { "دفع إلكتروني" },
                platform = platform,
                reference = ref,
                rawText = text,
            )
        }

        return null
    }

    private fun detectPlatform(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("instapay") || lower.contains("إنستاباي") || lower.contains("انستاباي") -> "InstaPay"
            lower.contains("vodafone") || lower.contains("فودافون") -> "Vodafone Cash"
            lower.contains("orange") || lower.contains("أورنج") || lower.contains("اورنج") -> "Orange Cash"
            lower.contains("etisalat") || lower.contains("اتصالات") -> "Etisalat Cash"
            lower.contains("we pay") || lower.contains("وي باي") -> "WE Pay"
            lower.contains("stc") || lower.contains("اس تي سي") -> "STC Pay"
            lower.contains("urpay") || lower.contains("يورباي") -> "UrPay"
            lower.contains("fawry") || lower.contains("فوري") -> "Fawry"
            lower.contains("tabby") || lower.contains("تابي") -> "Tabby"
            lower.contains("tamara") || lower.contains("تمارا") -> "Tamara"
            lower.contains("valu") || lower.contains("فاليو") -> "ValU"
            lower.contains("aman") || lower.contains("أمان") || lower.contains("امان") -> "Aman"
            else -> "محفظة / بنك"
        }
    }

    private fun extractReference(text: String): String? {
        val m = REF_PATTERN.matcher(text)
        return if (m.find()) m.group(1) else null
    }

    private fun parseAmount(raw: String?): BigDecimal? {
        if (raw == null) return null
        val normalized = raw.replace(",", "").trim()
        return runCatching { BigDecimal(normalized) }.getOrNull()
    }

    private fun cleanPartyName(raw: String?): String {
        if (raw == null) return ""
        return raw.trim()
            .replace(Regex("""\s+بنجاح.*$"""), "")
            .replace(Regex("""\s+(?:من خلال|عبر|بواسطة|مرجع|ref).*$""", RegexOption.IGNORE_CASE), "")
            .trim()
    }
}

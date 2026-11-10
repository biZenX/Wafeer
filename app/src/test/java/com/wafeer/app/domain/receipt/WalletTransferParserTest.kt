package com.wafeer.app.domain.model.smart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class WalletTransferParserTest {

    @Test
    fun parseInstaPayExpenseArabic() {
        val text = "تم تحويل مبلغ 1,500.00 ج.م بنجاح إلى أحمد محمد من خلال إنستاباي. رقم المرجع: TXN12345"
        val result = WalletTransferParser.parse(text)
        assertNotNull(result)
        assertEquals(BigDecimal("1500.00"), result!!.amount)
        assertTrue(result.isExpense)
        assertTrue(result.party.contains("أحمد محمد"))
        assertEquals("InstaPay", result.platform)
        assertEquals("TXN12345", result.reference)
    }

    @Test
    fun parseInstaPayIncomeArabic() {
        val text = "تم استلام مبلغ 350.50 جم من سارة إبراهيم عبر إنستاباي مرجع: REF9988"
        val result = WalletTransferParser.parse(text)
        assertNotNull(result)
        assertEquals(BigDecimal("350.50"), result!!.amount)
        assertFalse(result.isExpense)
        assertTrue(result.party.contains("سارة إبراهيم"))
        assertEquals("InstaPay", result.platform)
    }

    @Test
    fun parseInstaPayExpenseEnglish() {
        val text = "Successful transfer of EGP 450.00 to Mahmoud Ali via InstaPay. Ref: IP776655"
        val result = WalletTransferParser.parse(text)
        assertNotNull(result)
        assertEquals(BigDecimal("450.00"), result!!.amount)
        assertTrue(result.isExpense)
        assertTrue(result.party.contains("Mahmoud Ali"))
        assertEquals("InstaPay", result.platform)
    }

    @Test
    fun parseVodafoneCashExpense() {
        val text = "تم تحويل 200.00 جنيه لرقم 01012345678 بنجاح من فودافون كاش. مصاريف الخدمة 1 جنيه"
        val result = WalletTransferParser.parse(text)
        assertNotNull(result)
        assertEquals(BigDecimal("200.00"), result!!.amount)
        assertTrue(result.isExpense)
        assertEquals("Vodafone Cash", result.platform)
    }

    @Test
    fun parseVodafoneCashIncome() {
        val text = "تم استلام 1000 جنيه من رقم 01099887766 فودافون كاش"
        val result = WalletTransferParser.parse(text)
        assertNotNull(result)
        assertEquals(BigDecimal("1000"), result!!.amount)
        assertFalse(result.isExpense)
        assertEquals("Vodafone Cash", result.platform)
    }
}

package com.wafeer.app.presentation.ui.editor.dialogs

import com.google.common.truth.Truth.assertThat
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.BudgetSplitMode
import com.wafeer.app.domain.model.BudgetState
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class PlannedPreviewTest {

    @Test
    fun `plannedPreview calculates deduction for current period`() {
        val settings = BudgetSettings(
            period = BudgetPeriod.MONTHLY,
            splitMode = BudgetSplitMode.STATIC,
            periodStartDate = LocalDate.of(2026, 11, 1),
            totalBudget = BigDecimal("1000.00")
        )
        val state = BudgetState(
            totalBudget = BigDecimal("1000.00"),
            totalSpentInPeriod = BigDecimal("200.00"),
            daysRemaining = 10,
            dailyBudget = BigDecimal("80.00")
        )
        val amount = BigDecimal("100.00")
        val date = LocalDate.of(2026, 11, 20)

        val preview = plannedPreview(settings, state, amount, date)

        assertThat(preview).isInstanceOf(PlannedPreview.LeftInPeriod::class.java)
        val left = preview as PlannedPreview.LeftInPeriod
        assertThat(left.before).isEqualTo(BigDecimal("800.00"))
        assertThat(left.after).isEqualTo(BigDecimal("700.00"))
        assertThat(left.isNegative).isFalse()
    }
}

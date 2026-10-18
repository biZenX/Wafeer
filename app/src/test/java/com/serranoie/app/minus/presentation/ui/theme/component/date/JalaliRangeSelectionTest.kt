package com.serranoie.app.minus.presentation.ui.theme.component.date

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class JalaliRangeSelectionTest {

    private val mehr10 = LocalDate.of(2026, 10, 2)
    private val mehr20 = LocalDate.of(2026, 10, 12)
    private val aban5 = LocalDate.of(2026, 10, 27)

    @Test
    fun `first tap sets the start only`() {
        assertThat(nextRangeSelection(null, null, mehr10)).isEqualTo(mehr10 to null)
    }

    @Test
    fun `tap on or after the start closes the range`() {
        assertThat(nextRangeSelection(mehr10, null, aban5)).isEqualTo(mehr10 to aban5)
        assertThat(nextRangeSelection(mehr10, null, mehr10)).isEqualTo(mehr10 to mehr10)
    }

    @Test
    fun `tap before the start restarts from that day`() {
        assertThat(nextRangeSelection(mehr20, null, mehr10)).isEqualTo(mehr10 to null)
    }

    @Test
    fun `tap after a full range starts a new range`() {
        assertThat(nextRangeSelection(mehr10, mehr20, aban5)).isEqualTo(aban5 to null)
    }
}

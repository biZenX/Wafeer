package com.serranoie.app.minus.presentation.ui.theme

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.serranoie.app.minus.domain.model.ContrastMode
import com.serranoie.app.minus.domain.model.UserSettings
import com.serranoie.app.minus.presentation.appContrast
import io.mockk.mockk
import org.junit.After
import org.junit.Test

class ThemeManagerTest {

    private val context: Context = mockk(relaxed = true)

    @After
    fun tearDown() {
        context.appContrast = ContrastMode.NORMAL
    }

    @Test
    fun `applyUserSettings restores the saved contrast mode`() {
        context.appContrast = ContrastMode.NORMAL

        ThemeManager().applyUserSettings(context, UserSettings(contrastMode = ContrastMode.HIGH))

        assertThat(context.appContrast).isEqualTo(ContrastMode.HIGH)
    }
}

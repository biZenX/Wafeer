package com.wafeer.app.presentation.ui.smart

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.wafeer.app.data.repository.SmartToolsRepository
import com.wafeer.app.domain.model.smart.GameyaItem
import com.wafeer.app.domain.model.smart.InstallmentItem
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class SmartToolsViewModelTest {

    private val repository: SmartToolsRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val goalsFlow = MutableStateFlow<List<MicroGoal>>(emptyList())
    private val wishlistFlow = MutableStateFlow<List<WishlistItem>>(emptyList())
    private val savingsFlow = MutableStateFlow(BigDecimal.ZERO)
    private val funRatioFlow = MutableStateFlow(15f)
    private val installmentsFlow = MutableStateFlow<List<InstallmentItem>>(emptyList())
    private val gameyasFlow = MutableStateFlow<List<GameyaItem>>(emptyList())

    private lateinit var viewModel: SmartToolsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { repository.observeGoals() } returns goalsFlow
        every { repository.observeWishlist() } returns wishlistFlow
        every { repository.observeTotalWishlistSavings() } returns savingsFlow
        every { repository.observeFunFundRatio() } returns funRatioFlow
        every { repository.observeInstallments() } returns installmentsFlow
        every { repository.observeGameyas() } returns gameyasFlow

        viewModel = SmartToolsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state defaults to calculator tab and zero commitments`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedTab).isEqualTo(SmartToolTab.CALCULATOR)
            assertThat(state.totalMonthlyInstallments).isEqualTo(BigDecimal.ZERO)
            assertThat(state.totalMonthlyGameyaShares).isEqualTo(BigDecimal.ZERO)
            assertThat(state.totalMonthlyCommitments).isEqualTo(BigDecimal.ZERO)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectTab updates selected tab to COMMITMENTS`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.selectedTab).isEqualTo(SmartToolTab.CALCULATOR)

            viewModel.selectTab(SmartToolTab.COMMITMENTS)

            val updated = awaitItem()
            assertThat(updated.selectedTab).isEqualTo(SmartToolTab.COMMITMENTS)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `total monthly commitments calculates from active installments and gameyas`() = runTest {
        val activeInstallment1 = InstallmentItem(
            id = "inst-1",
            title = "Laptop",
            totalAmount = BigDecimal("12000"),
            monthlyAmount = BigDecimal("1000"),
            totalMonths = 12,
            paidMonths = 3,
        )
        val activeInstallment2 = InstallmentItem(
            id = "inst-2",
            title = "Phone",
            totalAmount = BigDecimal("6000"),
            monthlyAmount = BigDecimal("500"),
            totalMonths = 12,
            paidMonths = 6,
        )
        val completedInstallment = InstallmentItem(
            id = "inst-3",
            title = "Watch",
            totalAmount = BigDecimal("3000"),
            monthlyAmount = BigDecimal("500"),
            totalMonths = 6,
            paidMonths = 6,
        )

        val activeGameya = GameyaItem(
            id = "gam-1",
            name = "Work Circle",
            monthlyShare = BigDecimal("2000"),
            totalMonths = 10,
            payoutMonth = 5,
            currentMonth = 2,
            isCollected = false,
        )
        val completedGameya = GameyaItem(
            id = "gam-2",
            name = "Family Circle",
            monthlyShare = BigDecimal("1000"),
            totalMonths = 5,
            payoutMonth = 3,
            currentMonth = 5,
            isCollected = true,
        )

        installmentsFlow.value = listOf(activeInstallment1, activeInstallment2, completedInstallment)
        gameyasFlow.value = listOf(activeGameya, completedGameya)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.totalMonthlyInstallments.compareTo(BigDecimal("1500"))).isEqualTo(0)
            assertThat(state.totalMonthlyGameyaShares.compareTo(BigDecimal("2000"))).isEqualTo(0)
            assertThat(state.totalMonthlyCommitments.compareTo(BigDecimal("3500"))).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completed commitments do not deduct from monthly budget`() = runTest {
        val completedInstallment = InstallmentItem(
            id = "inst-1",
            title = "Paid Off TV",
            totalAmount = BigDecimal("5000"),
            monthlyAmount = BigDecimal("1000"),
            totalMonths = 5,
            paidMonths = 5,
        )
        val completedGameya = GameyaItem(
            id = "gam-1",
            name = "Old Circle",
            monthlyShare = BigDecimal("1500"),
            totalMonths = 5,
            payoutMonth = 2,
            currentMonth = 5,
            isCollected = true,
        )

        installmentsFlow.value = listOf(completedInstallment)
        gameyasFlow.value = listOf(completedGameya)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.totalMonthlyInstallments.compareTo(BigDecimal.ZERO)).isEqualTo(0)
            assertThat(state.totalMonthlyGameyaShares.compareTo(BigDecimal.ZERO)).isEqualTo(0)
            assertThat(state.totalMonthlyCommitments.compareTo(BigDecimal.ZERO)).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectTab navigates across all supported smart tabs`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().selectedTab).isEqualTo(SmartToolTab.CALCULATOR)

            val tabs = listOf(
                SmartToolTab.COMMITMENTS,
                SmartToolTab.WALLET_PARSER,
                SmartToolTab.MICRO_GOALS,
                SmartToolTab.WISHLIST_48H,
                SmartToolTab.FUN_FUND,
                SmartToolTab.CALCULATOR,
            )

            for (tab in tabs) {
                viewModel.selectTab(tab)
                assertThat(awaitItem().selectedTab).isEqualTo(tab)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deficit scenario when monthly commitments exceed remaining budget`() = runTest {
        val largeInstallment = InstallmentItem(
            id = "inst-car",
            title = "Car Loan",
            totalAmount = BigDecimal("120000"),
            monthlyAmount = BigDecimal("5000"),
            totalMonths = 24,
            paidMonths = 2,
        )
        val monthlyGameya = GameyaItem(
            id = "gam-gold",
            name = "Big Circle",
            monthlyShare = BigDecimal("3000"),
            totalMonths = 10,
            payoutMonth = 5,
            currentMonth = 1,
            isCollected = false,
        )

        installmentsFlow.value = listOf(largeInstallment)
        gameyasFlow.value = listOf(monthlyGameya)

        viewModel.uiState.test {
            val state = awaitItem()
            val totalCommitments = state.totalMonthlyCommitments
            assertThat(totalCommitments.compareTo(BigDecimal("8000"))).isEqualTo(0)

            val monthlyBudget = BigDecimal("6000")
            val netRemaining = monthlyBudget - totalCommitments
            assertThat(netRemaining.compareTo(BigDecimal("-2000"))).isEqualTo(0)
            assertThat(netRemaining < BigDecimal.ZERO).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }
}

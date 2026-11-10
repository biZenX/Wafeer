package com.wafeer.app.presentation.ui.smart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafeer.app.data.repository.SmartToolsRepository
import com.wafeer.app.domain.model.smart.AffordabilityCalculation
import com.wafeer.app.domain.model.smart.GameyaItem
import com.wafeer.app.domain.model.smart.GoldPortfolio
import com.wafeer.app.domain.model.smart.InstallmentItem
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.ParsedWalletTransfer
import com.wafeer.app.domain.model.smart.WalletTransferParser
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.ZakatCalculator
import com.wafeer.app.domain.model.smart.ZakatResult
import com.wafeer.app.domain.model.smart.ZakatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

enum class SmartToolTab {
    CALCULATOR,
    INSTALLMENTS,
    GAMEYA,
    GOLD_ZAKAT,
    WALLET_PARSER,
    MICRO_GOALS,
    WISHLIST_48H,
    FUN_FUND,
}

data class SmartToolsUiState(
    val selectedTab: SmartToolTab = SmartToolTab.CALCULATOR,
    val goals: List<MicroGoal> = emptyList(),
    val wishlist: List<WishlistItem> = emptyList(),
    val installments: List<InstallmentItem> = emptyList(),
    val gameyas: List<GameyaItem> = emptyList(),
    val goldPortfolio: GoldPortfolio = GoldPortfolio(),
    val zakatSettings: ZakatState = ZakatState(),
    val totalWishlistSavings: BigDecimal = BigDecimal.ZERO,
    val funFundRatio: Float = 15f,
    val calculationInputPrice: String = "",
    val calculationResult: AffordabilityCalculation? = null,
    val walletInputText: String = "",
    val parsedTransfer: ParsedWalletTransfer? = null,
) {
    val totalMonthlyInstallments: BigDecimal
        get() = installments.filter { !it.isCompleted }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.monthlyAmount }

    val totalMonthlyGameyaShares: BigDecimal
        get() = gameyas.filter { !it.isCompleted }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.monthlyShare }

    fun calculateZakat(liquidCash: BigDecimal): ZakatResult {
        return ZakatCalculator.calculate(
            liquidCash = liquidCash,
            goldHoldingsValue = goldPortfolio.currentTotalMarketValue,
            goldPrice21k = zakatSettings.goldPriceForNisab.takeIf { it > BigDecimal.ZERO }
                ?: goldPortfolio.currentPricePerGram21k,
            customNisab = zakatSettings.customNisabAmount,
            isHawlCompleted = zakatSettings.isHawlCompleted,
        )
    }
}

@HiltViewModel
class SmartToolsViewModel @Inject constructor(
    private val repository: SmartToolsRepository,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(SmartToolTab.CALCULATOR)
    private val _calculationInputPrice = MutableStateFlow("")
    private val _calculationResult = MutableStateFlow<AffordabilityCalculation?>(null)
    private val _walletInputText = MutableStateFlow("")
    private val _parsedTransfer = MutableStateFlow<ParsedWalletTransfer?>(null)

    private data class StreamBatchA(
        val goals: List<MicroGoal>,
        val wishlist: List<WishlistItem>,
        val savings: BigDecimal,
        val funRatio: Float,
    )

    private data class StreamBatchB(
        val installments: List<InstallmentItem>,
        val gameyas: List<GameyaItem>,
        val gold: GoldPortfolio,
        val zakat: ZakatState,
    )

    private val batchAFlow = combine(
        repository.observeGoals(),
        repository.observeWishlist(),
        repository.observeTotalWishlistSavings(),
        repository.observeFunFundRatio(),
    ) { goals, wishlist, savings, funRatio ->
        StreamBatchA(goals, wishlist, savings, funRatio)
    }

    private val batchBFlow = combine(
        repository.observeInstallments(),
        repository.observeGameyas(),
        repository.observeGoldPortfolio(),
        repository.observeZakatSettings(),
    ) { installments, gameyas, gold, zakat ->
        StreamBatchB(installments, gameyas, gold, zakat)
    }

    val uiState: StateFlow<SmartToolsUiState> = combine(
        combine(_selectedTab, _calculationInputPrice, _calculationResult, ::Triple),
        combine(_walletInputText, _parsedTransfer, ::Pair),
        batchAFlow,
        batchBFlow,
    ) { (tab, priceInput, calcResult), (walletText, parsed), batchA, batchB ->
        SmartToolsUiState(
            selectedTab = tab,
            goals = batchA.goals,
            wishlist = batchA.wishlist,
            installments = batchB.installments,
            gameyas = batchB.gameyas,
            goldPortfolio = batchB.gold,
            zakatSettings = batchB.zakat,
            totalWishlistSavings = batchA.savings,
            funFundRatio = batchA.funRatio,
            calculationInputPrice = priceInput,
            calculationResult = calcResult,
            walletInputText = walletText,
            parsedTransfer = parsed,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SmartToolsUiState(),
    )

    fun selectTab(tab: SmartToolTab) {
        _selectedTab.value = tab
    }

    fun onPriceInputChanged(
        priceInput: String,
        currentDailyAllowance: BigDecimal,
        remainingBudget: BigDecimal,
        remainingDays: Int,
        currencyCode: String = "USD",
    ) {
        _calculationInputPrice.value = priceInput
        val price = priceInput.toBigDecimalOrNull()
        if (price != null && price > BigDecimal.ZERO) {
            _calculationResult.value = repository.calculateAffordability(
                price = price,
                currentDailyAllowance = currentDailyAllowance,
                remainingBudget = remainingBudget,
                remainingDays = remainingDays,
                currencyCode = currencyCode,
            )
        } else {
            _calculationResult.value = null
        }
    }

    // --- Micro Goals ---
    fun addMicroGoal(title: String, targetAmount: BigDecimal) {
        if (title.isBlank() || targetAmount <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.addGoal(title, targetAmount)
        }
    }

    fun depositToGoal(goal: MicroGoal, amount: BigDecimal) {
        if (amount <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.depositToGoal(goal, amount)
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    // --- Wishlist ---
    fun addWishlistItem(title: String, price: BigDecimal, coolingHours: Int = 48) {
        if (title.isBlank() || price <= BigDecimal.ZERO) return
        viewModelScope.launch {
            repository.addToWishlist(title, price, coolingHours)
        }
    }

    fun markWishlistSaved(item: WishlistItem) {
        viewModelScope.launch {
            repository.markWishlistSaved(item)
        }
    }

    fun markWishlistPurchased(item: WishlistItem) {
        viewModelScope.launch {
            repository.markWishlistPurchased(item)
        }
    }

    fun deleteWishlistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteWishlistItem(id)
        }
    }

    fun setFunFundRatio(ratio: Float) {
        viewModelScope.launch {
            repository.setFunFundRatio(ratio)
        }
    }

    // --- Installments ---
    fun saveInstallment(item: InstallmentItem) {
        viewModelScope.launch {
            repository.saveInstallment(item)
        }
    }

    fun payInstallmentMonth(id: String) {
        viewModelScope.launch {
            repository.payInstallmentMonth(id)
        }
    }

    fun deleteInstallment(id: String) {
        viewModelScope.launch {
            repository.deleteInstallment(id)
        }
    }

    // --- Gam'eya ---
    fun saveGameya(item: GameyaItem) {
        viewModelScope.launch {
            repository.saveGameya(item)
        }
    }

    fun advanceGameyaMonth(id: String) {
        viewModelScope.launch {
            repository.advanceGameyaMonth(id)
        }
    }

    fun toggleGameyaCollected(id: String) {
        viewModelScope.launch {
            repository.toggleGameyaCollected(id)
        }
    }

    fun deleteGameya(id: String) {
        viewModelScope.launch {
            repository.deleteGameya(id)
        }
    }

    // --- Gold Portfolio ---
    fun saveGoldPortfolio(portfolio: GoldPortfolio) {
        viewModelScope.launch {
            repository.saveGoldPortfolio(portfolio)
        }
    }

    // --- Zakat ---
    fun saveZakatSettings(state: ZakatState) {
        viewModelScope.launch {
            repository.saveZakatSettings(state)
        }
    }

    // --- Wallet / InstaPay Parser ---
    fun onWalletInputChanged(text: String) {
        _walletInputText.value = text
        _parsedTransfer.value = WalletTransferParser.parse(text)
    }

    fun clearWalletInput() {
        _walletInputText.value = ""
        _parsedTransfer.value = null
    }
}

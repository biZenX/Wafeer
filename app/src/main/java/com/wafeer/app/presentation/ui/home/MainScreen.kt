package com.wafeer.app.presentation.ui.home

import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafeer.app.R
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.presentation.ui.budget.BudgetViewModel
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetNumpadIntent
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetTransactionIntent
import com.wafeer.app.presentation.ui.changelog.ChangelogGate
import com.wafeer.app.presentation.ui.theme.component.budget.formula.BudgetFormulaHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.wafeer.app.presentation.LocalWindowInsets
import com.wafeer.app.presentation.ui.tutorial.SensorDiagnosticDialog
import com.wafeer.app.presentation.ui.tutorial.TutorialBox
import com.wafeer.app.presentation.ui.tutorial.TutorialTooltip
import com.wafeer.app.presentation.ui.tutorial.rememberTutorialBoxState
import logcat.logcat

private const val TAG = "MainScreen"

@Composable
fun MainScreen(
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    openWalletOnStart: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    smartToolsViewModel: com.wafeer.app.presentation.ui.smart.SmartToolsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val mainScreenState by mainScreenViewModel.uiState.collectAsStateWithLifecycle()
    val budgetUiState by budgetViewModel.uiState.collectAsStateWithLifecycle()
    val censorDiagnosticState by mainScreenViewModel.censorDiagnosticState.collectAsStateWithLifecycle()
    val smartToolsState by smartToolsViewModel.uiState.collectAsStateWithLifecycle()
    var showSmartToolsBottomSheet by remember { mutableStateOf(false) }
    var isHistoryVisible by remember { mutableStateOf(false) }
    var isCategoryGridVisible by remember { mutableStateOf(false) }

    val receiptScanner = com.wafeer.app.presentation.ui.editor.receipt.rememberReceiptScanner(
        onScanned = { scan ->
            budgetViewModel.processIntent(
                com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetEditorIntent.ReceiptScanned(
                    amount = scan.total.toPlainString(),
                    merchant = scan.merchant,
                ),
            )
        },
        onFailed = {
            android.widget.Toast.makeText(context, R.string.receipt_scan_failed, android.widget.Toast.LENGTH_SHORT).show()
        },
    )

    val tutorialStage = mainScreenState.tutorialStage
    val tutorialBoxCompleted = mainScreenState.tutorialBoxCompleted

    val effectiveSelectedPeriod =
        mainScreenState.selectedViewPeriod ?: budgetUiState.budgetSettings?.period
        ?: BudgetPeriod.DAILY

    logcat(TAG) { "MainScreen composed (openWalletOnStart=$openWalletOnStart, effectivePeriod=$effectiveSelectedPeriod)" }

    LaunchedEffect(Unit) {
        logcat(TAG) { "Triggering onRequestNotificationPermission from MainScreen LaunchedEffect" }
        onRequestNotificationPermission()
    }

    LaunchedEffect(openWalletOnStart, mainScreenState.walletSheetOpened) {
        if (openWalletOnStart && !mainScreenState.walletSheetOpened) {
            mainScreenViewModel.processIntent(
                MainScreenUiIntent.ShowBudgetPeriodSheet(forceSetup = true),
                tutorialStage,
            )
            mainScreenViewModel.processIntent(
                MainScreenUiIntent.MarkWalletSheetOpened,
                tutorialStage,
            )
        }
    }

    LaunchedEffect(Unit) {
        mainScreenViewModel.effects.collect { effect ->
            when (effect) {
                is MainScreenUiEffect.RequestUndo -> {
                    budgetViewModel.processIntent(
                        BudgetTransactionIntent.RestoreTransactionTapped(effect.transaction),
                    )
                }

                is MainScreenUiEffect.UpdateDragProgress -> {
                    budgetViewModel.processIntent(
                        BudgetNumpadIntent.SetDragProgress(effect.progress),
                    )
                }

                is MainScreenUiEffect.OpenAnalytics -> {
                    onNavigateToAnalytics()
                }

                is MainScreenUiEffect.ShowUndoSnackbar -> {}
            }
        }
    }

    val hasNoBudget = budgetUiState.budgetSettings == null || budgetUiState.budgetSettings?.endDate == null
    val tutorialWalkOrder = remember(hasNoBudget) {
        if (hasNoBudget) {
            listOf(1, 0, 2, 5)
        } else {
            listOf(0, 1, 2, 5)
        }
    }
    val showSpotlightTour = !mainScreenState.spotlightTourV2Completed &&
        !mainScreenState.showBudgetPeriodSheet &&
        !isHistoryVisible &&
        !isCategoryGridVisible
    val tutorialBoxState = rememberTutorialBoxState(order = tutorialWalkOrder)

    LaunchedEffect(mainScreenState.spotlightTourV2Completed) {
        if (!mainScreenState.spotlightTourV2Completed && tutorialBoxState.isCompleted) {
            tutorialBoxState.resetForReplay()
        }
    }

    ChangelogGate(
        currentVersionCode = run {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                0,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION") info.versionCode
            }
        },
    ) {
        TutorialBox(
            showTutorial = showSpotlightTour,
            onFreePractice = null,
            onTutorialCompleted = {
                logcat(TAG) { "TutorialBox completed → persisting spotlightTourV2Completed=true" }
                mainScreenViewModel.processIntent(
                    MainScreenUiIntent.SetSpotlightTourCompleted(true),
                    tutorialStage,
                )
            },
            onTutorialReopened = {
                logcat(TAG) { "TutorialBox reopened → persisting spotlightTourV2Completed=false" }
                mainScreenViewModel.processIntent(
                    MainScreenUiIntent.SetSpotlightTourCompleted(false),
                    tutorialStage,
                )
            },
            onCutoutClick = { index ->
                if (index == 1) {
                    mainScreenViewModel.processIntent(
                        MainScreenUiIntent.ShowBudgetPeriodSheet(forceSetup = hasNoBudget),
                        tutorialStage,
                    )
                }
                tutorialBoxState.advance()
            },
            state = tutorialBoxState,
            tutorialTarget = { index ->
                val practiceBadge = stringResource(R.string.tutorial_practice_mode)
                when (index) {
                    0 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_numpad_title),
                        description = stringResource(R.string.tutorial_numpad_description),
                        icon = Icons.Rounded.Dialpad,
                        badgeText = practiceBadge,
                    )
                    1 -> TutorialTooltip(
                        title = if (hasNoBudget) {
                            stringResource(R.string.tutorial_budget_pill_setup_title)
                        } else {
                            stringResource(R.string.tutorial_budget_pill_title)
                        },
                        description = if (hasNoBudget) {
                            stringResource(R.string.tutorial_budget_pill_setup_description)
                        } else {
                            stringResource(R.string.tutorial_budget_pill_description)
                        },
                        icon = Icons.Rounded.AccountBalanceWallet,
                        badgeText = practiceBadge,
                    )
                    2 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_settings_title),
                        description = stringResource(R.string.tutorial_settings_description),
                        icon = Icons.Rounded.Tune,
                        badgeText = practiceBadge,
                    )
                    5 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_analytics_title),
                        description = stringResource(R.string.tutorial_analytics_description),
                        icon = Icons.Rounded.BarChart,
                        badgeText = practiceBadge,
                    )
                    else -> Text(text = "")
                }
            },
        ) {
            BudgetFormulaHost {
                Box(Modifier.fillMaxSize()) {
                    MainScreenContent(
                        mainScreenState = mainScreenState,
                        budgetUiState = budgetUiState,
                        actions =
                            MainScreenActions(
                                onProcessIntent = { intent ->
                                    when (intent) {
                                        is MainScreenUiIntent.ProcessBudgetTransactionIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        is MainScreenUiIntent.ProcessBudgetEditorIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        is MainScreenUiIntent.ProcessBudgetNumpadIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        else -> {
                                            mainScreenViewModel.processIntent(intent, tutorialStage)
                                        }
                                    }
                                },
                                onAdvanceTutorial = { expected ->
                                    mainScreenViewModel.processIntent(
                                        MainScreenUiIntent.AdvanceTutorial(expected),
                                        tutorialStage
                                    )
                                },
                                onNavigateToAnalytics = onNavigateToAnalytics,
                                onNavigateToSettings = onNavigateToSettings,
                                onOpenSmartTools = { showSmartToolsBottomSheet = true },
                                onUnresolvedSurplusBannerClick = budgetViewModel::onUnresolvedSurplusBannerClicked,
                                onLeftoverChoice = budgetViewModel::onLeftoverChoice,
                                onCreateCategory = budgetViewModel::createCategory,
                                onHistoryVisibilityChanged = { isHistoryVisible = it },
                                onCategoryGridVisibilityChanged = { isCategoryGridVisible = it },
                                receiptScanner = receiptScanner,
                                onPeriodSelected = { period ->
                                    mainScreenViewModel.processIntent(
                                        MainScreenUiIntent.SetSelectedPeriod(period), tutorialStage
                                    )
                                },
                            ),
                        openWalletOnStart = openWalletOnStart,
                        tutorialBoxState = tutorialBoxState,
                    )

                }
            }
        }

        if (showSmartToolsBottomSheet) {
            val dailyAllowance = budgetUiState.budgetState?.dailyBudget ?: java.math.BigDecimal.ZERO
            val totalBudget = budgetUiState.budgetState?.totalBudget ?: java.math.BigDecimal.ZERO
            val totalSpent = budgetUiState.budgetState?.totalSpentInPeriod ?: java.math.BigDecimal.ZERO
            val remainingBudget = (totalBudget - totalSpent).coerceAtLeast(java.math.BigDecimal.ZERO)
            val remainingDays = budgetUiState.budgetState?.daysRemaining ?: 1
            val currencyCode = budgetUiState.budgetSettings?.currencyCode ?: "USD"

            com.wafeer.app.presentation.ui.smart.SmartToolsBottomSheet(
                uiState = smartToolsState,
                currentDailyAllowance = dailyAllowance,
                remainingBudget = remainingBudget,
                remainingDays = remainingDays,
                currencyCode = currencyCode,
                onTabSelected = smartToolsViewModel::selectTab,
                onPriceInputChanged = { price ->
                    smartToolsViewModel.onPriceInputChanged(
                        priceInput = price,
                        currentDailyAllowance = dailyAllowance,
                        remainingBudget = remainingBudget,
                        remainingDays = remainingDays,
                        currencyCode = currencyCode,
                    )
                },
                onAddGoal = smartToolsViewModel::addMicroGoal,
                onDepositToGoal = smartToolsViewModel::depositToGoal,
                onDeleteGoal = smartToolsViewModel::deleteGoal,
                onAddWishlistItem = smartToolsViewModel::addWishlistItem,
                onMarkWishlistSaved = smartToolsViewModel::markWishlistSaved,
                onMarkWishlistPurchased = smartToolsViewModel::markWishlistPurchased,
                onDeleteWishlistItem = smartToolsViewModel::deleteWishlistItem,
                onSetFunFundRatio = smartToolsViewModel::setFunFundRatio,
                onScanReceipt = {
                    showSmartToolsBottomSheet = false
                    receiptScanner.launch()
                },
                onDismiss = { showSmartToolsBottomSheet = false },
            )
        }
    }
}

package com.wafeer.app.presentation.ui.smart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafeer.app.R
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.GameyaItem
import com.wafeer.app.domain.model.smart.GoldPortfolio
import com.wafeer.app.domain.model.smart.InstallmentItem
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import com.wafeer.app.domain.model.smart.ZakatState
import com.wafeer.app.presentation.util.haptic.HapticUtil.performUIHaptic
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartToolsBottomSheet(
    uiState: SmartToolsUiState,
    currentDailyAllowance: BigDecimal,
    remainingBudget: BigDecimal,
    remainingDays: Int,
    currencyCode: String,
    onTabSelected: (SmartToolTab) -> Unit,
    onPriceInputChanged: (String) -> Unit,
    onAddGoal: (String, BigDecimal) -> Unit,
    onDepositToGoal: (MicroGoal, BigDecimal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onAddWishlistItem: (String, BigDecimal, Int) -> Unit,
    onMarkWishlistSaved: (WishlistItem) -> Unit,
    onMarkWishlistPurchased: (WishlistItem) -> Unit,
    onDeleteWishlistItem: (Long) -> Unit,
    onSetFunFundRatio: (Float) -> Unit,
    onSaveInstallment: (InstallmentItem) -> Unit = {},
    onPayInstallment: (String) -> Unit = {},
    onDeleteInstallment: (String) -> Unit = {},
    onSaveGameya: (GameyaItem) -> Unit = {},
    onAdvanceGameya: (String) -> Unit = {},
    onToggleGameyaCollected: (String) -> Unit = {},
    onDeleteGameya: (String) -> Unit = {},
    onSaveGoldPortfolio: (GoldPortfolio) -> Unit = {},
    onSaveZakatSettings: (ZakatState) -> Unit = {},
    onWalletInputChanged: (String) -> Unit = {},
    onClearWalletInput: () -> Unit = {},
    onScanReceipt: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val view = LocalView.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.smart_tools_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.smart_tools_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.smart_tools_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (onScanReceipt != null) {
                Card(
                    onClick = {
                        onDismiss()
                        onScanReceipt()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DocumentScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.receipt_scan_button),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Text(
                                    text = stringResource(R.string.smart_tool_ocr_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Horizontally Scrollable Category Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.CALCULATOR,
                        icon = Icons.Rounded.Calculate,
                        label = stringResource(R.string.smart_tab_affordability),
                        onClick = {
                            onTabSelected(SmartToolTab.CALCULATOR)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.INSTALLMENTS,
                        icon = Icons.Rounded.CreditCard,
                        label = stringResource(R.string.smart_tab_installments),
                        onClick = {
                            onTabSelected(SmartToolTab.INSTALLMENTS)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.GAMEYA,
                        icon = Icons.Rounded.Groups,
                        label = stringResource(R.string.smart_tab_gameya),
                        onClick = {
                            onTabSelected(SmartToolTab.GAMEYA)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.GOLD_ZAKAT,
                        icon = Icons.Rounded.MonetizationOn,
                        label = stringResource(R.string.smart_tab_gold_zakat),
                        onClick = {
                            onTabSelected(SmartToolTab.GOLD_ZAKAT)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.WALLET_PARSER,
                        icon = Icons.Rounded.AccountBalanceWallet,
                        label = stringResource(R.string.smart_tab_wallet_parser),
                        onClick = {
                            onTabSelected(SmartToolTab.WALLET_PARSER)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.MICRO_GOALS,
                        icon = Icons.Rounded.Savings,
                        label = stringResource(R.string.smart_tab_micro_goals),
                        onClick = {
                            onTabSelected(SmartToolTab.MICRO_GOALS)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.WISHLIST_48H,
                        icon = Icons.Rounded.Timer,
                        label = stringResource(R.string.smart_tab_wishlist_48h),
                        onClick = {
                            onTabSelected(SmartToolTab.WISHLIST_48H)
                            performUIHaptic(view)
                        },
                    )
                }
                item {
                    CategoryPill(
                        selected = uiState.selectedTab == SmartToolTab.FUN_FUND,
                        icon = Icons.Rounded.LocalCafe,
                        label = stringResource(R.string.smart_tab_fun_fund),
                        onClick = {
                            onTabSelected(SmartToolTab.FUN_FUND)
                            performUIHaptic(view)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Tab Content with Expressive scale + fade morphing
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = {
                    (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                            scaleIn(initialScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)))
                        .togetherWith(
                            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                    scaleOut(targetScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                        )
                },
                label = "smartToolsTabTransition",
                modifier = Modifier.animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
            ) { selectedTab ->
                when (selectedTab) {
                    SmartToolTab.CALCULATOR -> {
                        AffordabilityCalculatorTab(
                            uiState = uiState,
                            currentDailyAllowance = currentDailyAllowance,
                            remainingBudget = remainingBudget,
                            remainingDays = remainingDays,
                            currencyCode = currencyCode,
                            onPriceInputChanged = onPriceInputChanged,
                            onAddToWishlist = { title, price ->
                                onAddWishlistItem(title, price, 48)
                                onTabSelected(SmartToolTab.WISHLIST_48H)
                            },
                            onAddToJar = { title, target ->
                                onAddGoal(title, target)
                                onTabSelected(SmartToolTab.MICRO_GOALS)
                            },
                        )
                    }
                    SmartToolTab.INSTALLMENTS -> {
                        InstallmentsTab(
                            installments = uiState.installments,
                            currencyCode = currencyCode,
                            onSave = onSaveInstallment,
                            onPayMonth = onPayInstallment,
                            onDelete = onDeleteInstallment,
                        )
                    }
                    SmartToolTab.GAMEYA -> {
                        GameyaTab(
                            gameyas = uiState.gameyas,
                            currencyCode = currencyCode,
                            onSave = onSaveGameya,
                            onAdvanceMonth = onAdvanceGameya,
                            onToggleCollected = onToggleGameyaCollected,
                            onDelete = onDeleteGameya,
                        )
                    }
                    SmartToolTab.GOLD_ZAKAT -> {
                        GoldZakatTab(
                            goldPortfolio = uiState.goldPortfolio,
                            zakatSettings = uiState.zakatSettings,
                            remainingBudget = remainingBudget,
                            currencyCode = currencyCode,
                            onSaveGold = onSaveGoldPortfolio,
                            onSaveZakat = onSaveZakatSettings,
                        )
                    }
                    SmartToolTab.WALLET_PARSER -> {
                        WalletParserTab(
                            uiState = uiState,
                            onInputChanged = onWalletInputChanged,
                            onClear = onClearWalletInput,
                        )
                    }
                    SmartToolTab.MICRO_GOALS -> {
                        MicroGoalsTab(
                            goals = uiState.goals,
                            currencyCode = currencyCode,
                            dailyAllowance = currentDailyAllowance,
                            onAddGoal = onAddGoal,
                            onDepositToGoal = onDepositToGoal,
                            onDeleteGoal = onDeleteGoal,
                        )
                    }
                    SmartToolTab.WISHLIST_48H -> {
                        Wishlist48HTab(
                            wishlist = uiState.wishlist,
                            totalSavings = uiState.totalWishlistSavings,
                            currencyCode = currencyCode,
                            onAddItem = { title, price, hours -> onAddWishlistItem(title, price, hours) },
                            onMarkSaved = onMarkWishlistSaved,
                            onMarkPurchased = onMarkWishlistPurchased,
                            onDeleteItem = onDeleteWishlistItem,
                        )
                    }
                    SmartToolTab.FUN_FUND -> {
                        FunFundTab(
                            ratioPercent = uiState.funFundRatio,
                            remainingBudget = remainingBudget,
                            currencyCode = currencyCode,
                            onSetRatio = onSetFunFundRatio,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        },
        label = {
            Text(
                text = label,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

// ==========================================
// 1. CALCULATOR TAB
// ==========================================
@Composable
private fun AffordabilityCalculatorTab(
    uiState: SmartToolsUiState,
    currentDailyAllowance: BigDecimal,
    remainingBudget: BigDecimal,
    remainingDays: Int,
    currencyCode: String,
    onPriceInputChanged: (String) -> Unit,
    onAddToWishlist: (String, BigDecimal) -> Unit,
    onAddToJar: (String, BigDecimal) -> Unit,
) {
    var itemTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.smart_calc_header),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.calculationInputPrice,
            onValueChange = onPriceInputChanged,
            label = { Text(stringResource(R.string.smart_calc_price_label, currencyCode)) },
            placeholder = { Text(stringResource(R.string.smart_calc_price_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = itemTitle,
            onValueChange = { itemTitle = it },
            label = { Text(stringResource(R.string.smart_calc_name_label)) },
            placeholder = { Text(stringResource(R.string.smart_calc_name_placeholder)) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        val result = uiState.calculationResult
        if (result != null) {
            Spacer(modifier = Modifier.height(16.dp))

            val (badgeColor, verdictText) = when (result.verdict) {
                AffordabilityVerdictLevel.SAFE -> Pair(Color(0xFF2E7D32), stringResource(R.string.smart_calc_verdict_safe))
                AffordabilityVerdictLevel.CAUTION -> Pair(Color(0xFFF57C00), stringResource(R.string.smart_calc_verdict_caution))
                AffordabilityVerdictLevel.DANGEROUS -> Pair(MaterialTheme.colorScheme.error, stringResource(R.string.smart_calc_verdict_dangerous))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = badgeColor.copy(alpha = 0.12f),
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(badgeColor),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = verdictText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = result.recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.smart_calc_stat_days_cost),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", result.daysOfAllowanceCost)} d",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stringResource(R.string.smart_calc_stat_daily_after),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = "${result.newDailyAllowanceAfter.toInt()} $currencyCode",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val title = itemTitle.ifBlank {
                                    "حاجة بـ ${result.price.toPlainString()} $currencyCode"
                                }
                                onAddToWishlist(title, result.price)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.smart_calc_btn_wishlist), style = MaterialTheme.typography.labelMedium)
                        }

                        FilledTonalButton(
                            onClick = {
                                val title = itemTitle.ifBlank {
                                    "شراء ${result.price.toPlainString()} $currencyCode"
                                }
                                onAddToJar(title, result.price)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Rounded.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.smart_calc_btn_jar), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. INSTALLMENTS TAB (الأقساط و BNPL)
// ==========================================
@Composable
private fun InstallmentsTab(
    installments: List<InstallmentItem>,
    currencyCode: String,
    onSave: (InstallmentItem) -> Unit,
    onPayMonth: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val activeList = installments.filter { !it.isCompleted }
    val completedList = installments.filter { it.isCompleted }
    val totalMonthly = activeList.fold(BigDecimal.ZERO) { acc, item -> acc + item.monthlyAmount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        // Summary Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.smart_installment_total_monthly),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        text = "${totalMonthly.toPlainString()} $currencyCode",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.smart_installments_btn_add))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (installments.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.CreditCard,
                title = stringResource(R.string.smart_installments_empty_title),
                desc = stringResource(R.string.smart_installments_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(activeList, key = { it.id }) { item ->
                    InstallmentCard(
                        item = item,
                        currencyCode = currencyCode,
                        onPayMonth = { onPayMonth(item.id) },
                        onDelete = { onDelete(item.id) },
                    )
                }

                if (completedList.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.smart_installment_completed_badge),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    items(completedList, key = { it.id }) { item ->
                        InstallmentCard(
                            item = item,
                            currencyCode = currencyCode,
                            onPayMonth = {},
                            onDelete = { onDelete(item.id) },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddInstallmentDialog(
            currencyCode = currencyCode,
            onConfirm = { item ->
                onSave(item)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun InstallmentCard(
    item: InstallmentItem,
    currencyCode: String,
    onPayMonth: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (item.provider.isNotBlank()) {
                        Text(
                            text = item.provider,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${item.monthlyAmount.toPlainString()} $currencyCode / شهر",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.smart_installment_remaining_amount, item.remainingAmount.toPlainString(), currencyCode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { item.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (item.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.smart_installment_progress_format, item.paidMonths, item.totalMonths),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )

                if (!item.isCompleted) {
                    FilledTonalButton(
                        onClick = onPayMonth,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            stringResource(R.string.smart_installment_btn_pay_month),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.smart_installment_completed_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. GAM'EYA TAB (الجمعيات الشهرية)
// ==========================================
@Composable
private fun GameyaTab(
    gameyas: List<GameyaItem>,
    currencyCode: String,
    onSave: (GameyaItem) -> Unit,
    onAdvanceMonth: (String) -> Unit,
    onToggleCollected: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val activeList = gameyas.filter { !it.isCompleted }
    val totalMonthlyShares = activeList.fold(BigDecimal.ZERO) { acc, item -> acc + item.monthlyShare }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.smart_gameya_total_monthly),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        text = "${totalMonthlyShares.toPlainString()} $currencyCode",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.smart_gameya_btn_add))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (gameyas.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Groups,
                title = stringResource(R.string.smart_gameya_empty_title),
                desc = stringResource(R.string.smart_gameya_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(gameyas, key = { it.id }) { item ->
                    GameyaCard(
                        item = item,
                        currencyCode = currencyCode,
                        onAdvanceMonth = { onAdvanceMonth(item.id) },
                        onToggleCollected = { onToggleCollected(item.id) },
                        onDelete = { onDelete(item.id) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGameyaDialog(
            currencyCode = currencyCode,
            onConfirm = { item ->
                onSave(item)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun GameyaCard(
    item: GameyaItem,
    currencyCode: String,
    onAdvanceMonth: () -> Unit,
    onToggleCollected: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isDueForPayout) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            }

            Text(
                text = stringResource(R.string.smart_gameya_payout_target, item.payoutMonth, item.totalPayout.toPlainString(), currencyCode),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${item.monthlyShare.toPlainString()} $currencyCode / شهر • دُفع ${item.currentMonth} من ${item.totalMonths} شهر",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { item.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (item.isDueForPayout) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.15f)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.smart_gameya_due_now),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                        )
                        FilledTonalButton(
                            onClick = onToggleCollected,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(stringResource(R.string.smart_gameya_btn_toggle_collected), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else if (item.isCollected) {
                Text(
                    text = stringResource(R.string.smart_gameya_collected_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (item.currentMonth < item.totalMonths) {
                FilledTonalButton(
                    onClick = onAdvanceMonth,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(stringResource(R.string.smart_gameya_btn_pay_share), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// ==========================================
// 4. GOLD & ZAKAT TAB (الذهب والزكاة)
// ==========================================
@Composable
private fun GoldZakatTab(
    goldPortfolio: GoldPortfolio,
    zakatSettings: ZakatState,
    remainingBudget: BigDecimal,
    currencyCode: String,
    onSaveGold: (GoldPortfolio) -> Unit,
    onSaveZakat: (ZakatState) -> Unit,
) {
    var grams21 by remember(goldPortfolio) { mutableStateOf(goldPortfolio.grams21k.takeIf { it > BigDecimal.ZERO }?.toPlainString() ?: "") }
    var grams24 by remember(goldPortfolio) { mutableStateOf(goldPortfolio.grams24k.takeIf { it > BigDecimal.ZERO }?.toPlainString() ?: "") }
    var coins by remember(goldPortfolio) { mutableStateOf(goldPortfolio.goldCoins.takeIf { it > 0 }?.toString() ?: "") }
    var price21 by remember(goldPortfolio) { mutableStateOf(goldPortfolio.currentPricePerGram21k.takeIf { it > BigDecimal.ZERO }?.toPlainString() ?: "") }
    var hawlCompleted by remember(zakatSettings) { mutableStateOf(zakatSettings.isHawlCompleted) }

    val currentPriceBd = price21.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val currentGold = GoldPortfolio(
        grams21k = grams21.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        grams24k = grams24.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        goldCoins = coins.toIntOrNull() ?: 0,
        currentPricePerGram21k = currentPriceBd,
    )

    val zakatResult = com.wafeer.app.domain.model.smart.ZakatCalculator.calculate(
        liquidCash = remainingBudget,
        goldHoldingsValue = currentGold.currentTotalMarketValue,
        goldPrice21k = currentPriceBd,
        customNisab = zakatSettings.customNisabAmount,
        isHawlCompleted = hawlCompleted,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Gold Holdings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.smart_gold_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.smart_gold_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = grams21,
                            onValueChange = { grams21 = it },
                            label = { Text(stringResource(R.string.smart_gold_grams_21_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = grams24,
                            onValueChange = { grams24 = it },
                            label = { Text(stringResource(R.string.smart_gold_grams_24_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = coins,
                            onValueChange = { coins = it },
                            label = { Text(stringResource(R.string.smart_gold_coins_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = price21,
                            onValueChange = { price21 = it },
                            label = { Text(stringResource(R.string.smart_gold_price_21_label, currencyCode)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.smart_gold_total_weight, currentGold.totalEquivalent21kGrams.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.smart_gold_market_value, currentGold.currentTotalMarketValue.setScale(2, RoundingMode.HALF_UP).toPlainString(), currencyCode),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onSaveGold(currentGold)
                            onSaveZakat(zakatSettings.copy(goldPriceForNisab = currentPriceBd, isHawlCompleted = hawlCompleted))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(stringResource(R.string.smart_gold_btn_save))
                    }
                }
            }
        }

        // Zakat Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (zakatResult.isEligible) Color(0xFF2E7D32).copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.smart_zakat_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.smart_zakat_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.smart_zakat_nisab_info, zakatResult.nisabThreshold.toPlainString(), currencyCode),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Checkbox(
                            checked = hawlCompleted,
                            onCheckedChange = {
                                hawlCompleted = it
                                onSaveZakat(zakatSettings.copy(isHawlCompleted = it))
                            },
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.smart_zakat_hawl_checkbox),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (zakatResult.isEligible) {
                        Text(
                            text = stringResource(R.string.smart_zakat_eligible_badge, zakatResult.zakatDue.toPlainString(), currencyCode),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.smart_zakat_not_eligible_badge),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. WALLET / INSTAPAY PARSER TAB
// ==========================================
@Composable
private fun WalletParserTab(
    uiState: SmartToolsUiState,
    onInputChanged: (String) -> Unit,
    onClear: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val parsed = uiState.parsedTransfer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.smart_wallet_parser_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.walletInputText,
            onValueChange = onInputChanged,
            placeholder = { Text(stringResource(R.string.smart_wallet_input_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(16.dp),
            maxLines = 4,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilledTonalButton(
                onClick = {
                    val text = clipboardManager.getText()?.text ?: ""
                    if (text.isNotBlank()) {
                        onInputChanged(text)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.smart_wallet_btn_paste))
            }

            if (uiState.walletInputText.isNotBlank()) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.smart_wallet_btn_clear))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (parsed != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (parsed.isExpense) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else Color(0xFF2E7D32).copy(alpha = 0.15f)
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.smart_wallet_parsed_title),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${if (parsed.isExpense) "-" else "+"}${parsed.amount.toPlainString()} (${parsed.platform})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (parsed.isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.smart_wallet_transfer_party, parsed.party),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    if (!parsed.reference.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.smart_wallet_transfer_ref, parsed.reference),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }
        } else if (uiState.walletInputText.isNotBlank()) {
            Text(
                text = stringResource(R.string.smart_wallet_unrecognized),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

// ==========================================
// 6. MICRO GOALS TAB
// ==========================================
@Composable
private fun MicroGoalsTab(
    goals: List<MicroGoal>,
    currencyCode: String,
    dailyAllowance: BigDecimal,
    onAddGoal: (String, BigDecimal) -> Unit,
    onDepositToGoal: (MicroGoal, BigDecimal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<MicroGoal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.smart_goals_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.smart_goals_btn_new))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (goals.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Savings,
                title = stringResource(R.string.smart_goals_empty_title),
                desc = stringResource(R.string.smart_goals_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        currencyCode = currencyCode,
                        dailyAllowance = dailyAllowance,
                        onDeposit = { selectedGoalForDeposit = goal },
                        onDelete = { onDeleteGoal(goal.id) },
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGoalDialog(
            currencyCode = currencyCode,
            onConfirm = { title, target ->
                onAddGoal(title, target)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    selectedGoalForDeposit?.let { goal ->
        DepositGoalDialog(
            goal = goal,
            currencyCode = currencyCode,
            dailyAllowance = dailyAllowance,
            onConfirm = { amount ->
                onDepositToGoal(goal, amount)
                selectedGoalForDeposit = null
            },
            onDismiss = { selectedGoalForDeposit = null },
        )
    }
}

@Composable
private fun GoalCard(
    goal: MicroGoal,
    currencyCode: String,
    dailyAllowance: BigDecimal,
    onDeposit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) Color(0xFF2E7D32).copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.smart_goal_delete_desc),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    R.string.smart_goal_progress_text,
                    goal.savedAmount.toPlainString(),
                    goal.targetAmount.toPlainString(),
                    currencyCode,
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { goal.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (goal.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (goal.isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.smart_goal_completed_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    val paceDaily = dailyAllowance.multiply(BigDecimal("0.1"))
                    val days = if (paceDaily > BigDecimal.ZERO) goal.daysToTarget(paceDaily) else 0
                    if (days > 0) {
                        Text(
                            text = stringResource(
                                R.string.smart_goal_pace_tip,
                                paceDaily.setScale(0, RoundingMode.HALF_UP).toPlainString(),
                                currencyCode,
                                days,
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    } else {
                        Text(
                            text = "${(goal.progressRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                if (!goal.isCompleted) {
                    FilledTonalButton(
                        onClick = onDeposit,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.smart_goal_btn_deposit), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. 48-HOUR WISHLIST TAB
// ==========================================
@Composable
private fun Wishlist48HTab(
    wishlist: List<WishlistItem>,
    totalSavings: BigDecimal,
    currencyCode: String,
    onAddItem: (String, BigDecimal, Int) -> Unit,
    onMarkSaved: (WishlistItem) -> Unit,
    onMarkPurchased: (WishlistItem) -> Unit,
    onDeleteItem: (Long) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        if (totalSavings > BigDecimal.ZERO) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E7D32).copy(alpha = 0.12f),
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.smart_wishlist_savings_header),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                        )
                        Text(
                            text = "${totalSavings.toPlainString()} $currencyCode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.smart_wishlist_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.smart_wishlist_btn_add))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (wishlist.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.HourglassTop,
                title = stringResource(R.string.smart_wishlist_empty_title),
                desc = stringResource(R.string.smart_wishlist_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(wishlist, key = { it.id }) { item ->
                    WishlistCard(
                        item = item,
                        currencyCode = currencyCode,
                        onSaved = { onMarkSaved(item) },
                        onPurchased = { onMarkPurchased(item) },
                        onDelete = { onDeleteItem(item.id) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddWishlistDialog(
            currencyCode = currencyCode,
            onConfirm = { title, price, hours ->
                onAddItem(title, price, hours)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun WishlistCard(
    item: WishlistItem,
    currencyCode: String,
    onSaved: () -> Unit,
    onPurchased: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (item.status) {
                WishlistStatus.SAVED_AND_DISMISSED -> Color(0xFF2E7D32).copy(alpha = 0.12f)
                WishlistStatus.PURCHASED -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                WishlistStatus.COOLING -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            },
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${item.price.toPlainString()} $currencyCode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.smart_goal_delete_desc),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (item.status) {
                WishlistStatus.COOLING -> {
                    if (item.isCoolingFinished) {
                        Text(
                            text = stringResource(R.string.smart_wishlist_ready_prompt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = onSaved,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            ) {
                                Text(stringResource(R.string.smart_wishlist_btn_saved), style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = onPurchased,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Text(stringResource(R.string.smart_wishlist_btn_bought), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        val hours = item.remainingHours
                        val mins = item.remainingMinutes
                        Text(
                            text = stringResource(R.string.smart_wishlist_remaining_prompt, hours, mins),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
                WishlistStatus.SAVED_AND_DISMISSED -> {
                    Text(
                        text = stringResource(R.string.smart_wishlist_status_saved, item.price.toPlainString(), currencyCode),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                    )
                }
                WishlistStatus.PURCHASED -> {
                    Text(
                        text = stringResource(R.string.smart_wishlist_status_purchased),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

// ==========================================
// 8. FUN FUND TAB
// ==========================================
@Composable
private fun FunFundTab(
    ratioPercent: Float,
    remainingBudget: BigDecimal,
    currencyCode: String,
    onSetRatio: (Float) -> Unit,
) {
    val funAllowance = remainingBudget.multiply(BigDecimal(ratioPercent.toDouble() / 100.0))
        .setScale(2, RoundingMode.HALF_UP)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.smart_fun_fund_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.smart_fun_fund_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.smart_fun_fund_allocated_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${funAllowance.toPlainString()} $currencyCode",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.smart_fun_fund_allocated_tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.smart_fun_fund_slider_label),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(10f, 15f, 20f, 25f).forEach { ratio ->
                val selected = ratioPercent.toInt() == ratio.toInt()
                FilledTonalButton(
                    onClick = { onSetRatio(ratio) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (selected) {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors()
                    },
                ) {
                    Text("${ratio.toInt()}%")
                }
            }
        }
    }
}

// ==========================================
// SHARED EMPTY STATE CARD
// ==========================================
@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    desc: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ==========================================
// DIALOGS
// ==========================================
@Composable
private fun AddInstallmentDialog(
    currencyCode: String,
    onConfirm: (InstallmentItem) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }
    var monthlyAmount by remember { mutableStateOf("") }
    var totalMonths by remember { mutableStateOf("") }
    var paidMonths by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_add_installment_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.smart_dialog_installment_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_installment_name_hint)) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text(stringResource(R.string.smart_dialog_installment_provider)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_installment_provider_hint)) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = monthlyAmount,
                    onValueChange = { monthlyAmount = it },
                    label = { Text(stringResource(R.string.smart_dialog_installment_monthly, currencyCode)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalMonths,
                        onValueChange = { totalMonths = it },
                        label = { Text(stringResource(R.string.smart_dialog_installment_months_count)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = paidMonths,
                        onValueChange = { paidMonths = it },
                        label = { Text(stringResource(R.string.smart_dialog_installment_paid_months)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val monthly = monthlyAmount.toBigDecimalOrNull()
                            val totalM = totalMonths.toIntOrNull() ?: 1
                            val paidM = paidMonths.toIntOrNull() ?: 0
                            if (title.isNotBlank() && monthly != null && monthly > BigDecimal.ZERO && totalM > 0) {
                                val item = InstallmentItem(
                                    title = title.trim(),
                                    provider = provider.trim(),
                                    monthlyAmount = monthly,
                                    totalAmount = monthly.multiply(BigDecimal(totalM)),
                                    totalMonths = totalM,
                                    paidMonths = paidM.coerceIn(0, totalM),
                                )
                                onConfirm(item)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_installment_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddGameyaDialog(
    currencyCode: String,
    onConfirm: (GameyaItem) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var share by remember { mutableStateOf("") }
    var totalMonths by remember { mutableStateOf("") }
    var payoutMonth by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_add_gameya_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.smart_dialog_gameya_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_gameya_name_hint)) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = share,
                    onValueChange = { share = it },
                    label = { Text(stringResource(R.string.smart_dialog_gameya_share, currencyCode)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalMonths,
                        onValueChange = { totalMonths = it },
                        label = { Text(stringResource(R.string.smart_dialog_gameya_total_months)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = payoutMonth,
                        onValueChange = { payoutMonth = it },
                        label = { Text(stringResource(R.string.smart_dialog_gameya_payout_month)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val shareBd = share.toBigDecimalOrNull()
                            val totalM = totalMonths.toIntOrNull() ?: 1
                            val payoutM = payoutMonth.toIntOrNull() ?: 1
                            if (name.isNotBlank() && shareBd != null && shareBd > BigDecimal.ZERO && totalM > 0) {
                                val item = GameyaItem(
                                    name = name.trim(),
                                    monthlyShare = shareBd,
                                    totalMonths = totalM,
                                    payoutMonth = payoutM.coerceIn(1, totalM),
                                )
                                onConfirm(item)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_gameya_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateGoalDialog(
    currencyCode: String,
    onConfirm: (String, BigDecimal) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_create_goal_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.smart_dialog_goal_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_goal_name_hint)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text(stringResource(R.string.smart_dialog_goal_target, currencyCode)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_goal_target_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = targetAmount.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_create))
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositGoalDialog(
    goal: MicroGoal,
    currencyCode: String,
    dailyAllowance: BigDecimal,
    onConfirm: (BigDecimal) -> Unit,
    onDismiss: () -> Unit,
) {
    var customAmount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_deposit_title, goal.title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                val quickPercents = listOf(10, 20, 50)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    quickPercents.forEach { pct ->
                        val amount = dailyAllowance.multiply(BigDecimal(pct.toDouble() / 100.0))
                            .setScale(0, RoundingMode.HALF_UP)
                        FilledTonalButton(
                            onClick = {
                                if (amount > BigDecimal.ZERO) {
                                    onConfirm(amount)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("${amount.toPlainString()} $currencyCode", fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text(stringResource(R.string.smart_dialog_custom_amount, currencyCode)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = customAmount.toBigDecimalOrNull()
                            if (amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_deposit_now))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWishlistDialog(
    currencyCode: String,
    onConfirm: (String, BigDecimal, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_add_wishlist_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.smart_dialog_item_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_item_name_hint)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.smart_dialog_approx_price, currencyCode)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_approx_price_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = price.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount, 48)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_start_cooldown))
                    }
                }
            }
        }
    }
}

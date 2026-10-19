package com.wafeer.app.presentation.ui.settings.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafeer.app.R
import com.wafeer.app.domain.model.Category
import com.wafeer.app.presentation.ui.settings.categories.icons.CategoryIconsCatalog
import com.wafeer.app.presentation.ui.settings.categories.icons.IconPickerBottomSheet
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onBack: () -> Unit,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var dialogParentCategory by remember { mutableStateOf<Category?>(null) }
    var editingCategoryBudget by remember { mutableStateOf<CategoryWithBudgetUi?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.categories_management_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dialogParentCategory = null
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.category_add_parent))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.categories, key = { it.category.id }) { item ->
                    ParentCategoryItemCard(
                        item = item,
                        onToggleExpand = { viewModel.toggleCategoryExpansion(item.category.id) },
                        onToggleVisibility = { viewModel.toggleCategoryVisibility(item.category.id, item.category.isHidden) },
                        onAddChild = {
                            dialogParentCategory = item.category
                            showAddDialog = true
                        },
                        onEditBudget = { editingCategoryBudget = item },
                        onIconClick = { viewModel.openIconPicker(item.category.id) },
                        onDelete = { viewModel.deleteCategory(item.category.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (uiState.isIconPickerOpen) {
        IconPickerBottomSheet(
            selectedIconId = null,
            onIconSelected = viewModel::onIconSelected,
            onDismiss = viewModel::closeIconPicker
        )
    }

    if (showAddDialog) {
        AddCategoryDialog(
            parent = dialogParentCategory,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cap, threshold ->
                viewModel.addCategory(
                    name = name,
                    parentId = dialogParentCategory?.id,
                    iconName = if (dialogParentCategory == null) "ic_grocery" else "ic_milk",
                    budgetCap = cap,
                    alertThreshold = threshold
                )
                showAddDialog = false
            }
        )
    }

    editingCategoryBudget?.let { item ->
        EditCategoryBudgetDialog(
            item = item,
            onDismiss = { editingCategoryBudget = null },
            onConfirm = { cap, threshold ->
                viewModel.updateBudgetCap(item.category.id, cap, threshold)
                editingCategoryBudget = null
            }
        )
    }
}

@Composable
fun ParentCategoryItemCard(
    item: CategoryWithBudgetUi,
    onToggleExpand: () -> Unit,
    onToggleVisibility: () -> Unit,
    onAddChild: () -> Unit,
    onEditBudget: () -> Unit,
    onIconClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = onIconClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconsCatalog.getIcon(item.category.iconName),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onToggleExpand)
                ) {
                    Text(
                        text = CategoryLocalizationHelper.getDisplayName(item.category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.category_children_count, item.children.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.budgetCap != null && item.budgetCap > BigDecimal.ZERO) {
                            Text(
                                text = " · سقف: ${item.budgetCap} (${item.alertThresholdPercent}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Visibility Switch
                Switch(
                    checked = !item.category.isHidden,
                    onCheckedChange = { onToggleVisibility() }
                )

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (item.isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = item.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 8.dp)
                ) {
                    // Actions row: Set Cap, Add Sub-item, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = onAddChild,
                            leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(stringResource(R.string.category_add_child)) }
                        )
                        FilterChip(
                            selected = false,
                            onClick = onEditBudget,
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(stringResource(R.string.category_budget_cap_label)) }
                        )
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Children items
                    item.children.forEach { child ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconsCatalog.getIcon(child.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = CategoryLocalizationHelper.getDisplayName(child),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    parent: Category?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, cap: BigDecimal?, threshold: Int?) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var capInput by remember { mutableStateOf("") }
    var threshold by remember { mutableIntStateOf(80) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (parent == null) stringResource(R.string.category_add_parent)
                else "${stringResource(R.string.category_add_child)} (${CategoryLocalizationHelper.getDisplayName(parent)})"
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.category_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = capInput,
                    onValueChange = { capInput = it },
                    label = { Text(stringResource(R.string.category_budget_cap_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (capInput.toBigDecimalOrNull() != null) {
                    Text(text = stringResource(R.string.category_alert_threshold_label), style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(50, 75, 90, 100).forEach { p ->
                            FilterChip(
                                selected = threshold == p,
                                onClick = { threshold = p },
                                label = { Text("$p%") }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        onConfirm(nameInput, capInput.toBigDecimalOrNull(), threshold)
                    }
                },
                enabled = nameInput.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun EditCategoryBudgetDialog(
    item: CategoryWithBudgetUi,
    onDismiss: () -> Unit,
    onConfirm: (cap: BigDecimal?, threshold: Int?) -> Unit
) {
    var capInput by remember { mutableStateOf(item.budgetCap?.toPlainString() ?: "") }
    var threshold by remember { mutableIntStateOf(item.alertThresholdPercent ?: 80) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${stringResource(R.string.category_budget_cap_label)}: ${CategoryLocalizationHelper.getDisplayName(item.category)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = capInput,
                    onValueChange = { capInput = it },
                    label = { Text(stringResource(R.string.category_budget_cap_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = stringResource(R.string.category_alert_threshold_label), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(50, 75, 90, 100).forEach { p ->
                        FilterChip(
                            selected = threshold == p,
                            onClick = { threshold = p },
                            label = { Text("$p%") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(capInput.toBigDecimalOrNull(), threshold)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirm(null, null)
                }
            ) {
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

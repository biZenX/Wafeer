package com.wafeer.app.presentation.ui.settings.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafeer.app.data.local.dao.CategoryBudgetDao
import com.wafeer.app.data.local.dao.CategoryDao
import com.wafeer.app.data.local.entity.CategoryBudgetEntity
import com.wafeer.app.data.local.entity.CategoryEntity
import com.wafeer.app.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class CategoryWithBudgetUi(
    val category: Category,
    val children: List<Category> = emptyList(),
    val budgetCap: BigDecimal? = null,
    val alertThresholdPercent: Int? = 80,
    val isExpanded: Boolean = false
)

data class CategoryManagementUiState(
    val searchQuery: String = "",
    val categories: List<CategoryWithBudgetUi> = emptyList(),
    val isIconPickerOpen: Boolean = false,
    val pendingIconCategoryId: Long? = null
)

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryBudgetDao: CategoryBudgetDao
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val expandedCategories = MutableStateFlow<Set<Long>>(emptySet())
    private val iconPickerState = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<CategoryManagementUiState> = combine(
        categoryDao.getAllCategories(),
        categoryBudgetDao.getActiveCategoryBudgets(0L),
        searchQuery,
        expandedCategories,
        iconPickerState
    ) { allEntities, budgets, query, expanded, iconPickingId ->
        val budgetsMap = budgets.associateBy { it.categoryId }
        val allCategories = allEntities.map { entity ->
            Category(
                id = entity.id,
                name = entity.name,
                isHidden = entity.isHidden,
                usageCount = entity.usageCount,
                lastUsedAt = entity.lastUsedAt,
                createdAt = entity.createdAt,
                parentId = entity.parentId,
                nameResKey = entity.nameResKey,
                iconName = entity.iconName
            )
        }

        val parents = allCategories.filter { it.parentId == null }
        val childrenByParent = allCategories.filter { it.parentId != null }.groupBy { it.parentId }

        val uis = parents.map { parent ->
            val budget = budgetsMap[parent.id]
            val children = childrenByParent[parent.id] ?: emptyList()
            CategoryWithBudgetUi(
                category = parent,
                children = children,
                budgetCap = budget?.allocatedAmount?.let { BigDecimal(it) },
                alertThresholdPercent = budget?.alertThresholdPercent ?: 80,
                isExpanded = expanded.contains(parent.id)
            )
        }.filter {
            if (query.isBlank()) true
            else it.category.name.contains(query, ignoreCase = true) ||
                    it.children.any { c -> c.name.contains(query, ignoreCase = true) }
        }

        CategoryManagementUiState(
            searchQuery = query,
            categories = uis,
            isIconPickerOpen = iconPickingId != null,
            pendingIconCategoryId = iconPickingId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryManagementUiState()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun toggleCategoryExpansion(categoryId: Long) {
        val current = expandedCategories.value.toMutableSet()
        if (current.contains(categoryId)) current.remove(categoryId)
        else current.add(categoryId)
        expandedCategories.value = current
    }

    fun toggleCategoryVisibility(categoryId: Long, currentHidden: Boolean) {
        viewModelScope.launch {
            categoryDao.setCategoryAndChildrenHidden(categoryId, !currentHidden)
        }
    }

    fun addCategory(name: String, parentId: Long?, iconName: String?, budgetCap: BigDecimal?, alertThreshold: Int?) {
        viewModelScope.launch {
            val entity = CategoryEntity(
                name = name.trim(),
                parentId = parentId,
                iconName = iconName
            )
            val insertedId = categoryDao.insertCategory(entity)
            if (budgetCap != null && budgetCap > BigDecimal.ZERO) {
                categoryBudgetDao.upsertBudget(
                    CategoryBudgetEntity(
                        categoryId = insertedId,
                        allocatedAmount = budgetCap.toPlainString(),
                        alertThresholdPercent = alertThreshold ?: 80
                    )
                )
            }
        }
    }

    fun updateBudgetCap(categoryId: Long, budgetCap: BigDecimal?, alertThreshold: Int?) {
        viewModelScope.launch {
            if (budgetCap == null || budgetCap <= BigDecimal.ZERO) {
                categoryBudgetDao.deleteBudgetForCategory(categoryId)
            } else {
                categoryBudgetDao.upsertBudget(
                    CategoryBudgetEntity(
                        categoryId = categoryId,
                        allocatedAmount = budgetCap.toPlainString(),
                        alertThresholdPercent = alertThreshold ?: 80
                    )
                )
            }
        }
    }

    fun openIconPicker(categoryId: Long) {
        iconPickerState.value = categoryId
    }

    fun closeIconPicker() {
        iconPickerState.value = null
    }

    fun onIconSelected(iconId: String) {
        val catId = iconPickerState.value ?: return
        viewModelScope.launch {
            categoryDao.updateCategoryIcon(catId, iconId)
            closeIconPicker()
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            categoryDao.deleteCategoryById(categoryId)
            categoryBudgetDao.deleteBudgetForCategory(categoryId)
        }
    }
}

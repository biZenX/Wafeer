package com.wafeer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wafeer.app.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

data class ParentCategoryBudgetRollup(
    val categoryId: Long,
    val categoryName: String,
    val nameResKey: String?,
    val iconName: String?,
    val allocatedAmount: String?,
    val spentAmount: Double,
    val alertThresholdPercent: Int?,
    val isRecurring: Boolean?,
    val periodId: Long?,
    val childrenCount: Int
)

data class ChildCategorySpendBreakdown(
    val categoryId: Long,
    val categoryName: String,
    val nameResKey: String?,
    val iconName: String?,
    val allocatedAmount: String?,
    val spentAmount: Double,
    val alertThresholdPercent: Int?
)

@Dao
interface CategoryBudgetDao {

    @Query("""
        SELECT 
            p.id AS categoryId,
            p.name AS categoryName,
            p.nameResKey AS nameResKey,
            p.iconName AS iconName,
            cb.allocatedAmount AS allocatedAmount,
            COALESCE(SUM(CAST(t.amount AS REAL)), 0.0) AS spentAmount,
            cb.alertThresholdPercent AS alertThresholdPercent,
            cb.isRecurring AS isRecurring,
            cb.periodId AS periodId,
            (SELECT COUNT(*) FROM category WHERE parentId = p.id AND isHidden = 0) AS childrenCount
        FROM category p
        LEFT JOIN category_budgets cb ON cb.categoryId = p.id 
            AND (cb.periodId = :periodId OR (cb.isRecurring = 1 AND cb.periodId IS NULL))
        LEFT JOIN category c ON (c.id = p.id OR c.parentId = p.id)
        LEFT JOIN transactions t ON t.categoryId = c.id
            AND t.isAdjustment = 0
            AND (
                (:periodId > 0 AND t.periodId = :periodId)
                OR (:periodId <= 0 AND t.date >= :startDate AND t.date < :endDate)
            )
        WHERE p.parentId IS NULL AND p.isHidden = 0
        GROUP BY p.id, cb.id
        ORDER BY spentAmount DESC, p.name ASC
    """)
    fun getParentBudgetsWithRollup(
        periodId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<ParentCategoryBudgetRollup>>

    @Query("""
        SELECT 
            c.id AS categoryId,
            c.name AS categoryName,
            c.nameResKey AS nameResKey,
            c.iconName AS iconName,
            cb.allocatedAmount AS allocatedAmount,
            COALESCE(SUM(CAST(t.amount AS REAL)), 0.0) AS spentAmount,
            cb.alertThresholdPercent AS alertThresholdPercent
        FROM category c
        LEFT JOIN category_budgets cb ON cb.categoryId = c.id 
            AND (cb.periodId = :periodId OR (cb.isRecurring = 1 AND cb.periodId IS NULL))
        LEFT JOIN transactions t ON t.categoryId = c.id
            AND t.isAdjustment = 0
            AND (
                (:periodId > 0 AND t.periodId = :periodId)
                OR (:periodId <= 0 AND t.date >= :startDate AND t.date < :endDate)
            )
        WHERE c.parentId = :parentId AND c.isHidden = 0
        GROUP BY c.id, cb.id
        ORDER BY spentAmount DESC, c.name ASC
    """)
    fun getChildBreakdownForParent(
        parentId: Long,
        periodId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<ChildCategorySpendBreakdown>>

    @Query("""
        SELECT * FROM category_budgets
        WHERE categoryId = :categoryId 
          AND (periodId = :periodId OR (isRecurring = 1 AND periodId IS NULL))
        ORDER BY periodId DESC
        LIMIT 1
    """)
    suspend fun getBudgetForCategory(categoryId: Long, periodId: Long): CategoryBudgetEntity?

    @Query("SELECT * FROM category_budgets WHERE periodId IS NULL OR periodId = :periodId")
    fun getActiveCategoryBudgets(periodId: Long): Flow<List<CategoryBudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: CategoryBudgetEntity): Long

    @Delete
    suspend fun deleteBudget(budget: CategoryBudgetEntity)

    @Query("DELETE FROM category_budgets WHERE categoryId = :categoryId")
    suspend fun deleteBudgetForCategory(categoryId: Long)
}

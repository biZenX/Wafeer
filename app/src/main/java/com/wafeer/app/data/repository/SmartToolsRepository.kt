package com.wafeer.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wafeer.app.data.local.dao.MicroGoalDao
import com.wafeer.app.data.local.dao.WishlistDao
import com.wafeer.app.data.local.entity.MicroGoalEntity
import com.wafeer.app.data.local.entity.WishlistItemEntity
import com.wafeer.app.domain.model.smart.AffordabilityCalculation
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.GameyaItem
import com.wafeer.app.domain.model.smart.GoldPortfolio
import com.wafeer.app.domain.model.smart.InstallmentItem
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import com.wafeer.app.domain.model.smart.ZakatState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class InstallmentDto(
    val id: String,
    val title: String,
    val totalAmount: String,
    val monthlyAmount: String,
    val totalMonths: Int,
    val paidMonths: Int,
    val dueDayOfMonth: Int = 1,
    val category: String = "أقساط",
    val provider: String = "",
    val createdAt: Long = 0L,
) {
    fun toDomain() = InstallmentItem(
        id = id,
        title = title,
        totalAmount = runCatching { BigDecimal(totalAmount) }.getOrDefault(BigDecimal.ZERO),
        monthlyAmount = runCatching { BigDecimal(monthlyAmount) }.getOrDefault(BigDecimal.ZERO),
        totalMonths = totalMonths,
        paidMonths = paidMonths,
        dueDayOfMonth = dueDayOfMonth,
        category = category,
        provider = provider,
        createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis(),
    )

    companion object {
        fun fromDomain(domain: InstallmentItem) = InstallmentDto(
            id = domain.id,
            title = domain.title,
            totalAmount = domain.totalAmount.toPlainString(),
            monthlyAmount = domain.monthlyAmount.toPlainString(),
            totalMonths = domain.totalMonths,
            paidMonths = domain.paidMonths,
            dueDayOfMonth = domain.dueDayOfMonth,
            category = domain.category,
            provider = domain.provider,
            createdAt = domain.createdAt,
        )
    }
}

@Serializable
private data class GameyaDto(
    val id: String,
    val name: String,
    val monthlyShare: String,
    val totalMonths: Int,
    val payoutMonth: Int,
    val currentMonth: Int,
    val isCollected: Boolean = false,
    val createdAt: Long = 0L,
) {
    fun toDomain() = GameyaItem(
        id = id,
        name = name,
        monthlyShare = runCatching { BigDecimal(monthlyShare) }.getOrDefault(BigDecimal.ZERO),
        totalMonths = totalMonths,
        payoutMonth = payoutMonth,
        currentMonth = currentMonth,
        isCollected = isCollected,
        createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis(),
    )

    companion object {
        fun fromDomain(domain: GameyaItem) = GameyaDto(
            id = domain.id,
            name = domain.name,
            monthlyShare = domain.monthlyShare.toPlainString(),
            totalMonths = domain.totalMonths,
            payoutMonth = domain.payoutMonth,
            currentMonth = domain.currentMonth,
            isCollected = domain.isCollected,
            createdAt = domain.createdAt,
        )
    }
}

@Serializable
private data class GoldPortfolioDto(
    val grams21k: String = "0",
    val grams24k: String = "0",
    val goldCoins: Int = 0,
    val averagePurchasePricePerGram: String = "0",
    val currentPricePerGram21k: String = "0",
    val lastUpdated: Long = 0L,
) {
    fun toDomain() = GoldPortfolio(
        grams21k = runCatching { BigDecimal(grams21k) }.getOrDefault(BigDecimal.ZERO),
        grams24k = runCatching { BigDecimal(grams24k) }.getOrDefault(BigDecimal.ZERO),
        goldCoins = goldCoins,
        averagePurchasePricePerGram = runCatching { BigDecimal(averagePurchasePricePerGram) }.getOrDefault(BigDecimal.ZERO),
        currentPricePerGram21k = runCatching { BigDecimal(currentPricePerGram21k) }.getOrDefault(BigDecimal.ZERO),
        lastUpdated = if (lastUpdated > 0) lastUpdated else System.currentTimeMillis(),
    )

    companion object {
        fun fromDomain(domain: GoldPortfolio) = GoldPortfolioDto(
            grams21k = domain.grams21k.toPlainString(),
            grams24k = domain.grams24k.toPlainString(),
            goldCoins = domain.goldCoins,
            averagePurchasePricePerGram = domain.averagePurchasePricePerGram.toPlainString(),
            currentPricePerGram21k = domain.currentPricePerGram21k.toPlainString(),
            lastUpdated = domain.lastUpdated,
        )
    }
}

@Serializable
private data class ZakatSettingsDto(
    val customNisabAmount: String? = null,
    val goldPriceForNisab: String = "0",
    val isHawlCompleted: Boolean = true,
) {
    fun toDomain() = ZakatState(
        customNisabAmount = customNisabAmount?.let { runCatching { BigDecimal(it) }.getOrNull() },
        goldPriceForNisab = runCatching { BigDecimal(goldPriceForNisab) }.getOrDefault(BigDecimal.ZERO),
        isHawlCompleted = isHawlCompleted,
    )

    companion object {
        fun fromDomain(domain: ZakatState) = ZakatSettingsDto(
            customNisabAmount = domain.customNisabAmount?.toPlainString(),
            goldPriceForNisab = domain.goldPriceForNisab.toPlainString(),
            isHawlCompleted = domain.isHawlCompleted,
        )
    }
}

@Singleton
class SmartToolsRepository @Inject constructor(
    private val microGoalDao: MicroGoalDao,
    private val wishlistDao: WishlistDao,
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val KEY_FUN_FUND_RATIO = floatPreferencesKey("smart_fun_fund_ratio")
        private val KEY_TOTAL_WISHLIST_SAVINGS = stringPreferencesKey("smart_total_wishlist_savings")
        private val KEY_INSTALLMENTS = stringPreferencesKey("smart_installments_json")
        private val KEY_GAMEYAS = stringPreferencesKey("smart_gameyas_json")
        private val KEY_GOLD_PORTFOLIO = stringPreferencesKey("smart_gold_portfolio_json")
        private val KEY_ZAKAT_SETTINGS = stringPreferencesKey("smart_zakat_settings_json")

        private val jsonParser = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    // --- Micro Goals ---
    fun observeGoals(): Flow<List<MicroGoal>> =
        microGoalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    suspend fun addGoal(title: String, targetAmount: BigDecimal): Long {
        val entity = MicroGoalEntity(
            title = title.trim(),
            targetAmount = targetAmount.toPlainString(),
            savedAmount = "0",
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
        )
        return microGoalDao.insertGoal(entity)
    }

    suspend fun depositToGoal(goal: MicroGoal, depositAmount: BigDecimal) {
        val newSaved = (goal.savedAmount + depositAmount).coerceAtLeast(BigDecimal.ZERO)
        val completed = newSaved >= goal.targetAmount
        val updated = MicroGoalEntity.fromDomain(
            goal.copy(
                savedAmount = newSaved,
                isCompleted = completed,
            )
        )
        microGoalDao.updateGoal(updated)
    }

    suspend fun deleteGoal(id: Long) {
        microGoalDao.deleteGoalById(id)
    }

    // --- 48-Hour Wishlist ---
    fun observeWishlist(): Flow<List<WishlistItem>> =
        wishlistDao.getAllItems().map { list -> list.map { it.toDomain() } }

    suspend fun addToWishlist(title: String, price: BigDecimal, coolingHours: Int = 48): Long {
        val entity = WishlistItemEntity(
            title = title.trim(),
            price = price.toPlainString(),
            createdAt = System.currentTimeMillis(),
            coolingHours = coolingHours,
            status = WishlistStatus.COOLING.name,
        )
        return wishlistDao.insertItem(entity)
    }

    suspend fun markWishlistSaved(item: WishlistItem) {
        val updated = WishlistItemEntity.fromDomain(
            item.copy(
                status = WishlistStatus.SAVED_AND_DISMISSED,
                resolvedAt = System.currentTimeMillis(),
            )
        )
        wishlistDao.updateItem(updated)

        // Increment total savings
        dataStore.edit { prefs ->
            val currentStr = prefs[KEY_TOTAL_WISHLIST_SAVINGS] ?: "0"
            val current = runCatching { BigDecimal(currentStr) }.getOrDefault(BigDecimal.ZERO)
            val newTotal = current + item.price
            prefs[KEY_TOTAL_WISHLIST_SAVINGS] = newTotal.toPlainString()
        }
    }

    suspend fun markWishlistPurchased(item: WishlistItem) {
        val updated = WishlistItemEntity.fromDomain(
            item.copy(
                status = WishlistStatus.PURCHASED,
                resolvedAt = System.currentTimeMillis(),
            )
        )
        wishlistDao.updateItem(updated)
    }

    suspend fun deleteWishlistItem(id: Long) {
        wishlistDao.deleteItemById(id)
    }

    fun observeTotalWishlistSavings(): Flow<BigDecimal> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_TOTAL_WISHLIST_SAVINGS] ?: "0"
            runCatching { BigDecimal(raw) }.getOrDefault(BigDecimal.ZERO)
        }

    // --- Fun Fund Ratio ---
    fun observeFunFundRatio(): Flow<Float> =
        dataStore.data.map { prefs ->
            prefs[KEY_FUN_FUND_RATIO] ?: 15f
        }

    suspend fun setFunFundRatio(ratioPercent: Float) {
        dataStore.edit { prefs ->
            prefs[KEY_FUN_FUND_RATIO] = ratioPercent.coerceIn(5f, 40f)
        }
    }

    // --- Installments (الأقساط و BNPL) ---
    fun observeInstallments(): Flow<List<InstallmentItem>> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_INSTALLMENTS] ?: ""
            if (raw.isBlank()) emptyList()
            else {
                runCatching {
                    jsonParser.decodeFromString<List<InstallmentDto>>(raw).map { it.toDomain() }
                }.getOrDefault(emptyList())
            }
        }

    suspend fun saveInstallment(item: InstallmentItem) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_INSTALLMENTS] ?: ""
            val list = if (raw.isBlank()) mutableListOf()
            else {
                runCatching {
                    jsonParser.decodeFromString<List<InstallmentDto>>(raw).toMutableList()
                }.getOrDefault(mutableListOf())
            }
            val existingIndex = list.indexOfFirst { it.id == item.id }
            val dto = InstallmentDto.fromDomain(item)
            if (existingIndex >= 0) {
                list[existingIndex] = dto
            } else {
                list.add(0, dto)
            }
            prefs[KEY_INSTALLMENTS] = jsonParser.encodeToString(list)
        }
    }

    suspend fun payInstallmentMonth(id: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_INSTALLMENTS] ?: ""
            if (raw.isNotBlank()) {
                val list = runCatching {
                    jsonParser.decodeFromString<List<InstallmentDto>>(raw).toMutableList()
                }.getOrNull()
                if (list != null) {
                    val index = list.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val current = list[index]
                        val newPaid = (current.paidMonths + 1).coerceAtMost(current.totalMonths)
                        list[index] = current.copy(paidMonths = newPaid)
                        prefs[KEY_INSTALLMENTS] = jsonParser.encodeToString(list)
                    }
                }
            }
        }
    }

    suspend fun deleteInstallment(id: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_INSTALLMENTS] ?: ""
            if (raw.isNotBlank()) {
                val list = runCatching {
                    jsonParser.decodeFromString<List<InstallmentDto>>(raw).toMutableList()
                }.getOrNull()
                if (list != null) {
                    list.removeAll { it.id == id }
                    prefs[KEY_INSTALLMENTS] = jsonParser.encodeToString(list)
                }
            }
        }
    }

    // --- Gam'eya (الجمعيات الشهرية) ---
    fun observeGameyas(): Flow<List<GameyaItem>> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_GAMEYAS] ?: ""
            if (raw.isBlank()) emptyList()
            else {
                runCatching {
                    jsonParser.decodeFromString<List<GameyaDto>>(raw).map { it.toDomain() }
                }.getOrDefault(emptyList())
            }
        }

    suspend fun saveGameya(item: GameyaItem) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_GAMEYAS] ?: ""
            val list = if (raw.isBlank()) mutableListOf()
            else {
                runCatching {
                    jsonParser.decodeFromString<List<GameyaDto>>(raw).toMutableList()
                }.getOrDefault(mutableListOf())
            }
            val existingIndex = list.indexOfFirst { it.id == item.id }
            val dto = GameyaDto.fromDomain(item)
            if (existingIndex >= 0) {
                list[existingIndex] = dto
            } else {
                list.add(0, dto)
            }
            prefs[KEY_GAMEYAS] = jsonParser.encodeToString(list)
        }
    }

    suspend fun advanceGameyaMonth(id: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_GAMEYAS] ?: ""
            if (raw.isNotBlank()) {
                val list = runCatching {
                    jsonParser.decodeFromString<List<GameyaDto>>(raw).toMutableList()
                }.getOrNull()
                if (list != null) {
                    val index = list.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val current = list[index]
                        val newMonth = (current.currentMonth + 1).coerceAtMost(current.totalMonths)
                        list[index] = current.copy(currentMonth = newMonth)
                        prefs[KEY_GAMEYAS] = jsonParser.encodeToString(list)
                    }
                }
            }
        }
    }

    suspend fun toggleGameyaCollected(id: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_GAMEYAS] ?: ""
            if (raw.isNotBlank()) {
                val list = runCatching {
                    jsonParser.decodeFromString<List<GameyaDto>>(raw).toMutableList()
                }.getOrNull()
                if (list != null) {
                    val index = list.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val current = list[index]
                        list[index] = current.copy(isCollected = !current.isCollected)
                        prefs[KEY_GAMEYAS] = jsonParser.encodeToString(list)
                    }
                }
            }
        }
    }

    suspend fun deleteGameya(id: String) {
        dataStore.edit { prefs ->
            val raw = prefs[KEY_GAMEYAS] ?: ""
            if (raw.isNotBlank()) {
                val list = runCatching {
                    jsonParser.decodeFromString<List<GameyaDto>>(raw).toMutableList()
                }.getOrNull()
                if (list != null) {
                    list.removeAll { it.id == id }
                    prefs[KEY_GAMEYAS] = jsonParser.encodeToString(list)
                }
            }
        }
    }

    // --- Gold Portfolio (محفظة الذهب وملاذات الادخار) ---
    fun observeGoldPortfolio(): Flow<GoldPortfolio> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_GOLD_PORTFOLIO] ?: ""
            if (raw.isBlank()) GoldPortfolio()
            else {
                runCatching {
                    jsonParser.decodeFromString<GoldPortfolioDto>(raw).toDomain()
                }.getOrDefault(GoldPortfolio())
            }
        }

    suspend fun saveGoldPortfolio(portfolio: GoldPortfolio) {
        dataStore.edit { prefs ->
            val dto = GoldPortfolioDto.fromDomain(portfolio)
            prefs[KEY_GOLD_PORTFOLIO] = jsonParser.encodeToString(dto)
        }
    }

    // --- Zakat Settings ---
    fun observeZakatSettings(): Flow<ZakatState> =
        dataStore.data.map { prefs ->
            val raw = prefs[KEY_ZAKAT_SETTINGS] ?: ""
            if (raw.isBlank()) ZakatState()
            else {
                runCatching {
                    jsonParser.decodeFromString<ZakatSettingsDto>(raw).toDomain()
                }.getOrDefault(ZakatState())
            }
        }

    suspend fun saveZakatSettings(state: ZakatState) {
        dataStore.edit { prefs ->
            val dto = ZakatSettingsDto.fromDomain(state)
            prefs[KEY_ZAKAT_SETTINGS] = jsonParser.encodeToString(dto)
        }
    }

    // --- Can I Afford It? Calculator ---
    fun calculateAffordability(
        price: BigDecimal,
        currentDailyAllowance: BigDecimal,
        remainingBudget: BigDecimal,
        remainingDays: Int,
        currencyCode: String = "USD",
    ): AffordabilityCalculation {
        val days = remainingDays.coerceAtLeast(1)
        val daily = currentDailyAllowance.coerceAtLeast(BigDecimal.ZERO)

        val daysCost = if (daily > BigDecimal.ZERO) {
            (price.toDouble() / daily.toDouble()).toFloat()
        } else {
            0f
        }

        val budgetAfter = remainingBudget - price
        val newDailyAfter = if (days > 0) {
            budgetAfter.divide(BigDecimal(days), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        val (verdict, recommendation) = when {
            price > remainingBudget -> {
                val rec = if (isAr) {
                    "السعر أكبر من ميزانيتك المتبقية (${remainingBudget.toPlainString()} $currencyCode). لو اشتريتها هتكسر الميزانية أو تضطر تستلف!"
                } else {
                    "The price exceeds your remaining budget ($currencyCode ${remainingBudget.toPlainString()}). Buying this will put you over budget!"
                }
                Pair(AffordabilityVerdictLevel.DANGEROUS, rec)
            }
            newDailyAfter < (daily * BigDecimal("0.5")) -> {
                val rec = if (isAr) {
                    "هتاكل منك مصروف ${String.format(java.util.Locale.US, "%.1f", daysCost)} يوم! مصروفك اليومي هينزل من ${daily.toInt()} إلى ${newDailyAfter.coerceAtLeast(BigDecimal.ZERO).toInt()} $currencyCode. محتاج تشد الحزام باقي الأيام."
                } else {
                    "This equals ${String.format(java.util.Locale.US, "%.1f", daysCost)} days of allowance! Your daily budget will drop from ${daily.toInt()} to ${newDailyAfter.coerceAtLeast(BigDecimal.ZERO).toInt()} $currencyCode. You'll need to tighten your belt."
                }
                Pair(AffordabilityVerdictLevel.CAUTION, rec)
            }
            else -> {
                val rec = if (isAr) {
                    "تقدر تشتريها وأنت مطمن! هتكلفك تقريباً ${String.format(java.util.Locale.US, "%.1f", daysCost)} يوم من المصروف، وميزانيتك اليومية هتفضل مستقرة عند ${newDailyAfter.toInt()} $currencyCode."
                } else {
                    "Safe to buy! It costs about ${String.format(java.util.Locale.US, "%.1f", daysCost)} days of allowance, and your daily budget will remain stable at ${newDailyAfter.toInt()} $currencyCode."
                }
                Pair(AffordabilityVerdictLevel.SAFE, rec)
            }
        }

        return AffordabilityCalculation(
            price = price,
            currentDailyAllowance = daily,
            daysOfAllowanceCost = daysCost,
            newDailyAllowanceAfter = newDailyAfter.coerceAtLeast(BigDecimal.ZERO),
            verdict = verdict,
            recommendation = recommendation,
        )
    }
}

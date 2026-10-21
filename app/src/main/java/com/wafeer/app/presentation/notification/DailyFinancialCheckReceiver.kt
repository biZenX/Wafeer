package com.wafeer.app.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wafeer.app.data.repository.BudgetRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DailyFinancialCheckReceiver : BroadcastReceiver() {

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        logcat("DailyFinancialCheckReceiver") { "onReceive called for daily financial check" }
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = budgetRepository.getBudgetSettingsSync()
                if (settings != null) {
                    val today = LocalDate.now()
                    val todaySpent = budgetRepository.getSpentForDate(today).first()
                    val dailyBudget = settings.calculateDailyBudget()
                    val currency = settings.currencyCode

                    val periodSpent = budgetRepository.getSpentForPeriod(settings.startDate, today).first()
                    val isPeriodOverspent = periodSpent > settings.totalBudget
                    val isDailyOverspent = dailyBudget > BigDecimal.ZERO && todaySpent > dailyBudget

                    if (isPeriodOverspent || isDailyOverspent) {
                        val overAmount = if (isPeriodOverspent) {
                            periodSpent.subtract(settings.totalBudget)
                        } else {
                            todaySpent.subtract(dailyBudget)
                        }
                        logcat("DailyFinancialCheckReceiver") { "Overspend detected ($overAmount $currency) -> Showing animated cracking pot notification" }
                        notificationHelper.showOverspendAlert(
                            overspendAmount = overAmount.toPlainString(),
                            currency = currency,
                            isPeriodOverspend = isPeriodOverspent,
                        )
                    } else {
                        logcat("DailyFinancialCheckReceiver") { "Spending within budget (todaySpent=$todaySpent, dailyBudget=$dailyBudget) -> canceling overspend alert" }
                        notificationHelper.cancelOverspendAlert()
                    }
                }
                com.wafeer.app.data.updater.AppUpdateCheckWorker.checkNow(context.applicationContext)
                notificationScheduler.scheduleDailyFinancialCheck()
            } catch (e: Exception) {
                logcat("DailyFinancialCheckReceiver") { "Error in daily financial check: ${e.message}" }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

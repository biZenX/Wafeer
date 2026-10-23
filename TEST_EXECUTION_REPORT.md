# تقرير نتائج الاختبارات الشامل لتطبيق وفير (Wafeer Test Execution Report)

> **تاريخ ووقت الفحص:** 2026-10-23 19:21:16  
> **حالة الاختبارات الإجمالية:** `100% ناجحة (ALL PASSED)` ✅  
> **الفروع والوحدات المشمولة:** `:app` (FOSS Debug), `:sync-contract`, `:wear`  

---

## 1. الإحصائيات العامة (Summary Metrics)

| المؤشر | القيمة | الحالة |
| :--- | :--- | :--- |
| **إجمالي عدد الاختبارات (Total Tests)** | **747** | مكتمل |
| **الاختبارات الناجحة (Passed)** | **747** | **100% بنجاح** ✅ |
| **الاختبارات الفاشلة (Failed)** | **0** | **0** 🛡️ |
| **الأخطاء أثناء التنفيذ (Errors)** | **0** | **0** |
| **الاختبارات المتجاوزة (Skipped)** | **0** | **0** |
| **إجمالي حزم الاختبار (Test Suites)** | **113** حزمة | 100% ناجحة |
| **اختبارات واجهة المستخدم التلقائية (Paparazzi UI Snapshots)** | **62 حزمة** | تم التحقق منها بصرياً وهندسياً |
| **الفحص السكوني لجودة الكود (Detekt Static Analysis)** | **BUILD SUCCESSFUL** | خالي من الأخطاء |
| **إجمالي زمن تشغيل الاختبارات الصافي** | **51.40 ثانية** | عالي الكفاءة |

---

## 2. ملخص حزم الاختبار حسب النطاق الوظيفي (Functional Categories)

1. **طبقة البيانات والتخزين (Data & Storage Layer):**
   - اختبارات قاعدة بيانات Room والـ DAOs.
   - استيراد وتصدير ملفات CSV واختبارات التحصين ضد حقن الصيغ الرياضية (CSV Formula Injection Sanitization).
   - مستودعات الإعدادات والأدوات الذكية (`SettingsRepository`, `SmartToolsRepository`, `BudgetRepository`).

2. **طبقة منطق الأعمال والحسابات (Domain & Financial Logic):**
   - حاسبة الميزانية اليومية والفترة (`BudgetStateCalculator`, `DailyFinancialCalculator`).
   - المصروفات الدورية والاشتراكات الشهرية (`RecurringExpenseCalculator`).
   - انتقال الفترات عند منتصف الليل ومعالجة الفترات السابقة (`MidnightPeriodChecker`, `BackfillOrphanedPeriods`).
   - خوارزمية تحليل الإشعارات البنكية التلقائية (`ExpenseNotificationParser`).

3. **طبقة العرض والتحكم (Presentation & ViewModels):**
   - إدارة الميزانيات، شاشة التحليلات، وسجل المعاملات (`BudgetViewModel`, `AnalyticsViewModel`, `HistoryViewModel`).
   - إدارة التنبيهات وإعادة الجدولة بعد إعادة التشغيل (`NotificationScheduler`, `NotificationRescheduleReceiver`).

4. **مزامنة الساعة الذكية وبروتوكول الاتصال (Wear Sync Protocol):**
   - اختبار تسلسل وإلغاء تسلسل رسائل الـ Ack وحزم المزامنة (`WearSyncProtocolTest`).
   - استيعاب المعاملات ومنع التكرار والحماية بالأقفال (`WearExpenseIngestor`).

5. **اختبارات مطابقة واجهة المستخدم (UI Snapshot Tests with Paparazzi):**
   - 62 اختبار شاشة ومكون رسومي للتأكد من ثبات التصميم، الألوان، RTL، والخطوط عبر مختلف الأحجام.

---

## 3. جدول حزم الاختبارات (Test Suites Table)

| الحزمة / الفئة (Suite Class) | الوحدة (Module) | الاختبارات | ناجح | فاشل | متجاوز | المدة (ثانية) |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: |
| `app.data.csv.WafeerCsvParserTest` | :app (fossDebug) | 6 | 6 ✅ | 0 | 0 | 0.055s |
| `app.data.csv.WafeerCsvRoundTripTest` | :app (fossDebug) | 17 | 17 ✅ | 0 | 0 | 0.080s |
| `app.data.csv.WafeerCsvServiceTest` | :app (fossDebug) | 16 | 16 ✅ | 0 | 0 | 2.068s |
| `app.data.repository.BudgetRepositoryImplCategoryTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.443s |
| `app.data.repository.SettingsRepositoryImplTest` | :app (fossDebug) | 22 | 22 ✅ | 0 | 0 | 0.073s |
| `app.data.repository.SmartToolsRepositoryTest` | :app (fossDebug) | 5 | 5 ✅ | 0 | 0 | 0.246s |
| `app.data.updater.AppUpdateManagerTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.004s |
| `app.domain.calculator.EvaluatorTest` | :app (fossDebug) | 6 | 6 ✅ | 0 | 0 | 0.011s |
| `app.domain.calculator.RecurringExpenseCalculatorTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.003s |
| `app.domain.calculator.TransactionPeriodMapperTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.002s |
| `app.domain.model.CreditCardTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.001s |
| `app.domain.model.SavingsPreferencesTest` | :app (fossDebug) | 22 | 22 ✅ | 0 | 0 | 0.005s |
| `app.domain.notification.ExpenseNotificationParserTest` | :app (fossDebug) | 18 | 18 ✅ | 0 | 0 | 0.016s |
| `app.domain.time.MidnightPeriodCheckerTest` | :app (fossDebug) | 29 | 29 ✅ | 0 | 0 | 0.594s |
| `app.domain.usecase.BackfillOrphanedPeriodsUseCaseOrchestrationTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.045s |
| `app.domain.usecase.BackfillOrphanedPeriodsUseCaseTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.000s |
| `app.domain.usecase.ChangelogTriggerEvaluatorTest` | :app (fossDebug) | 13 | 13 ✅ | 0 | 0 | 0.008s |
| `app.domain.usecase.FoldCarryIntoTotalBudgetUseCaseTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.029s |
| `app.presentation.notification.NotificationRescheduleReceiverTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.004s |
| `app.presentation.notification.NotificationSchedulerTest` | :app (fossDebug) | 20 | 20 ✅ | 0 | 0 | 0.430s |
| `app.presentation.ui.analytics.AnalyticsViewModelTest` | :app (fossDebug) | 11 | 11 ✅ | 0 | 0 | 0.994s |
| `app.presentation.ui.budget.AskMeLeftoverChoiceTest` | :app (fossDebug) | 34 | 34 ✅ | 0 | 0 | 0.013s |
| `app.presentation.ui.budget.BudgetPeriodManagerTest` | :app (fossDebug) | 17 | 17 ✅ | 0 | 0 | 0.511s |
| `app.presentation.ui.budget.BudgetStateCalculatorTest` | :app (fossDebug) | 21 | 21 ✅ | 0 | 0 | 0.009s |
| `app.presentation.ui.budget.BudgetTransactionHandlerTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 0.115s |
| `app.presentation.ui.budget.BudgetViewModelTest` | :app (fossDebug) | 22 | 22 ✅ | 0 | 0 | 1.233s |
| `app.presentation.ui.budget.DailySurplusSplitModeChangeTest` | :app (fossDebug) | 32 | 32 ✅ | 0 | 0 | 0.014s |
| `app.presentation.ui.budget.PeriodEndSurplusHandoffTest` | :app (fossDebug) | 22 | 22 ✅ | 0 | 0 | 0.397s |
| `app.presentation.ui.budget.controller.EditorStateControllerTest` | :app (fossDebug) | 20 | 20 ✅ | 0 | 0 | 0.006s |
| `app.presentation.ui.budget.controller.NumpadControllerTest` | :app (fossDebug) | 25 | 25 ✅ | 0 | 0 | 0.005s |
| `app.presentation.ui.budget.controller.PeriodActionsControllerTest` | :app (fossDebug) | 11 | 11 ✅ | 0 | 0 | 0.002s |
| `app.presentation.ui.budget.controller.TransactionActionsControllerTest` | :app (fossDebug) | 12 | 12 ✅ | 0 | 0 | 0.035s |
| `app.presentation.ui.changelog.ChangelogHistoryViewModelTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.284s |
| `app.presentation.ui.editor.sheets.split.DynamicAllocationsTest` | :app (fossDebug) | 19 | 19 ✅ | 0 | 0 | 0.005s |
| `app.presentation.ui.editor.sheets.split.NextBlockAllocationsTest` | :app (fossDebug) | 13 | 13 ✅ | 0 | 0 | 0.002s |
| `app.presentation.ui.editor.sheets.split.SplitBudgetTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 0.001s |
| `app.presentation.ui.history.HistoryCalculationsTest` | :app (fossDebug) | 8 | 8 ✅ | 0 | 0 | 0.004s |
| `app.presentation.ui.history.HistoryViewModelTest` | :app (fossDebug) | 15 | 15 ✅ | 0 | 0 | 0.355s |
| `app.presentation.ui.home.MainScreenViewModelTest` | :app (fossDebug) | 12 | 12 ✅ | 0 | 0 | 0.118s |
| `app.presentation.ui.onboarding.OnboardingViewModelTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.093s |
| `app.presentation.ui.screenshot.AnalyticsScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 6.963s |
| `app.presentation.ui.screenshot.AnalyticsTabletScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.536s |
| `app.presentation.ui.screenshot.AppearanceOptionsScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.453s |
| `app.presentation.ui.screenshot.AutoResizeBasicTextFieldScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.228s |
| `app.presentation.ui.screenshot.AverageSpendCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.104s |
| `app.presentation.ui.screenshot.BudgetComponentScreenshotTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.550s |
| `app.presentation.ui.screenshot.BudgetDisplayScreenshotTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.391s |
| `app.presentation.ui.screenshot.BudgetGraphScreenshotTest` | :app (fossDebug) | 5 | 5 ✅ | 0 | 0 | 1.001s |
| `app.presentation.ui.screenshot.BudgetPeriodSheetInteractionScreenshotTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 10.375s |
| `app.presentation.ui.screenshot.BudgetPillPaletteNightScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.265s |
| `app.presentation.ui.screenshot.BudgetPillPaletteScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.439s |
| `app.presentation.ui.screenshot.BudgetPillScreenshotTest` | :app (fossDebug) | 10 | 10 ✅ | 0 | 0 | 5.726s |
| `app.presentation.ui.screenshot.CalendarHeatmapScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.158s |
| `app.presentation.ui.screenshot.CategoriesChartCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.102s |
| `app.presentation.ui.screenshot.CategoryAmountScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.093s |
| `app.presentation.ui.screenshot.CategoryAnalyticsScreenshotTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 1.048s |
| `app.presentation.ui.screenshot.ChangelogScreenshotTest` | :app (fossDebug) | 12 | 12 ✅ | 0 | 0 | 1.693s |
| `app.presentation.ui.screenshot.ComponentScreenshotTest` | :app (fossDebug) | 8 | 8 ✅ | 0 | 0 | 0.998s |
| `app.presentation.ui.screenshot.DayTotalItemScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.056s |
| `app.presentation.ui.screenshot.DaysLeftCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.077s |
| `app.presentation.ui.screenshot.DescriptionButtonScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.095s |
| `app.presentation.ui.screenshot.DetailedChartScreenshotTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 0.894s |
| `app.presentation.ui.screenshot.DonutChartScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.096s |
| `app.presentation.ui.screenshot.EditableNoteTagScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.131s |
| `app.presentation.ui.screenshot.EditorScreenshotTest` | :app (fossDebug) | 5 | 5 ✅ | 0 | 0 | 0.846s |
| `app.presentation.ui.screenshot.ExpenseItemExpandedContentScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.154s |
| `app.presentation.ui.screenshot.ExpenseItemScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.117s |
| `app.presentation.ui.screenshot.FeatureLabScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.399s |
| `app.presentation.ui.screenshot.FinishedPeriodHeaderScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.130s |
| `app.presentation.ui.screenshot.HintTipScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.072s |
| `app.presentation.ui.screenshot.HistoryDateDividerScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.047s |
| `app.presentation.ui.screenshot.HistoryScreenshotTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.440s |
| `app.presentation.ui.screenshot.MainScreenScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.442s |
| `app.presentation.ui.screenshot.MiddlePeriodHeaderScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.062s |
| `app.presentation.ui.screenshot.MinMaxSpentCardScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.291s |
| `app.presentation.ui.screenshot.MultiCategoryChartScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.289s |
| `app.presentation.ui.screenshot.NewCategoryTagScreenshotTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.176s |
| `app.presentation.ui.screenshot.NoTransactionsViewScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.108s |
| `app.presentation.ui.screenshot.NumberedRowScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.049s |
| `app.presentation.ui.screenshot.NumpadButtonScreenshotTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.331s |
| `app.presentation.ui.screenshot.NumpadScreenshotTest` | :app (fossDebug) | 4 | 4 ✅ | 0 | 0 | 0.692s |
| `app.presentation.ui.screenshot.PastPeriodsBottomSheetScreenshotTest` | :app (fossDebug) | 5 | 5 ✅ | 0 | 0 | 0.745s |
| `app.presentation.ui.screenshot.RecurrentPaymentsDividerScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.100s |
| `app.presentation.ui.screenshot.RecurrentTicketScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.078s |
| `app.presentation.ui.screenshot.RolloverDialogScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.178s |
| `app.presentation.ui.screenshot.SavingsRecommendationCardScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.368s |
| `app.presentation.ui.screenshot.ScratchDueTodayAccentTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.627s |
| `app.presentation.ui.screenshot.SettingsGroupScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.124s |
| `app.presentation.ui.screenshot.SettingsScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.398s |
| `app.presentation.ui.screenshot.SpendBudgetCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.081s |
| `app.presentation.ui.screenshot.SpendsChartScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.229s |
| `app.presentation.ui.screenshot.SpendsCountCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.075s |
| `app.presentation.ui.screenshot.StatCardScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.184s |
| `app.presentation.ui.screenshot.SubscriptionsScreenshotTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.528s |
| `app.presentation.ui.screenshot.SwipeActionsScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.084s |
| `app.presentation.ui.screenshot.SwipeToDismissScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.058s |
| `app.presentation.ui.screenshot.SwipeableExpenseItemScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.108s |
| `app.presentation.ui.screenshot.TextRowScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.115s |
| `app.presentation.ui.screenshot.TicketCardScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.131s |
| `app.presentation.ui.screenshot.TicketViewScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.066s |
| `app.presentation.ui.screenshot.TopSheetScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.144s |
| `app.presentation.ui.screenshot.UpcomingRecurrentItemRowScreenshotTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.128s |
| `app.presentation.ui.screenshot.WavyDividerScreenshotTest` | :app (fossDebug) | 1 | 1 ✅ | 0 | 0 | 0.069s |
| `app.presentation.ui.settings.SettingsViewModelTest` | :app (fossDebug) | 20 | 20 ✅ | 0 | 0 | 0.775s |
| `app.presentation.ui.settings.bugreport.BugReportViewModelTest` | :app (fossDebug) | 13 | 13 ✅ | 0 | 0 | 0.423s |
| `app.presentation.ui.subscriptions.SubscriptionsViewModelTest` | :app (fossDebug) | 10 | 10 ✅ | 0 | 0 | 0.073s |
| `app.presentation.ui.theme.component.SavingsCardMetricsTest` | :app (fossDebug) | 5 | 5 ✅ | 0 | 0 | 0.001s |
| `app.presentation.ui.theme.component.budget.BudgetGraphTest` | :app (fossDebug) | 2 | 2 ✅ | 0 | 0 | 0.000s |
| `app.presentation.ui.theme.component.budget.formula.BudgetFormulaTest` | :app (fossDebug) | 7 | 7 ✅ | 0 | 0 | 0.004s |
| `app.presentation.ui.tutorial.TutorialBoxStateTest` | :app (fossDebug) | 6 | 6 ✅ | 0 | 0 | 0.001s |
| `app.presentation.util.MarkdownParserTest` | :app (fossDebug) | 3 | 3 ✅ | 0 | 0 | 0.001s |
| `app.presentation.util.font.format.CurrencyFormatTest` | :app (fossDebug) | 13 | 13 ✅ | 0 | 0 | 0.015s |
| `sync.contract.WearSyncProtocolTest` | :sync-contract | 1 | 1 ✅ | 0 | 0 | 0.062s |

---

## 4. السجل التفصيلي لجميع الاختبارات الفردية (747 اختباراً)

| # | اسم الاختبار (Test Method) | حزمة الاختبار (Suite) | النتيجة | المدة |
| :---: | :--- | :--- | :---: | :---: |
| 1 | `parse_metadata_with_split_mode_returnsCorrectSettings` | `WafeerCsvParserTest` | ✅ PASSED | 0.043s |
| 2 | `parse_invalidAmount_discardsRow` | `WafeerCsvParserTest` | ✅ PASSED | 0.007s |
| 3 | `parse_legacyCommentAndNoteHeaders_mapToCategoryAndExtraNote` | `WafeerCsvParserTest` | ✅ PASSED | 0.001s |
| 4 | `parse_legacyHeaderWithoutNote_leavesNoteEmpty` | `WafeerCsvParserTest` | ✅ PASSED | 0.001s |
| 5 | `parse_readsCategoryAndExtraNoteColumns` | `WafeerCsvParserTest` | ✅ PASSED | 0.001s |
| 6 | `parse_validRows_returnsRows` | `WafeerCsvParserTest` | ✅ PASSED | 0.001s |
| 7 | `the header names the category and extra note columns, not comment` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.050s |
| 8 | `a recurrent transaction keeps its frequency, subscription day and end date` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.004s |
| 9 | `comments with commas and quotes are escaped and read back verbatim` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 10 | `the extra note survives the round trip, including commas and quotes` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.002s |
| 11 | `a plain transaction survives the round trip field for field` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.002s |
| 12 | `a non-positive amount is rejected on import` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.002s |
| 13 | `formula injection triggers are escaped in exported CSV and restored upon import` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.004s |
| 14 | `an archived budget survives the round trip` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 15 | `metadata with no end date comes back with the derived period end date` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.002s |
| 16 | `a transaction with no date is dropped on export` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 17 | `amount scale is preserved exactly` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.002s |
| 18 | `transactions keep their original order` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 19 | `backup metadata survives the round trip` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 20 | `credit flags survive the round trip` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 21 | `a transaction without a note comes back with an empty note` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 22 | `a full backup with metadata, an archive and transactions all come back together` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 23 | `seconds on a transaction date are truncated to the minute` | `WafeerCsvRoundTripTest` | ✅ PASSED | 0.001s |
| 24 | `rows with id zero are inserted fresh, never upserted` | `WafeerCsvServiceTest` | ✅ PASSED | 1.545s |
| 25 | `getExportFileName falls back to the default when there is no budget` | `WafeerCsvServiceTest` | ✅ PASSED | 0.097s |
| 26 | `the extra note is carried onto each imported transaction` | `WafeerCsvServiceTest` | ✅ PASSED | 0.015s |
| 27 | `a backup exported before the header rename still restores category and extra note` | `WafeerCsvServiceTest` | ✅ PASSED | 0.012s |
| 28 | `a backup without metadata leaves settings and onboarding untouched` | `WafeerCsvServiceTest` | ✅ PASSED | 0.098s |
| 29 | `a backup with no archives does not call upsertArchivedBudgets` | `WafeerCsvServiceTest` | ✅ PASSED | 0.009s |
| 30 | `each distinct non-blank comment resolves a category exactly once` | `WafeerCsvServiceTest` | ✅ PASSED | 0.015s |
| 31 | `blank comments do not trigger category resolution and leave the category unset` | `WafeerCsvServiceTest` | ✅ PASSED | 0.010s |
| 32 | `the resolved category id is attached to each imported transaction` | `WafeerCsvServiceTest` | ✅ PASSED | 0.007s |
| 33 | `getExportFileName builds the name from the period sequence and its dates` | `WafeerCsvServiceTest` | ✅ PASSED | 0.024s |
| 34 | `a backup mixing kept and fresh rows routes each to the correct path` | `WafeerCsvServiceTest` | ✅ PASSED | 0.009s |
| 35 | `rows with a real id are upserted with that id preserved and never re-inserted` | `WafeerCsvServiceTest` | ✅ PASSED | 0.009s |
| 36 | `the result reports valid rows as imported and rejected rows as discarded` | `WafeerCsvServiceTest` | ✅ PASSED | 0.007s |
| 37 | `archived budgets in the backup are upserted` | `WafeerCsvServiceTest` | ✅ PASSED | 0.009s |
| 38 | `exportAllTransactions writes a metadata row and every transaction` | `WafeerCsvServiceTest` | ✅ PASSED | 0.161s |
| 39 | `metadata applies the budget settings, current period and marks onboarding complete` | `WafeerCsvServiceTest` | ✅ PASSED | 0.036s |
| 40 | `createCategory leaves an existing visible category untouched` | `BudgetRepositoryImplCategoryTest` | ✅ PASSED | 0.408s |
| 41 | `createCategory inserts an unused visible category` | `BudgetRepositoryImplCategoryTest` | ✅ PASSED | 0.005s |
| 42 | `createCategory restores a hidden category instead of duplicating it` | `BudgetRepositoryImplCategoryTest` | ✅ PASSED | 0.008s |
| 43 | `localizeDefaultCategoriesAndDemoData translates default categories between Arabic and English` | `BudgetRepositoryImplCategoryTest` | ✅ PASSED | 0.022s |
| 44 | `setEarlyFinishActive writes all three fields and clearEarlyFinish resets them` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.024s |
| 45 | `theme mode round-trips` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 46 | `observeSettings reflects a write made after the first read` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 47 | `getRemainingFromLastPeriod on an empty store is zero` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 48 | `a corrupt stored rollover strategy reads back as a null strategy` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 49 | `last seen version code round-trips and resets` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 50 | `a corrupt stored budget split view period reads back as null` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 51 | `current period rollover round-trips amount and carry-forward` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 52 | `arbitrary string keys round-trip` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 53 | `resetTutorials clears the tutorial completion flags` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 54 | `clearPendingRollover removes both keys` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 55 | `getPendingRollover on an empty store is zero with no strategy` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 56 | `budget split view period round-trips` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.001s |
| 57 | `savings preferences round-trip, and null goal fields are removed` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 58 | `a corrupt stored theme mode falls back to SYSTEM` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.001s |
| 59 | `last period snapshot round-trips and clears` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 60 | `an empty store yields the documented default settings` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 61 | `toggling a scanned package flips only that package in the stored set` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.003s |
| 62 | `setBudgetEndDate with a value then null toggles the key` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 63 | `pending rollover round-trips amount and strategy` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.001s |
| 64 | `toggling a scanned package reads the persisted set, not a stale copy` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 65 | `setCurrentPeriod is observable via the boundary flow and the id getter` | `SettingsRepositoryImplTest` | ✅ PASSED | 0.002s |
| 66 | `calculateAffordability_safeVerdict` | `SmartToolsRepositoryTest` | ✅ PASSED | 0.190s |
| 67 | `calculateAffordability_cautionVerdict_whenSevereImpactOnAllowance` | `SmartToolsRepositoryTest` | ✅ PASSED | 0.008s |
| 68 | `calculateAffordability_dangerousVerdict_whenExceedsBudget` | `SmartToolsRepositoryTest` | ✅ PASSED | 0.007s |
| 69 | `depositToGoal_updatesSavedAmountAndMarksCompleted` | `SmartToolsRepositoryTest` | ✅ PASSED | 0.026s |
| 70 | `addGoal_insertsEntityProperly` | `SmartToolsRepositoryTest` | ✅ PASSED | 0.014s |
| 71 | `isVersionNewer_detectsStrictlyNewerVersions` | `AppUpdateManagerTest` | ✅ PASSED | 0.003s |
| 72 | `isVersionNewer_rejectsOlderOrEqualVersions` | `AppUpdateManagerTest` | ✅ PASSED | 0.001s |
| 73 | `parseVersionCode_computesCorrectCode` | `AppUpdateManagerTest` | ✅ PASSED | 0.000s |
| 74 | `malformed expressions return null` | `EvaluatorTest` | ✅ PASSED | 0.008s |
| 75 | `equal precedence operators evaluate left to right` | `EvaluatorTest` | ✅ PASSED | 0.000s |
| 76 | `leading unary sign only negates the first operand` | `EvaluatorTest` | ✅ PASSED | 0.001s |
| 77 | `testBudgetExpressionEvaluator` | `EvaluatorTest` | ✅ PASSED | 0.000s |
| 78 | `testEditorCalculations` | `EvaluatorTest` | ✅ PASSED | 0.000s |
| 79 | `multiplication and division bind tighter than addition and subtraction` | `EvaluatorTest` | ✅ PASSED | 0.001s |
| 80 | `calculateMonthlyEquivalent_normalizesEachFrequencyToAMonthlyAmount` | `RecurringExpenseCalculatorTest` | ✅ PASSED | 0.002s |
| 81 | `monthlyRecurring_usesSubscriptionDay` | `RecurringExpenseCalculatorTest` | ✅ PASSED | 0.000s |
| 82 | `calculateMonthlyEquivalent_emptyListReturnsZero` | `RecurringExpenseCalculatorTest` | ✅ PASSED | 0.001s |
| 83 | `weeklyRecurring_dueOnSeventhDay_returnsTrue` | `RecurringExpenseCalculatorTest` | ✅ PASSED | 0.000s |
| 84 | `mapDate_calendarBucket_usesCalendarWeekStartMonday` | `TransactionPeriodMapperTest` | ✅ PASSED | 0.001s |
| 85 | `mapDate_activeBudget_usesBudgetAlignedWeek` | `TransactionPeriodMapperTest` | ✅ PASSED | 0.001s |
| 86 | `test calculatePaymentDueDate - normal case` | `CreditCardTest` | ✅ PASSED | 0.001s |
| 87 | `test calculatePaymentDueDate - sunday adjustment` | `CreditCardTest` | ✅ PASSED | 0.000s |
| 88 | `test calculatePaymentDueDate - saturday adjustment` | `CreditCardTest` | ✅ PASSED | 0.000s |
| 89 | `test calculatePaymentDueDate - transition to next month` | `CreditCardTest` | ✅ PASSED | 0.000s |
| 90 | `tapping Custom from a named preset flips the tag without changing the percentages` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 91 | `spendingCeilingPct equals 80 for the default 50-30-20 split` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 92 | `SavingsSplitPreset fromValues returns CUSTOM for any other triple` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 93 | `SavingsSplitPreset fromValues never returns CUSTOM for known splits` | `SavingsPreferencesTest` | ✅ PASSED | 0.001s |
| 94 | `default split is the classic 50-30-20 rule` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 95 | `projectedPerPeriod returns null for negative months` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 96 | `tapping a named preset from Custom overrides any user-tuned percentages` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 97 | `projectedPerPeriod divides the goal across the number of months` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 98 | `projectedPerPeriod returns null when goal amount is missing` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 99 | `projectedPerPeriod returns null for zero months` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 100 | `CUSTOM preset allows arbitrary percentages` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 101 | `default needs-wants-savings percentages are derived from CONSERVATIVE preset` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 102 | `fromPreset defaults to no goal when goal fields are not provided` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 103 | `default needs-wants-savings percentages are derived from the BALANCED preset` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 104 | `projectedPerPeriod returns null for zero or negative goal amount` | `SavingsPreferencesTest` | ✅ PASSED | 0.001s |
| 105 | `fromPreset builds preferences with the preset's values and keeps goal independent` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 106 | `default needs-wants-savings percentages are derived from AGGRESSIVE_SAVER preset` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 107 | `preset tag is preserved regardless of whether the percentages match the preset` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 108 | `fromPreset round-trips through preset name` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 109 | `projectedPerPeriod rounds half-up to 2 decimal places` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 110 | `spendingCeilingPct is needs plus wants` | `SavingsPreferencesTest` | ✅ PASSED | 0.001s |
| 111 | `projectedPerPeriod returns null when goal months is missing` | `SavingsPreferencesTest` | ✅ PASSED | 0.000s |
| 112 | `accepts_iso_code_glued_to_the_amount` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.004s |
| 113 | `parses_dot_decimal_and_comma_grouping` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 114 | `ignores_income_sign` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 115 | `shared_symbol_is_evidence_for_the_budget_currency` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 116 | `ignores_numbers_far_from_currency_evidence` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 117 | `treats_three_trailing_digits_as_grouping` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 118 | `keeps_negative_sign_as_spend` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 119 | `parses_comma_decimal_and_dot_grouping` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 120 | `ignores_iso_code_embedded_in_a_longer_word` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 121 | `ignores_zero_amounts` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 122 | `ignores_denied_words` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 123 | `parses_iso_code_after_amount` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 124 | `parses_unique_symbol_before_amount` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 125 | `parses_mercado_pago_purchase_notification` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 126 | `shared_symbol_is_not_evidence_when_another_currency_code_is_named` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.000s |
| 127 | `ignores_numbers_without_currency_evidence` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 128 | `shared_symbol_with_iso_code_is_evidence` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 129 | `ignores_percentages` | `ExpenseNotificationParserTest` | ✅ PASSED | 0.001s |
| 130 | `when the period ended over budget then the user is routed to analytics only and nothing is queued` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.047s |
| 131 | `reopenUnresolvedSurplusDialog does nothing once a strategy has already been decided` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.046s |
| 132 | `resolveUnresolvedSurplus applies ADD_TO_FIRST_DAY immediately, dated today, when a new period is already active` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.052s |
| 133 | `when a period ends with SPLIT_EQUALLY then a rollover is queued and analytics is the only stop` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.011s |
| 134 | `when a period was finished early and a stale midnight-transition flag is set then it gets cleared` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.021s |
| 135 | `an early finish with ASK_ALWAYS shows the dialog for a period ending today` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.028s |
| 136 | `confirming the dialog clears its state without queuing a rollover` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.014s |
| 137 | `resolveUnresolvedSurplus is a no-op when there is nothing pending` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.014s |
| 138 | `when a period elapsed naturally (no early finish) then it is still flagged as ended` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.013s |
| 139 | `reopenUnresolvedSurplusDialog does nothing when there is nothing pending` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.006s |
| 140 | `when a period ends naturally with a cached midnight snapshot then totalSpent reflects the actual spend` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.121s |
| 141 | `when the period ended over budget then it is also marked as already handled` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.027s |
| 142 | `when a period ends naturally without a cached snapshot then totalSpent is computed fresh, not a flat totalBudget placeholder` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.013s |
| 143 | `reopenUnresolvedSurplusDialog re-shows the dialog leanly from the persisted amount` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.008s |
| 144 | `when a period was finished early then it is never flagged as ended, even past the original schedule` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.009s |
| 145 | `when a period ends with ADD_TO_FIRST_DAY then the surplus is queued for the first day` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.011s |
| 146 | `when a period end was already surfaced then it is never re-flagged as ended, even past the schedule` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.010s |
| 147 | `when the period ended over budget then totalSpent reflects the actual overspend, not a flat totalBudget placeholder` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.014s |
| 148 | `when a period ends with ASK_ALWAYS then the dialog is shown, nothing is queued, and the surplus is marked unresolved` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.012s |
| 149 | `resolveUnresolvedSurplus queues ADD_TO_FIRST_DAY for next boundary when no new period is active yet` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.013s |
| 150 | `an early finish with nothing remaining does nothing` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.006s |
| 151 | `when a natural end is processed then periodEndAlreadyHandled is marked so the next app foreground won't re-trigger it` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.021s |
| 152 | `the transition data exposes the finished period's window, budget and currency` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.008s |
| 153 | `resolveUnresolvedSurplus applies SPLIT_EQUALLY immediately when a new period is already active` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.015s |
| 154 | `resolveUnresolvedSurplus queues SPLIT_EQUALLY for next boundary when no new period is active yet` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.012s |
| 155 | `an early finish with SPLIT_EQUALLY queues the surplus immediately without a dialog` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.006s |
| 156 | `when a period is still active (no early finish, end date in the future) then it is not flagged as ended` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.011s |
| 157 | `resolveUnresolvedSurplus with a null strategy discards without touching settings` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.012s |
| 158 | `dismissing the dialog leaves the surplus persisted as unresolved, not discarded` | `MidnightPeriodCheckerTest` | ✅ PASSED | 0.010s |
| 159 | `slices orphaned transactions into real weekly periods and reassigns their periodId` | `BackfillOrphanedPeriodsUseCaseOrchestrationTest` | ✅ PASSED | 0.020s |
| 160 | `already backfilled is a no-op` | `BackfillOrphanedPeriodsUseCaseOrchestrationTest` | ✅ PASSED | 0.004s |
| 161 | `no settings yet defers without marking done` | `BackfillOrphanedPeriodsUseCaseOrchestrationTest` | ✅ PASSED | 0.007s |
| 162 | `never touches a window that overlaps an already-real archived period` | `BackfillOrphanedPeriodsUseCaseOrchestrationTest` | ✅ PASSED | 0.014s |
| 163 | `windows walk backward in fixed-length steps aligned to the current period start` | `BackfillOrphanedPeriodsUseCaseTest` | ✅ PASSED | 0.000s |
| 164 | `windows stop as soon as the earliest orphaned date is covered, no extra trailing window` | `BackfillOrphanedPeriodsUseCaseTest` | ✅ PASSED | 0.000s |
| 165 | `when current version is greater than lastSeen then decision is Show` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.006s |
| 166 | `when current versionCode is one and lastSeen is null then Show (bootstrap first launch)` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 167 | `when current version is far above lastSeen then decision is Show` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 168 | `when current version is exactly lastSeen plus one then decision is Show` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.001s |
| 169 | `when current versionCode equals lastSeen at Int MAX boundary then Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 170 | `when lastSeen is null then decision is Show (bootstrap from pre-gate build)` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 171 | `when current version code is zero then decision is Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 172 | `when latest release is null then decision is Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.001s |
| 173 | `when current version code is negative then decision is Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 174 | `when current version equals lastSeen then decision is Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 175 | `when current version is less than lastSeen then decision is Skip` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 176 | `when Show is returned it carries the same release instance passed in` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 177 | `when current versionCode is at Int MAX and lastSeen is one less then Show` | `ChangelogTriggerEvaluatorTest` | ✅ PASSED | 0.000s |
| 178 | `a live add-to-today period stored before the fold gets its carry added to the total, once` | `FoldCarryIntoTotalBudgetUseCaseTest` | ✅ PASSED | 0.014s |
| 179 | `a spread-across-days period is already folded and is left alone` | `FoldCarryIntoTotalBudgetUseCaseTest` | ✅ PASSED | 0.008s |
| 180 | `once marked done it never touches the settings again` | `FoldCarryIntoTotalBudgetUseCaseTest` | ✅ PASSED | 0.007s |
| 181 | `allowedSystemActions_rejectsUnauthorizedOrArbitraryActions` | `NotificationRescheduleReceiverTest` | ✅ PASSED | 0.004s |
| 182 | `allowedSystemActions_containsExpectedSystemEvents` | `NotificationRescheduleReceiverTest` | ✅ PASSED | 0.000s |
| 183 | `weekly notification fires on the first future occurrence at the configured time` | `NotificationSchedulerTest` | ✅ PASSED | 0.392s |
| 184 | `no occurrence for a transaction without a date` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 185 | `stepped occurrence is the start date when today is on or before it` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 186 | `no occurrence for a transaction without a recurrence frequency` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 187 | `credit cutoff reminder moves to the next cycle once today's slot has passed` | `NotificationSchedulerTest` | ✅ PASSED | 0.003s |
| 188 | `monthly occurrence is this month when the billing day is still ahead` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 189 | `credit cutoff reminder fires three days before the due date of the last cutoff` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 190 | `biweekly stepped occurrence lands on the next fourteen-day multiple` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 191 | `monthly billing day on the last day of a 31-day month resolves to that day` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 192 | `monthly occurrence never precedes the recurrence start date` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 193 | `no occurrence once the next one would fall after the recurrence end date` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 194 | `monthly occurrence rolls to next month when the billing day has passed` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 195 | `an occurrence whose notification time has already passed is skipped to the next one` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 196 | `weekly stepped occurrence lands on the next seven-day multiple` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 197 | `monthly notification uses the subscription day, not the original transaction day` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 198 | `lead days move the trigger ahead of the occurrence and roll past a lead already gone` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 199 | `monthly billing day is clamped to the length of the target month` | `NotificationSchedulerTest` | ✅ PASSED | 0.004s |
| 200 | `occurrences already marked paid are skipped` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 201 | `credit cutoff reminder skips a due date that already passed and rolls to the next cycle` | `NotificationSchedulerTest` | ✅ PASSED | 0.002s |
| 202 | `monthly occurrence is today when the billing day is today` | `NotificationSchedulerTest` | ✅ PASSED | 0.001s |
| 203 | `initial state is loading then success when data emits` | `AnalyticsViewModelTest` | ✅ PASSED | 0.325s |
| 204 | `closing historical view returns to current` | `AnalyticsViewModelTest` | ✅ PASSED | 0.027s |
| 205 | `deleting a one-time expense in a past period removes the row` | `AnalyticsViewModelTest` | ✅ PASSED | 0.022s |
| 206 | `onClose without selected period triggers NavigateToMain effect` | `AnalyticsViewModelTest` | ✅ PASSED | 0.017s |
| 207 | `deleting a projected recurring charge skips that occurrence instead of deleting the subscription` | `AnalyticsViewModelTest` | ✅ PASSED | 0.020s |
| 208 | `selecting historical period updates display state` | `AnalyticsViewModelTest` | ✅ PASSED | 0.110s |
| 209 | `paid recurring occurrence is not double-counted in historical period spend total` | `AnalyticsViewModelTest` | ✅ PASSED | 0.044s |
| 210 | `archived period total in the list is recomputed from live rows, not the stored snapshot` | `AnalyticsViewModelTest` | ✅ PASSED | 0.021s |
| 211 | `reconstructed virtual period total matches what opening it recomputes` | `AnalyticsViewModelTest` | ✅ PASSED | 0.367s |
| 212 | `onCreateNewPeriod clears early finish and navigates` | `AnalyticsViewModelTest` | ✅ PASSED | 0.023s |
| 213 | `displayState categories includes hidden categories, unlike active-only list` | `AnalyticsViewModelTest` | ✅ PASSED | 0.016s |
| 214 | `an overspend eats the waiting money first, without asking` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 215 | `unanswered prompts stack up day after day instead of expiring` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 216 | `a debt that outlives one day keeps being taken from the days that follow` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 217 | `spread on the last day is the same as carrying, there is nowhere else to put it` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 218 | `spreading after skipping days shares the whole pile, not only yesterday` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 219 | `once the period is over everything still waiting is folded into what is left` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 220 | `an answer recorded before the period started is ignored` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 221 | `mixing spread then carry gives today the raised rate plus what was waiting` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 222 | `carry leaves the daily rate alone, tomorrow is a normal day again` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 223 | `answering after skipping days settles everything that piled up, not only yesterday` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 224 | `a prompt raised on the last day can still be answered` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 225 | `an overspend bigger than the waiting money comes out of today, whatever the user would have picked` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 226 | `a budget that does not divide evenly keeps its cents through a spread` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 227 | `no prompt is raised while the user is still paying off a debt` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 228 | `a rollover applied mid-period only lands on the day it was applied` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 229 | `mixing carry then spread shares only what is still unspent` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 230 | `nothing is pending on day one, there is no yesterday to ask about` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 231 | `carry spends only what today actually costs, the rest returns to the prompt tomorrow` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 232 | `carrying several days in a row walks the whole surplus forward` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 233 | `an untouched day turns into a pending prompt the next morning` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 234 | `spread reports the grown rate back as the split budget so the period pill adds up` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 235 | `an answer given on a past day still shapes what today looks like` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 236 | `an answer recorded for a day the user never reached does nothing yet` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 237 | `a transaction dated later in the period is charged against today, not against its own day` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 238 | `spreading twice compounds onto the already raised rate` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 239 | `the period totals are untouched by whatever the user answers` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 240 | `carry hands the whole pending amount to today and clears the prompt` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 241 | `a spread on day two is still paying out on day six` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 242 | `what is left after the end is the same whatever the user answered along the way` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 243 | `the rollover is kept out of the daily rate, it is a one-off not a raise` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 244 | `an unanswered prompt never inflates today's allowance` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 245 | `a first-day rollover is offered on day one and joins the prompt if it is not spent` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 246 | `spreading later in the period divides over fewer days, so each day gets more` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.001s |
| 247 | `spread raises the daily rate for the rest of the period instead of today alone` | `AskMeLeftoverChoiceTest` | ✅ PASSED | 0.000s |
| 248 | `when starting a new period after finishing early then the surplus is carried into the new total budget` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.166s |
| 249 | `when Analytics clears the early-finish flag before the new period is saved then the surplus still carries over` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.029s |
| 250 | `when starting a new period the way the real UI does it (no explicit force, only a changed start date) then the surplus still carries over` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.023s |
| 251 | `when finishing early with nothing left then no rollover is queued and no dialog is shown` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.020s |
| 252 | `an undecided pending surplus survives a period boundary untouched, instead of being silently cleared` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.020s |
| 253 | `when starting a new period with add-to-today then the carry is folded into the total and offered on the first live day` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.030s |
| 254 | `when the user just edits the active budget mid-period (same start date, no force) then nothing is archived and no rollover applies` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.020s |
| 255 | `when a genuinely new period starts then earlyFinishActive and periodEndAlreadyHandled are cleared` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.017s |
| 256 | `archiving a period includes a recurring charge projected from before it started, matching what reopening it would show` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.023s |
| 257 | `when finishing early with split-equally strategy then remaining balance is queued as a pending rollover` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.022s |
| 258 | `when the next period starts on the day the previous one ended then that day belongs to the new period only` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.021s |
| 259 | `the rollover queued on early finish is the same number the archived period reports as saved` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.021s |
| 260 | `when the user edits the dates of a live period then it stays the same period and keeps its rollover` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.028s |
| 261 | `when archiving a period finished early then the snapshot uses the actual finish date not the original schedule` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.019s |
| 262 | `when finishing early with ask-always strategy then nothing is queued and the rollover dialog is requested instead` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.016s |
| 263 | `when the user edits the active budget mid-period while earlyFinishActive is true then it is not silently cleared` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.016s |
| 264 | `when finishing early with ask-always and the user picks a strategy then the surplus is queued for the next period, not added back to the finished one` | `BudgetPeriodManagerTest` | ✅ PASSED | 0.019s |
| 265 | `an unpaid recurring occurrence still counts as projected spend today` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 266 | `carry over - unspent days add up and overspending comes out of the next day` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.002s |
| 267 | `static and dynamic produce different daily budgets for the same data` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 268 | `spending the day-one carry does not put the rest of the period over budget, and an unspent carry stays in the total` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 269 | `dynamic split - daily budget is remaining divided by days remaining` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 270 | `rollOverAppliedDate falls back to startDate when not set, matching the previous behavior` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 271 | `allowance days - Sun to Thu active days allocate budget only on active days and zero on Fri and Sat` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 272 | `a recurring templates own row never counts toward period totals, only its projection or a materialized transaction does` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 273 | `ask me - overspending first uses the waiting money, the rest comes out of today` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 274 | `reserveUpcomingCharges counts a subscription due later in the period from day one` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 275 | `marking a recurring occurrence paid on its due date does not double-count todays spend` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 276 | `static split - daily budget ignores how much was spent and how many days are left` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 277 | `allowance days - 1 active day per week rolls over strictly to the next active day` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 278 | `rollOverLimit via rollOverAppliedDate is added to remainingToday only on its target date, not on startDate` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 279 | `dynamic split - daily budget goes up when the day rolls over and nothing is spent` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 280 | `an unpaid subscription charge due earlier in the period counts as spent, even when its template belongs to an older period` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 281 | `static split - daily budget is totalBudget divided by period days` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 282 | `dynamic split - daily budget is zero when the remaining balance is non-positive` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 283 | `dynamic split - daily budget is zero when no days remain` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 284 | `ask me - unused money waits for a choice, then carries to today or spreads over the days left` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.001s |
| 285 | `carry over - an unspent first-day surplus keeps carrying` | `BudgetStateCalculatorTest` | ✅ PASSED | 0.000s |
| 286 | `marking paid reschedules the series notification past the paid date` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.074s |
| 287 | `editing a transaction with zero amount fails` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.007s |
| 288 | `marking a virtual occurrence paid resolves back to the real template via sourceTransactionId` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.007s |
| 289 | `marking a future occurrence paid records the scheduled date as handled` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.007s |
| 290 | `editing an income transaction with negative amount succeeds` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.008s |
| 291 | `marking an occurrence paid creates a real transaction dated today, not the scheduled date` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.006s |
| 292 | `the materialized transaction is not itself recurring and has no link back to the template` | `BudgetTransactionHandlerTest` | ✅ PASSED | 0.006s |
| 293 | `when_restore_transaction_tapped_intent_is_processed_then_handler_is_called_with_transaction` | `BudgetViewModelTest` | ✅ PASSED | 0.717s |
| 294 | `when_input_ends_with_an_operator_then_numpad_draft_amount_is_null` | `BudgetViewModelTest` | ✅ PASSED | 0.024s |
| 295 | `when_dismiss_recurrent_dialog_intent_is_processed_then_dialog_is_hidden` | `BudgetViewModelTest` | ✅ PASSED | 0.115s |
| 296 | `when_apply_intent_results_in_added_then_numpad_input_and_comment_are_cleared` | `BudgetViewModelTest` | ✅ PASSED | 0.041s |
| 297 | `when_restore_transaction_handler_returns_failure_then_show_message_effect_is_emitted` | `BudgetViewModelTest` | ✅ PASSED | 0.033s |
| 298 | `when_amount_is_prefixed_with_plus_then_numpad_draft_amount_is_negative_income` | `BudgetViewModelTest` | ✅ PASSED | 0.026s |
| 299 | `when_equals_is_tapped_on_valid_expression_then_input_becomes_evaluation_result` | `BudgetViewModelTest` | ✅ PASSED | 0.021s |
| 300 | `when_set_lock_draggable_system_intent_is_processed_then_lock_draggable_is_set` | `BudgetViewModelTest` | ✅ PASSED | 0.018s |
| 301 | `when_viewmodel_is_created_then_ui_state_has_default_values` | `BudgetViewModelTest` | ✅ PASSED | 0.015s |
| 302 | `when_dismiss_credit_cutoff_dialog_intent_is_processed_then_dialog_and_credit_are_cleared` | `BudgetViewModelTest` | ✅ PASSED | 0.015s |
| 303 | `when_number_tapped_intent_is_processed_then_numpad_input_is_updated` | `BudgetViewModelTest` | ✅ PASSED | 0.015s |
| 304 | `when_apply_intent_results_in_show_recurrent_dialog_then_dialog_and_pending_values_are_set` | `BudgetViewModelTest` | ✅ PASSED | 0.028s |
| 305 | `when_delete_transaction_handler_returns_failure_then_show_message_effect_is_emitted` | `BudgetViewModelTest` | ✅ PASSED | 0.018s |
| 306 | `when_amount_is_prefixed_with_minus_then_numpad_draft_amount_is_a_positive_decrease` | `BudgetViewModelTest` | ✅ PASSED | 0.013s |
| 307 | `when_operator_is_tapped_then_input_gains_operator_and_calculation_mode_is_set` | `BudgetViewModelTest` | ✅ PASSED | 0.014s |
| 308 | `when_set_lock_swipeable_system_intent_is_processed_then_lock_swipeable_is_set` | `BudgetViewModelTest` | ✅ PASSED | 0.012s |
| 309 | `when_delete_transaction_tapped_intent_is_processed_then_handler_is_called_with_transaction` | `BudgetViewModelTest` | ✅ PASSED | 0.017s |
| 310 | `when_plain_amount_is_typed_then_numpad_draft_amount_is_the_positive_value` | `BudgetViewModelTest` | ✅ PASSED | 0.017s |
| 311 | `when_reset_input_is_tapped_then_input_is_cleared_and_calculation_is_reset` | `BudgetViewModelTest` | ✅ PASSED | 0.017s |
| 312 | `when_edit_transaction_tapped_intent_is_processed_then_handler_is_called_with_updated_transaction` | `BudgetViewModelTest` | ✅ PASSED | 0.018s |
| 313 | `when_apply_intent_results_in_queued_for_next_period_then_show_message_effect_is_emitted` | `BudgetViewModelTest` | ✅ PASSED | 0.020s |
| 314 | `when_mark_first_launch_complete_is_called_then_is_first_launch_becomes_false_and_use_case_is_invoked` | `BudgetViewModelTest` | ✅ PASSED | 0.019s |
| 315 | `dynamic re-slices what is left over the days that remain instead of banking it` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 316 | `static ignores the unused allowance of earlier days - today is always a fresh daily budget` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 317 | `today's spend comes off today's allowance in every mode` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 318 | `switching ask me to carry over mid-period settles the pending prompt onto today` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 319 | `switching static to dynamic mid-period raises the daily budget without giving today a lump sum` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 320 | `switching carry over to ask me mid-period moves the banked surplus into the pending prompt` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 321 | `ask me parks the unused 200 as a pending leftover and leaves today untouched until the user decides` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 322 | `income received on an earlier day is parked behind the ask me prompt, not handed to today` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 323 | `switching carry over back to static mid-period drops the banked surplus, it is not spendable any more` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 324 | `an unspent first-day rollover is banked by carry over, parked by ask me and dropped by static` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 325 | `switching static to carry over mid-period retroactively releases every unused day` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 326 | `once the period is over dynamic stops offering a daily budget while the other modes report the leftover` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 327 | `ask me with carry picked every day is the same reading as carry over` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 328 | `income received today lands on today in every mode and lifts the period total` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 329 | `switching to static after blowing the whole budget still reports the period as over budget` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.002s |
| 330 | `switching ask me to static mid-period discards the pending prompt entirely` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 331 | `switching to ask me after an overspend never parks a debt as a pending prompt, it is taken today` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 332 | `no mode switch changes today's spend, even when today already has transactions` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 333 | `switching static to ask me mid-period surfaces every skipped day at once as one pending prompt` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 334 | `a first-day rollover survives every mode switch in the period total` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 335 | `ask me with spread picked today lands on the same daily budget as dynamic` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 336 | `an ask me choice made before the switch away still applies when the user switches back` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 337 | `a mid-period rollover only shows up from the day it was applied, whatever the mode` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 338 | `on the last day carry over and ask me hand over everything that is left, static still shows one day` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 339 | `a debt larger than one day keeps eating into the following days in carry over and ask me` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 340 | `a one-day period reads the same in every mode` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 341 | `carry over hands the unused 200 straight to today` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 342 | `yesterday's overspend follows the user in carry over and ask me, but not in static` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 343 | `switching ask me to static then back to ask me restores the pending prompt, nothing was consumed` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 344 | `no mode switch changes the money actually spent, the period total or the days left` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.001s |
| 345 | `on day one every mode agrees, there is no surplus to argue about yet` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 346 | `switching carry over to dynamic mid-period spreads the surplus instead of banking it on today` | `DailySurplusSplitModeChangeTest` | ✅ PASSED | 0.000s |
| 347 | `a day one lump left unspent becomes the first ask me prompt` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.035s |
| 348 | `finishing early with ask me chosen asks about the balance left on the day it was stopped` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.014s |
| 349 | `a spread surplus raises the rate of an ask me period instead of waiting behind a prompt` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 350 | `answering spread it makes the next period's daily budget bigger from day one` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.016s |
| 351 | `with day one lump chosen up front the surplus lands on the first day of the next period` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 352 | `the queued surplus is consumed once, a third period starts clean` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.017s |
| 353 | `the amount folded into the new period is recomputed at the boundary, not the number shown in the dialog` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.016s |
| 354 | `with spread it chosen up front the surplus is queued silently and the user is only shown analytics` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.017s |
| 355 | `never answering leaves the surplus out of the new period, but does not throw it away` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.016s |
| 356 | `the surplus is what the period had left, whatever split mode it ran on` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.018s |
| 357 | `answering give it to me now puts the whole surplus on day one without raising the daily budget` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.027s |
| 358 | `ending a period twice in a row does not hand the surplus over twice` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.019s |
| 359 | `a surplus left unanswered can still be claimed once the new period is running` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.021s |
| 360 | `discarding the surplus leaves the next period with exactly what the user typed` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.025s |
| 361 | `a day one lump left unspent is banked by a carry over period` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.016s |
| 362 | `finishing early and answering give it to me now starts the next period with the lump on day one` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.017s |
| 363 | `a period that ended exactly on budget hands nothing over` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.017s |
| 364 | `a period that ended over budget hands nothing over and routes the user to analytics` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 365 | `the debt of an overspent period is not carried into the next one` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 366 | `a period that ends with money left asks the user, and queues nothing on its own` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.014s |
| 367 | `a dynamic period simply starts from the bigger total` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 368 | `the lump sum is only offered on day one, it does not repeat every day` | `PeriodEndSurplusHandoffTest` | ✅ PASSED | 0.015s |
| 369 | `when_date_selected_is_processed_then_selected_date_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.001s |
| 370 | `when_apply_credit_cutoff_day_is_called_then_dialog_is_hidden_and_credit_is_enabled` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 371 | `when_set_credit_enabled_true_with_no_cutoff_day_then_credit_flag_and_dialog_are_both_set` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 372 | `when_set_anim_state_is_processed_then_anim_state_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.001s |
| 373 | `when_set_credit_enabled_true_with_cutoff_day_then_credit_flag_is_set_but_dialog_is_not_shown` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 374 | `when_dismiss_credit_cutoff_dialog_is_processed_then_dialog_and_credit_flag_are_cleared` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 375 | `when_set_recurrent_enabled_true_while_wallet_enabled_then_wallet_is_disabled` | `EditorStateControllerTest` | ✅ PASSED | 0.002s |
| 376 | `when_set_credit_enabled_true_while_wallet_enabled_then_wallet_is_disabled` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 377 | `when_set_recurrent_enabled_false_is_processed_then_flag_is_cleared` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 378 | `when_apply_recurrent_dialog_is_called_then_dialog_amount_comment_and_toggles_are_cleared` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 379 | `when_set_lock_draggable_is_processed_then_lock_draggable_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 380 | `when_set_edit_mode_is_processed_then_edit_mode_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.001s |
| 381 | `when_comment_updated_is_processed_then_comment_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 382 | `when_set_wallet_income_enabled_true_then_wallet_is_enabled_and_credit_recurrent_are_disabled` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 383 | `when_set_lock_swipeable_is_processed_then_lock_swipeable_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 384 | `when_dismiss_recurrent_dialog_is_processed_then_dialog_and_pending_values_are_cleared` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 385 | `when_show_recurrent_dialog_is_called_then_dialog_amount_and_comment_are_set` | `EditorStateControllerTest` | ✅ PASSED | 0.001s |
| 386 | `when_set_credit_enabled_false_is_processed_then_credit_flag_and_dialog_are_both_cleared` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 387 | `when_set_recurrent_enabled_true_is_processed_then_flag_and_change_are_updated` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 388 | `when_controller_is_created_then_state_has_defaults` | `EditorStateControllerTest` | ✅ PASSED | 0.000s |
| 389 | `when_number_tapped_then_input_appends_digit_and_change_is_emitted` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 390 | `when_operator_tapped_while_already_in_calculation_mode_then_calculation_flag_is_not_re_emitted` | `NumpadControllerTest` | ✅ PASSED | 0.001s |
| 391 | `when_number_tapped_twice_then_input_is_two_chars_and_two_changes_are_emitted` | `NumpadControllerTest` | ✅ PASSED | 0.001s |
| 392 | `when_dot_tapped_on_empty_input_then_input_becomes_zero_dot` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 393 | `when_operator_tapped_on_empty_input_and_is_unary_then_operator_is_appended_and_calculation_mode_is_set` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 394 | `when_dot_tapped_after_operator_then_input_becomes_operator_zero_dot` | `NumpadControllerTest` | ✅ PASSED | 0.001s |
| 395 | `when_set_calculation_mode_disabled_then_calculation_flag_is_false_and_drag_progress_is_reset` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 396 | `when_operator_tapped_after_another_operator_then_second_operator_is_ignored` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 397 | `when_equals_tapped_on_empty_input_then_input_is_unchanged` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 398 | `when_set_drag_progress_is_processed_then_value_and_change_are_updated` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 399 | `when_backspace_clears_input_and_is_calculation_then_calculation_flag_is_cleared` | `NumpadControllerTest` | ✅ PASSED | 0.001s |
| 400 | `when_reset_tapped_then_input_is_cleared_and_calculation_is_cleared_when_in_calculation_mode` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 401 | `when_clear_input_is_called_then_input_becomes_empty_and_change_is_returned` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 402 | `when_dot_tapped_twice_in_same_segment_then_second_dot_is_ignored` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 403 | `when_operator_tapped_on_non_empty_input_then_operator_is_appended_and_calculation_mode_is_set` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 404 | `when_backspace_clears_input_but_not_in_calculation_mode_then_calculation_flag_is_not_changed` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 405 | `when_dot_tapped_after_digit_then_input_gains_a_dot` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 406 | `when_backspace_tapped_then_last_char_is_removed_and_change_is_emitted` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 407 | `when_set_calculation_mode_enabled_then_calculation_flag_and_drag_progress_are_updated` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 408 | `when_equals_tapped_on_invalid_expression_then_input_is_unchanged` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 409 | `when_equals_tapped_on_valid_expression_then_input_becomes_evaluation_result` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 410 | `when_non_unary_operator_tapped_on_empty_input_then_input_is_unchanged` | `NumpadControllerTest` | ✅ PASSED | 0.001s |
| 411 | `when_operator_tapped_after_dot_then_operator_is_ignored` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 412 | `when_reset_tapped_not_in_calculation_mode_then_only_input_change_is_emitted` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 413 | `when_controller_is_created_then_input_is_empty_and_calculation_is_false` | `NumpadControllerTest` | ✅ PASSED | 0.000s |
| 414 | `when_save_budget_settings_is_called_then_persist_action_is_emitted_and_is_first_launch_becomes_false` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 415 | `when_update_recurrent_notification_time_is_called_then_action_with_hour_and_minute_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.001s |
| 416 | `when_mark_first_launch_complete_is_called_then_is_first_launch_becomes_false_and_action_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 417 | `when_trigger_test_notifications_is_called_twice_then_two_actions_are_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 418 | `when_trigger_test_notifications_is_called_then_action_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 419 | `when_update_period_end_notification_time_is_called_then_action_with_hour_and_minute_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 420 | `when_clear_early_finish_state_is_called_then_clear_action_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.001s |
| 421 | `when_mark_first_launch_complete_is_called_twice_then_action_is_emitted_twice_but_flag_stays_false` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 422 | `when_finish_budget_early_is_called_then_finish_action_is_emitted` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 423 | `when_save_budget_settings_is_called_twice_then_persist_action_is_emitted_twice_with_each_settings` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 424 | `when_controller_is_created_then_is_first_launch_is_true` | `PeriodActionsControllerTest` | ✅ PASSED | 0.000s |
| 425 | `when_handler_returns_invalid_input_then_no_actions_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 426 | `when_handler_returns_show_recurrent_dialog_then_open_dialog_action_is_emitted_with_amount_and_comment` | `TransactionActionsControllerTest` | ✅ PASSED | 0.002s |
| 427 | `when_handler_reports_restore_failure_then_restore_failed_action_is_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 428 | `when_handler_returns_added_then_clear_input_clear_editor_flags_and_added_action_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 429 | `when_handler_returns_queued_for_next_period_then_clear_input_clear_flags_queued_and_show_message_actions_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 430 | `when_handler_returns_failed_then_show_message_action_is_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 431 | `when_handler_reports_recurrent_failure_then_no_actions_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 432 | `when_handler_reports_applied_recurrent_successfully_then_clear_input_action_is_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 433 | `when_handler_reports_delete_failure_then_delete_failed_action_is_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.002s |
| 434 | `when_handler_reports_restore_success_then_no_actions_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 435 | `when_handler_reports_delete_success_then_no_actions_are_emitted` | `TransactionActionsControllerTest` | ✅ PASSED | 0.002s |
| 436 | `when_edit_is_called_then_handler_receives_the_transaction` | `TransactionActionsControllerTest` | ✅ PASSED | 0.003s |
| 437 | `releases starts from an empty list` | `ChangelogHistoryViewModelTest` | ✅ PASSED | 0.203s |
| 438 | `releases emits the repository's release list once collected` | `ChangelogHistoryViewModelTest` | ✅ PASSED | 0.081s |
| 439 | `forPeriod returns the matching allocation` | `DynamicAllocationsTest` | ✅ PASSED | 0.001s |
| 440 | `14 days remaining - biweekly is the full remaining balance` | `DynamicAllocationsTest` | ✅ PASSED | 0.001s |
| 441 | `overspent - all allocations are zero and over flag is true` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 442 | `zero total budget - everything is zero, over flag follows today spend` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 443 | `period fully spent - all allocations are zero and over flag is true` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 444 | `14 days remaining - weekly is half the remaining balance` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 445 | `mid week - the weekly allocation is fixed from the start of the week` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 446 | `15 days remaining - weekly is seven days of the daily rate` | `DynamicAllocationsTest` | ✅ PASSED | 0.001s |
| 447 | `spec example - 6 days remaining gives 5486_73 for monthly final block` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 448 | `spec example - 6 days remaining gives 5486_73 for weekly final block` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 449 | `daily allocation decreases as days remaining decreases` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 450 | `spec example - 6 days remaining gives 914_46 daily` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 451 | `no spend today - over flag is false` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 452 | `today's spend is within daily allocation - over flag is false` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 453 | `7 days remaining - weekly block is the full balance` | `DynamicAllocationsTest` | ✅ PASSED | 0.001s |
| 454 | `today's spend exceeds daily allocation - over flag is true` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 455 | `weekly allocation adds back this week's spend` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 456 | `zero days remaining - everything is zero, over flag follows today spend` | `DynamicAllocationsTest` | ✅ PASSED | 0.000s |
| 457 | `spending exactly today's allocation is not over and keeps tomorrow's allocation unchanged` | `DynamicAllocationsTest` | ✅ PASSED | 0.001s |
| 458 | `weekly - second week leaves exactly one whole week` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 459 | `monthly view on a single month budget has no next month` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 460 | `forPeriod returns the matching allocation` | `NextBlockAllocationsTest` | ✅ PASSED | 0.001s |
| 461 | `period already over - every allocation is zero` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 462 | `overspent period - every allocation is zero` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 463 | `biweekly - one whole 14 day block after the current one` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 464 | `weekly - first week of a 21 day period leaves two whole weeks` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 465 | `weekly - partial next week gets only its days` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 466 | `weekly - final week of the period has no next week` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 467 | `last day of the period - no next block, allocation is zero` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 468 | `spec example - 120 over 3 days, 50 spent today, next day gets 35` | `NextBlockAllocationsTest` | ✅ PASSED | 0.001s |
| 469 | `mid period - only the final day is left after the current one` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 470 | `rounding follows HALF_UP to two decimals` | `NextBlockAllocationsTest` | ✅ PASSED | 0.000s |
| 471 | `weekly in the partial last week is only the days that are left` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 472 | `returns zero when total days is non-positive` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 473 | `monthly on a 30 day period returns the full budget` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 474 | `returns zero when total budget is zero` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 475 | `daily on a 30 day period divides the budget equally` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 476 | `weekly on a 30 day period is seven days of the daily rate` | `SplitBudgetTest` | ✅ PASSED | 0.001s |
| 477 | `biweekly on a 30 day period is fourteen days of the daily rate` | `SplitBudgetTest` | ✅ PASSED | 0.000s |
| 478 | `when_next_in_period_charge_is_paid_then_the_following_in_period_charge_is_upcoming` | `HistoryCalculationsTest` | ✅ PASSED | 0.002s |
| 479 | `when_no_occurrences_are_paid_then_all_occurrences_in_period_are_returned` | `HistoryCalculationsTest` | ✅ PASSED | 0.001s |
| 480 | `when_next_charge_date_is_paid_then_item_is_excluded_from_upcoming_in_period` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 481 | `when_out_of_period_charge_date_is_not_paid_then_item_appears_in_future_out_of_period` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 482 | `when_one_occurrence_is_paid_then_only_that_occurrence_is_excluded` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 483 | `when_a_different_transactions_occurrence_is_paid_then_this_item_is_unaffected` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 484 | `when_out_of_period_charge_date_is_paid_then_the_following_charge_is_shown` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 485 | `when_next_charge_date_is_not_paid_then_item_is_included` | `HistoryCalculationsTest` | ✅ PASSED | 0.000s |
| 486 | `toggling the past period is reflected in the state` | `HistoryViewModelTest` | ✅ PASSED | 0.038s |
| 487 | `an out-of-range credit cutoff day is ignored` | `HistoryViewModelTest` | ✅ PASSED | 0.109s |
| 488 | `locking swipeables is reflected in the state` | `HistoryViewModelTest` | ✅ PASSED | 0.032s |
| 489 | `setting an editing transaction is reflected in the state` | `HistoryViewModelTest` | ✅ PASSED | 0.015s |
| 490 | `setting a recurrent transaction to delete opens the confirmation dialog` | `HistoryViewModelTest` | ✅ PASSED | 0.012s |
| 491 | `a failed delete surfaces a snackbar` | `HistoryViewModelTest` | ✅ PASSED | 0.010s |
| 492 | `updating the credit cutoff day writes it onto the current budget settings` | `HistoryViewModelTest` | ✅ PASSED | 0.024s |
| 493 | `deleting a transaction stages it for removal immediately then clears it on success` | `HistoryViewModelTest` | ✅ PASSED | 0.014s |
| 494 | `a failed edit surfaces a snackbar and keeps the editing transaction` | `HistoryViewModelTest` | ✅ PASSED | 0.009s |
| 495 | `expanding a transaction toggles it on and off` | `HistoryViewModelTest` | ✅ PASSED | 0.011s |
| 496 | `dismissing the delete-recurrent dialog clears its state` | `HistoryViewModelTest` | ✅ PASSED | 0.012s |
| 497 | `deleting several transactions in quick succession deletes all of them, not just the last` | `HistoryViewModelTest` | ✅ PASSED | 0.018s |
| 498 | `confirming a recurrent delete closes the dialog and deletes the transaction` | `HistoryViewModelTest` | ✅ PASSED | 0.019s |
| 499 | `a failed mark-as-paid surfaces a snackbar` | `HistoryViewModelTest` | ✅ PASSED | 0.013s |
| 500 | `a successful edit clears the editing transaction` | `HistoryViewModelTest` | ✅ PASSED | 0.017s |
| 501 | `when_cancel_pending_delete_is_processed_then_snackbar_state_is_cleared_and_request_undo_is_emitted` | `MainScreenViewModelTest` | ✅ PASSED | 0.017s |
| 502 | `when_show_budget_period_sheet_with_force_setup_is_processed_then_sheet_and_force_setup_flags_are_true` | `MainScreenViewModelTest` | ✅ PASSED | 0.007s |
| 503 | `when_queue_delete_with_undo_is_processed_then_snackbar_state_and_effect_are_set` | `MainScreenViewModelTest` | ✅ PASSED | 0.009s |
| 504 | `when_hide_budget_period_sheet_is_processed_then_both_sheet_flags_are_false` | `MainScreenViewModelTest` | ✅ PASSED | 0.007s |
| 505 | `when_mark_wallet_sheet_opened_is_processed_then_wallet_sheet_opened_is_true` | `MainScreenViewModelTest` | ✅ PASSED | 0.008s |
| 506 | `when_viewmodel_is_created_then_initial_state_is_default` | `MainScreenViewModelTest` | ✅ PASSED | 0.010s |
| 507 | `when_set_selected_period_is_processed_then_selected_view_period_is_updated` | `MainScreenViewModelTest` | ✅ PASSED | 0.011s |
| 508 | `when_dismiss_snackbar_is_processed_then_snackbar_state_is_cleared_and_no_effect_is_emitted` | `MainScreenViewModelTest` | ✅ PASSED | 0.011s |
| 509 | `when_show_budget_period_sheet_without_force_setup_is_processed_then_sheet_is_visible_but_force_setup_is_false` | `MainScreenViewModelTest` | ✅ PASSED | 0.009s |
| 510 | `when_set_drag_progress_is_processed_then_update_drag_progress_effect_is_emitted_with_value` | `MainScreenViewModelTest` | ✅ PASSED | 0.007s |
| 511 | `when_3500ms_pass_without_user_action_then_snackbar_is_auto_dismissed_and_no_effect_is_emitted` | `MainScreenViewModelTest` | ✅ PASSED | 0.010s |
| 512 | `when_set_shown_stage_is_processed_then_shown_stage_is_updated` | `MainScreenViewModelTest` | ✅ PASSED | 0.010s |
| 513 | `when_welcome_dismissed_fails_with_repo_exception_then_onboarding_failed_effect_is_emitted` | `OnboardingViewModelTest` | ✅ PASSED | 0.066s |
| 514 | `when_welcome_dismissed_is_called_then_settings_marked_completed_and_effect_emitted` | `OnboardingViewModelTest` | ✅ PASSED | 0.022s |
| 515 | `when_viewmodel_is_created_then_state_has_defaults` | `OnboardingViewModelTest` | ✅ PASSED | 0.005s |
| 516 | `analyticsFinishedPeriod` | `AnalyticsScreenshotTest` | ✅ PASSED | 6.513s |
| 517 | `analyticsActivePeriod` | `AnalyticsScreenshotTest` | ✅ PASSED | 0.450s |
| 518 | `analyticsTabletWideLayout` | `AnalyticsTabletScreenshotTest` | ✅ PASSED | 0.536s |
| 519 | `appearanceOptionsScreen` | `AppearanceOptionsScreenshotTest` | ✅ PASSED | 0.453s |
| 520 | `autoResizeBasicTextFieldReadOnly` | `AutoResizeBasicTextFieldScreenshotTest` | ✅ PASSED | 0.228s |
| 521 | `averageSpendCard` | `AverageSpendCardScreenshotTest` | ✅ PASSED | 0.104s |
| 522 | `budgetOverviewCards` | `BudgetComponentScreenshotTest` | ✅ PASSED | 0.195s |
| 523 | `budgetDisplayOverBudget` | `BudgetComponentScreenshotTest` | ✅ PASSED | 0.218s |
| 524 | `budgetDisplayHealthyBudget` | `BudgetComponentScreenshotTest` | ✅ PASSED | 0.137s |
| 525 | `budgetDisplayHealthy` | `BudgetDisplayScreenshotTest` | ✅ PASSED | 0.138s |
| 526 | `budgetDisplayOverBudget` | `BudgetDisplayScreenshotTest` | ✅ PASSED | 0.145s |
| 527 | `countDaysChipDefault` | `BudgetDisplayScreenshotTest` | ✅ PASSED | 0.057s |
| 528 | `countDaysChipWithExtraDays` | `BudgetDisplayScreenshotTest` | ✅ PASSED | 0.051s |
| 529 | `budgetGraph_daysView_hourly_atPeriodStart` | `BudgetGraphScreenshotTest` | ✅ PASSED | 0.238s |
| 530 | `budgetGraph_categoriesView_hourly` | `BudgetGraphScreenshotTest` | ✅ PASSED | 0.210s |
| 531 | `budgetGraph_categoriesView_empty` | `BudgetGraphScreenshotTest` | ✅ PASSED | 0.179s |
| 532 | `budgetGraph_categoriesView_hourly_sameHourMultipleCategories` | `BudgetGraphScreenshotTest` | ✅ PASSED | 0.174s |
| 533 | `budgetGraph_daysView_hourly` | `BudgetGraphScreenshotTest` | ✅ PASSED | 0.200s |
| 534 | `behaviourListOverscrollsInsteadOfMovingTheSheet` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 2.378s |
| 535 | `calculatedCardStaticHelper` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 0.191s |
| 536 | `editModeState` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 0.249s |
| 537 | `periodSelectionAndSheetSwipe` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 4.291s |
| 538 | `behaviourStep` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 0.160s |
| 539 | `calculatedCardDynamicHelper` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 0.150s |
| 540 | `editModeTransition` | `BudgetPeriodSheetInteractionScreenshotTest` | ✅ PASSED | 2.956s |
| 541 | `budgetPillPalette_dark` | `BudgetPillPaletteNightScreenshotTest` | ✅ PASSED | 0.265s |
| 542 | `budgetPillPalette_light` | `BudgetPillPaletteScreenshotTest` | ✅ PASSED | 0.222s |
| 543 | `budgetPillPalette_overriddenScale` | `BudgetPillPaletteScreenshotTest` | ✅ PASSED | 0.217s |
| 544 | `budgetPillDraftCountsDownCenteredAmount` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.084s |
| 545 | `budgetPillDailyExceededWithProjection` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.071s |
| 546 | `budgetPillOverBudget` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.133s |
| 547 | `budgetPillDraftPushesOverDailyAllocation` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.071s |
| 548 | `budgetPillDynamicOverBudget` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.127s |
| 549 | `budgetPillDynamicMode` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.121s |
| 550 | `budgetPillDailyExceeded` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.154s |
| 551 | `budgetPillHealthy` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.129s |
| 552 | `budgetPillWeeklyAlternatesWithTodayAndSwipes` | `BudgetPillScreenshotTest` | ✅ PASSED | 4.757s |
| 553 | `budgetPillWeeklyExceededWithProjection` | `BudgetPillScreenshotTest` | ✅ PASSED | 0.079s |
| 554 | `calendarHeatmap` | `CalendarHeatmapScreenshotTest` | ✅ PASSED | 0.158s |
| 555 | `categoriesChartCard` | `CategoriesChartCardScreenshotTest` | ✅ PASSED | 0.102s |
| 556 | `categoryAmountDefault` | `CategoryAmountScreenshotTest` | ✅ PASSED | 0.048s |
| 557 | `categoryAmountSpecial` | `CategoryAmountScreenshotTest` | ✅ PASSED | 0.045s |
| 558 | `categoryAnalytics_threeTransactionsAcrossDays` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.222s |
| 559 | `categoryAnalytics_darkTheme` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.174s |
| 560 | `categoryAnalytics_emptyState` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.117s |
| 561 | `categoryAnalytics_twoTransactions` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.155s |
| 562 | `categoryAnalytics_loading` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.143s |
| 563 | `categoryAnalytics_singleTransaction` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.119s |
| 564 | `categoryAnalytics_dayView` | `CategoryAnalyticsScreenshotTest` | ✅ PASSED | 0.118s |
| 565 | `changelogHistoryScreenSpanish` | `ChangelogScreenshotTest` | ✅ PASSED | 0.177s |
| 566 | `changelogItemCardFeature` | `ChangelogScreenshotTest` | ✅ PASSED | 0.113s |
| 567 | `changelogSectionHeaderImprovements` | `ChangelogScreenshotTest` | ✅ PASSED | 0.058s |
| 568 | `changelogHistoryScreenDarkTheme` | `ChangelogScreenshotTest` | ✅ PASSED | 0.153s |
| 569 | `changelogHistoryScreenFrench` | `ChangelogScreenshotTest` | ✅ PASSED | 0.153s |
| 570 | `changelogSectionHeaderBugFixes` | `ChangelogScreenshotTest` | ✅ PASSED | 0.051s |
| 571 | `changelogBottomSheetFeature` | `ChangelogScreenshotTest` | ✅ PASSED | 0.448s |
| 572 | `changelogHistoryScreen` | `ChangelogScreenshotTest` | ✅ PASSED | 0.159s |
| 573 | `changelogItemCardImprovement` | `ChangelogScreenshotTest` | ✅ PASSED | 0.114s |
| 574 | `changelogHistoryScreenEmpty` | `ChangelogScreenshotTest` | ✅ PASSED | 0.115s |
| 575 | `changelogItemCardBugFix` | `ChangelogScreenshotTest` | ✅ PASSED | 0.103s |
| 576 | `changelogSectionHeaderNewFeatures` | `ChangelogScreenshotTest` | ✅ PASSED | 0.049s |
| 577 | `categoriesChartCardWithTransactions` | `ComponentScreenshotTest` | ✅ PASSED | 0.125s |
| 578 | `budgetPillHealthyBudget` | `ComponentScreenshotTest` | ✅ PASSED | 0.129s |
| 579 | `daysLeftCardMidMonth` | `ComponentScreenshotTest` | ✅ PASSED | 0.057s |
| 580 | `budgetPillOverBudget` | `ComponentScreenshotTest` | ✅ PASSED | 0.156s |
| 581 | `statCardWithCrossedValue` | `ComponentScreenshotTest` | ✅ PASSED | 0.149s |
| 582 | `multipleStatCardsRow` | `ComponentScreenshotTest` | ✅ PASSED | 0.123s |
| 583 | `spendsChartWithTransactions` | `ComponentScreenshotTest` | ✅ PASSED | 0.142s |
| 584 | `statCardStandard` | `ComponentScreenshotTest` | ✅ PASSED | 0.117s |
| 585 | `dayTotalItem` | `DayTotalItemScreenshotTest` | ✅ PASSED | 0.056s |
| 586 | `daysLeftCardMidMonth` | `DaysLeftCardScreenshotTest` | ✅ PASSED | 0.077s |
| 587 | `descriptionButtonWithTitleAndDescription` | `DescriptionButtonScreenshotTest` | ✅ PASSED | 0.095s |
| 588 | `detailedChart_darkTheme` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.134s |
| 589 | `detailedChart_selectedDate` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.134s |
| 590 | `detailedChart_tooltip` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.130s |
| 591 | `detailedChart_fourPointsVarying` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.121s |
| 592 | `detailedChart_threePointsAscending` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.132s |
| 593 | `detailedChart_twoPointsLine` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.119s |
| 594 | `detailedChart_singlePoint` | `DetailedChartScreenshotTest` | ✅ PASSED | 0.123s |
| 595 | `donutChartDefault` | `DonutChartScreenshotTest` | ✅ PASSED | 0.096s |
| 596 | `editableNoteTagCollapsed` | `EditableNoteTagScreenshotTest` | ✅ PASSED | 0.131s |
| 597 | `editorEditingWithExtraNoteInline` | `EditorScreenshotTest` | ✅ PASSED | 0.241s |
| 598 | `editorEditingExtraNoteLabelsWithChips` | `EditorScreenshotTest` | ✅ PASSED | 0.152s |
| 599 | `editorIdleWithHealthyBudget` | `EditorScreenshotTest` | ✅ PASSED | 0.128s |
| 600 | `editorEditingCalculationExpression` | `EditorScreenshotTest` | ✅ PASSED | 0.154s |
| 601 | `editorEditingAmountWithTagsAndComment` | `EditorScreenshotTest` | ✅ PASSED | 0.171s |
| 602 | `expenseDetailReadOnly` | `ExpenseItemExpandedContentScreenshotTest` | ✅ PASSED | 0.154s |
| 603 | `expenseItemFirst` | `ExpenseItemScreenshotTest` | ✅ PASSED | 0.055s |
| 604 | `expenseItemLast` | `ExpenseItemScreenshotTest` | ✅ PASSED | 0.062s |
| 605 | `featureLabScreen_darkTheme` | `FeatureLabScreenshotTest` | ✅ PASSED | 0.213s |
| 606 | `featureLabScreen` | `FeatureLabScreenshotTest` | ✅ PASSED | 0.186s |
| 607 | `finishedPeriodHeaderRender` | `FinishedPeriodHeaderScreenshotTest` | ✅ PASSED | 0.130s |
| 608 | `hintTipCenterPosition` | `HintTipScreenshotTest` | ✅ PASSED | 0.072s |
| 609 | `historyDateDividerExpanded` | `HistoryDateDividerScreenshotTest` | ✅ PASSED | 0.047s |
| 610 | `historyRecurringPaymentsSections` | `HistoryScreenshotTest` | ✅ PASSED | 0.166s |
| 611 | `historyCurrentAndPastExpenses` | `HistoryScreenshotTest` | ✅ PASSED | 0.160s |
| 612 | `historyEmptyState` | `HistoryScreenshotTest` | ✅ PASSED | 0.114s |
| 613 | `mainScreenPhoneIdle` | `MainScreenScreenshotTest` | ✅ PASSED | 0.250s |
| 614 | `mainScreenPhoneEditingExpense` | `MainScreenScreenshotTest` | ✅ PASSED | 0.192s |
| 615 | `middlePeriodHeader` | `MiddlePeriodHeaderScreenshotTest` | ✅ PASSED | 0.062s |
| 616 | `minMaxSpentCardMax` | `MinMaxSpentCardScreenshotTest` | ✅ PASSED | 0.144s |
| 617 | `minMaxSpentCardMin` | `MinMaxSpentCardScreenshotTest` | ✅ PASSED | 0.147s |
| 618 | `multiCategoryChart_tooltipOnTappedBar` | `MultiCategoryChartScreenshotTest` | ✅ PASSED | 0.150s |
| 619 | `multiCategoryChart_noStaticTotalLabels` | `MultiCategoryChartScreenshotTest` | ✅ PASSED | 0.139s |
| 620 | `newCategoryTagSavedWithLongName` | `NewCategoryTagScreenshotTest` | ✅ PASSED | 0.071s |
| 621 | `newCategoryTagSaved` | `NewCategoryTagScreenshotTest` | ✅ PASSED | 0.060s |
| 622 | `newCategoryTagCollapsed` | `NewCategoryTagScreenshotTest` | ✅ PASSED | 0.045s |
| 623 | `noTransactionsEmptyState` | `NoTransactionsViewScreenshotTest` | ✅ PASSED | 0.108s |
| 624 | `numberedRowWithSubtitle` | `NumberedRowScreenshotTest` | ✅ PASSED | 0.049s |
| 625 | `numpadButtonDefaultRender` | `NumpadButtonScreenshotTest` | ✅ PASSED | 0.110s |
| 626 | `numpadButtonOperatorRender` | `NumpadButtonScreenshotTest` | ✅ PASSED | 0.107s |
| 627 | `numpadButtonPrimaryRender` | `NumpadButtonScreenshotTest` | ✅ PASSED | 0.114s |
| 628 | `numpadIdle` | `NumpadScreenshotTest` | ✅ PASSED | 0.168s |
| 629 | `numpadCalculationMode` | `NumpadScreenshotTest` | ✅ PASSED | 0.167s |
| 630 | `numpadLeftoverChoice` | `NumpadScreenshotTest` | ✅ PASSED | 0.199s |
| 631 | `numpadThousandsShortcut` | `NumpadScreenshotTest` | ✅ PASSED | 0.158s |
| 632 | `pastPeriodsBottomSheet_moreThanFourCategoriesFillsBar` | `PastPeriodsBottomSheetScreenshotTest` | ✅ PASSED | 0.157s |
| 633 | `pastPeriodsBottomSheet_wavyDividerMarksEstimatedSection` | `PastPeriodsBottomSheetScreenshotTest` | ✅ PASSED | 0.161s |
| 634 | `pastPeriodsBottomSheet_manyCategoriesCycleColorsAndShowSavedStripe` | `PastPeriodsBottomSheetScreenshotTest` | ✅ PASSED | 0.143s |
| 635 | `pastPeriodsBottomSheet_virtualPeriodShowsEstimatedBadge` | `PastPeriodsBottomSheetScreenshotTest` | ✅ PASSED | 0.130s |
| 636 | `pastPeriodsBottomSheet_savedLegendEntryLabel` | `PastPeriodsBottomSheetScreenshotTest` | ✅ PASSED | 0.154s |
| 637 | `recurrentPaymentsDividerCollapsed` | `RecurrentPaymentsDividerScreenshotTest` | ✅ PASSED | 0.054s |
| 638 | `recurrentPaymentsDividerExpanded` | `RecurrentPaymentsDividerScreenshotTest` | ✅ PASSED | 0.046s |
| 639 | `recurrentTicketCard` | `RecurrentTicketScreenshotTest` | ✅ PASSED | 0.078s |
| 640 | `rolloverDialogDefault` | `RolloverDialogScreenshotTest` | ✅ PASSED | 0.178s |
| 641 | `savingsRecommendationCardOverBudget` | `SavingsRecommendationCardScreenshotTest` | ✅ PASSED | 0.182s |
| 642 | `savingsRecommendationCardUnderSavings` | `SavingsRecommendationCardScreenshotTest` | ✅ PASSED | 0.186s |
| 643 | `dueTodayCardAccentLight` | `ScratchDueTodayAccentTest` | ✅ PASSED | 0.513s |
| 644 | `dueTodayCardAccentDark` | `ScratchDueTodayAccentTest` | ✅ PASSED | 0.114s |
| 645 | `paddedListGroupAppearance` | `SettingsGroupScreenshotTest` | ✅ PASSED | 0.124s |
| 646 | `settingsDefaultState` | `SettingsScreenshotTest` | ✅ PASSED | 0.224s |
| 647 | `settingsDarkThemeWithCreditFeature` | `SettingsScreenshotTest` | ✅ PASSED | 0.174s |
| 648 | `spendBudgetCard` | `SpendBudgetCardScreenshotTest` | ✅ PASSED | 0.081s |
| 649 | `spendsChartLine` | `SpendsChartScreenshotTest` | ✅ PASSED | 0.125s |
| 650 | `outlinedSpendsChart` | `SpendsChartScreenshotTest` | ✅ PASSED | 0.104s |
| 651 | `spendsCountCardDefault` | `SpendsCountCardScreenshotTest` | ✅ PASSED | 0.075s |
| 652 | `statCardWithCrossedValue` | `StatCardScreenshotTest` | ✅ PASSED | 0.096s |
| 653 | `statCardBasic` | `StatCardScreenshotTest` | ✅ PASSED | 0.088s |
| 654 | `subscriptionsEmptyState` | `SubscriptionsScreenshotTest` | ✅ PASSED | 0.141s |
| 655 | `subscriptionsDueSoonAndUpcoming_darkTheme` | `SubscriptionsScreenshotTest` | ✅ PASSED | 0.206s |
| 656 | `subscriptionsDueSoonAndUpcoming` | `SubscriptionsScreenshotTest` | ✅ PASSED | 0.181s |
| 657 | `swipeActionsDefault` | `SwipeActionsScreenshotTest` | ✅ PASSED | 0.084s |
| 658 | `swipeToDismissDefault` | `SwipeToDismissScreenshotTest` | ✅ PASSED | 0.058s |
| 659 | `swipeableExpenseItem` | `SwipeableExpenseItemScreenshotTest` | ✅ PASSED | 0.108s |
| 660 | `textRowWithEndCaption` | `TextRowScreenshotTest` | ✅ PASSED | 0.055s |
| 661 | `textRowWithIconAndDescription` | `TextRowScreenshotTest` | ✅ PASSED | 0.060s |
| 662 | `transactionTicketCard` | `TicketCardScreenshotTest` | ✅ PASSED | 0.131s |
| 663 | `ticketViewDefault` | `TicketViewScreenshotTest` | ✅ PASSED | 0.066s |
| 664 | `topSheetHalfExpanded` | `TopSheetScreenshotTest` | ✅ PASSED | 0.144s |
| 665 | `upcomingRecurrentItemFirst` | `UpcomingRecurrentItemRowScreenshotTest` | ✅ PASSED | 0.069s |
| 666 | `upcomingRecurrentItemLast` | `UpcomingRecurrentItemRowScreenshotTest` | ✅ PASSED | 0.059s |
| 667 | `wavyDivider` | `WavyDividerScreenshotTest` | ✅ PASSED | 0.069s |
| 668 | `onCensorModeToggle flips the censor manager` | `SettingsViewModelTest` | ✅ PASSED | 0.563s |
| 669 | `onRecurrentNotificationTimeChange persists the time and reschedules the recurrent job` | `SettingsViewModelTest` | ✅ PASSED | 0.018s |
| 670 | `onBack emits the back navigation effect` | `SettingsViewModelTest` | ✅ PASSED | 0.007s |
| 671 | `contrast and typography modes are mapped to display strings` | `SettingsViewModelTest` | ✅ PASSED | 0.011s |
| 672 | `onAmoledToggle flips the current value and persists it` | `SettingsViewModelTest` | ✅ PASSED | 0.012s |
| 673 | `onShowPastTransactionsToggle flips the current value and persists it` | `SettingsViewModelTest` | ✅ PASSED | 0.012s |
| 674 | `onBugReportClick emits the bug report navigation effect and consumeEffect clears it` | `SettingsViewModelTest` | ✅ PASSED | 0.006s |
| 675 | `onNotificationTimeChange persists the time and reschedules` | `SettingsViewModelTest` | ✅ PASSED | 0.010s |
| 676 | `onThemeChange maps the label to the enum and persists it` | `SettingsViewModelTest` | ✅ PASSED | 0.013s |
| 677 | `onCutoffDayChange does nothing when there is no budget yet` | `SettingsViewModelTest` | ✅ PASSED | 0.012s |
| 678 | `the credit card cutoff day comes from the active budget settings` | `SettingsViewModelTest` | ✅ PASSED | 0.012s |
| 679 | `onPeriodMappingModeChange persists the mode` | `SettingsViewModelTest` | ✅ PASSED | 0.009s |
| 680 | `onContrastChange persists the mapped contrast mode` | `SettingsViewModelTest` | ✅ PASSED | 0.009s |
| 681 | `onRoundedFontToggle flips the current value and persists it` | `SettingsViewModelTest` | ✅ PASSED | 0.012s |
| 682 | `the censored flag is reflected from the censor manager` | `SettingsViewModelTest` | ✅ PASSED | 0.010s |
| 683 | `onResetTutorial delegates to the repository` | `SettingsViewModelTest` | ✅ PASSED | 0.009s |
| 684 | `theme mode is mapped to a display string` | `SettingsViewModelTest` | ✅ PASSED | 0.010s |
| 685 | `onCutoffDayChange writes the day onto the existing budget settings` | `SettingsViewModelTest` | ✅ PASSED | 0.011s |
| 686 | `onRecurrentPaymentsViewModeChange persists the mode` | `SettingsViewModelTest` | ✅ PASSED | 0.011s |
| 687 | `onMaterialYouToggle flips the current value and persists it` | `SettingsViewModelTest` | ✅ PASSED | 0.018s |
| 688 | `finishing a hidden step's exit animation drops it from the list` | `BugReportViewModelTest` | ✅ PASSED | 0.119s |
| 689 | `a failing report generation emits a ShowError effect` | `BugReportViewModelTest` | ✅ PASSED | 0.107s |
| 690 | `attachments are de-duplicated on selection and removable` | `BugReportViewModelTest` | ✅ PASSED | 0.159s |
| 691 | `adding steps appends rows with incrementing ids starting after the initial three` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 692 | `changing a step out of bounds is a no-op` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 693 | `a successful bug submission emits an email composer effect with a bug-tagged title` | `BugReportViewModelTest` | ✅ PASSED | 0.005s |
| 694 | `selecting a bug report shows the reproduction steps` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 695 | `the generating flag is cleared again after a submission finishes` | `BugReportViewModelTest` | ✅ PASSED | 0.006s |
| 696 | `selecting a feature request hides the reproduction steps` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 697 | `a successful feature-request submission tags the title with Feature` | `BugReportViewModelTest` | ✅ PASSED | 0.005s |
| 698 | `submitting an incomplete report does nothing` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 699 | `removing a step while more than one exists hides it rather than dropping it` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 700 | `text fields update the state` | `BugReportViewModelTest` | ✅ PASSED | 0.003s |
| 701 | `skipping a cycle calls the repository directly without going through the transaction handler` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.009s |
| 702 | `a subscription past its end date is excluded from the active count and total` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.008s |
| 703 | `no recurring transactions produces an empty state` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.006s |
| 704 | `a next charge beyond the due-soon window appears in upcoming` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.007s |
| 705 | `confirming paid delegates to the budget transaction handler with the occurrence date` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.008s |
| 706 | `a next charge within the due-soon window appears in dueSoon` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.006s |
| 707 | `a subscription due exactly today is surfaced in dueSoon with a zero-day next charge` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.007s |
| 708 | `a failed confirm-paid call surfaces a snackbar effect` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.006s |
| 709 | `a subscription already marked paid for today is not surfaced again` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.006s |
| 710 | `a failed skip call surfaces a snackbar effect` | `SubscriptionsViewModelTest` | ✅ PASSED | 0.009s |
| 711 | `ideal savings and the six month example follow the savings preference` | `SavingsCardMetricsTest` | ✅ PASSED | 0.001s |
| 712 | `savings never go negative when over budget` | `SavingsCardMetricsTest` | ✅ PASSED | 0.000s |
| 713 | `percentages are shares of the budget and incomes are ignored` | `SavingsCardMetricsTest` | ✅ PASSED | 0.000s |
| 714 | `no metrics without a budget period` | `SavingsCardMetricsTest` | ✅ PASSED | 0.000s |
| 715 | `goalPerPeriod is null unless the goal is usable` | `SavingsCardMetricsTest` | ✅ PASSED | 0.000s |
| 716 | `calculateCumulativePoints correctly sums daily transactions` | `BudgetGraphTest` | ✅ PASSED | 0.000s |
| 717 | `calculateCumulativePoints handles weekly granularity` | `BudgetGraphTest` | ✅ PASSED | 0.000s |
| 718 | `dynamic weekly chain includes surplus and adjustments and ends at the allocation minus spent` | `BudgetFormulaTest` | ✅ PASSED | 0.003s |
| 719 | `carry over overspend ends with tomorrow's amount and never shows a negative number` | `BudgetFormulaTest` | ✅ PASSED | 0.000s |
| 720 | `static weekly chain evaluates step by step down to the pill amount` | `BudgetFormulaTest` | ✅ PASSED | 0.000s |
| 721 | `static over its block keeps the plain left row and never projects` | `BudgetFormulaTest` | ✅ PASSED | 0.000s |
| 722 | `daily over its allocation ends with tomorrow's projection instead of a negative amount` | `BudgetFormulaTest` | ✅ PASSED | 0.000s |
| 723 | `reserved charges get their own labelled row and are split out of the remaining budget` | `BudgetFormulaTest` | ✅ PASSED | 0.001s |
| 724 | `carry over weekly chain adds what was carried and ends at the pill amount` | `BudgetFormulaTest` | ✅ PASSED | 0.000s |
| 725 | `advance skips indices with no bounds` | `TutorialBoxStateTest` | ✅ PASSED | 0.000s |
| 726 | `initial state is not completed and index is -1` | `TutorialBoxStateTest` | ✅ PASSED | 0.000s |
| 727 | `advance with empty order does nothing` | `TutorialBoxStateTest` | ✅ PASSED | 0.001s |
| 728 | `resetForReplay clears state` | `TutorialBoxStateTest` | ✅ PASSED | 0.000s |
| 729 | `advance marks as completed when no more steps available` | `TutorialBoxStateTest` | ✅ PASSED | 0.000s |
| 730 | `advance skips already visited indices` | `TutorialBoxStateTest` | ✅ PASSED | 0.000s |
| 731 | `stripMarkdown removes markdown tokens cleanly for notifications` | `MarkdownParserTest` | ✅ PASSED | 0.000s |
| 732 | `parseToAnnotatedString strips leading markdown headers and bullets` | `MarkdownParserTest` | ✅ PASSED | 0.001s |
| 733 | `parseToAnnotatedString handles bullet points correctly` | `MarkdownParserTest` | ✅ PASSED | 0.000s |
| 734 | `the factory formatter places the symbol at the end for end-position currencies` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 735 | `an end-position currency puts the symbol after the number` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 736 | `minimumFractionDigits pads a whole value` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 737 | `the simple formatter concatenates the symbol before a negative number` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 738 | `USD keeps two decimals when the value has them` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 739 | `currency code lookup is case-insensitive` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 740 | `an unknown currency code falls back to prefixing the raw code` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 741 | `maximumFractionDigits of zero drops the fractional part` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 742 | `the factory formatter honours minimumFractionDigits` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 743 | `the factory formatter prefixes the symbol and groups thousands` | `CurrencyFormatTest` | ✅ PASSED | 0.000s |
| 744 | `the factory formatter puts a real minus in front of the symbol` | `CurrencyFormatTest` | ✅ PASSED | 0.001s |
| 745 | `EGP formats with pound symbol in English locale and Arabic symbol in Arabic locale` | `CurrencyFormatTest` | ✅ PASSED | 0.012s |
| 746 | `USD gets a leading dollar sign and grouped thousands` | `CurrencyFormatTest` | ✅ PASSED | 0.001s |
| 747 | `ackPayload_serializesAndDeserializesWithEnumStatus` | `WearSyncProtocolTest` | ✅ PASSED | 0.062s |

---
**تم إعداد وتوليد هذا التقرير تلقائياً ومطابقته 100% مع نتائج تشغيل Gradle التنفيذية.**
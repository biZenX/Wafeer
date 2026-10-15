package com.serranoie.app.minus.domain.time

import java.time.LocalDate
import java.util.Locale

enum class CalendarSystem {
    GREGORIAN {
        override fun dayOfMonth(date: LocalDate): Int = date.dayOfMonth

        override fun lengthOfMonth(date: LocalDate): Int = date.lengthOfMonth()

        override fun withDayOfMonth(date: LocalDate, day: Int): LocalDate =
            date.withDayOfMonth(day.coerceIn(1, date.lengthOfMonth()))

        override fun plusMonths(date: LocalDate, months: Long): LocalDate = date.plusMonths(months)
    },
    PERSIAN {
        override fun dayOfMonth(date: LocalDate): Int = PersianDate.of(date).day

        override fun lengthOfMonth(date: LocalDate): Int = PersianDate.of(date).lengthOfMonth()

        override fun withDayOfMonth(date: LocalDate, day: Int): LocalDate {
            val persianDate = PersianDate.of(date)
            return persianDate.copy(day = day.coerceIn(1, persianDate.lengthOfMonth())).toLocalDate()
        }

        override fun plusMonths(date: LocalDate, months: Long): LocalDate {
            val persianDate = PersianDate.of(date)
            val totalMonths = persianDate.year * 12L + persianDate.month0 + months
            val target = PersianDate(
                year = Math.floorDiv(totalMonths, 12L).toInt(),
                month0 = Math.floorMod(totalMonths, 12L).toInt(),
                day = 1,
            )
            return target.copy(day = persianDate.day.coerceAtMost(target.lengthOfMonth())).toLocalDate()
        }
    };

    abstract fun dayOfMonth(date: LocalDate): Int

    abstract fun lengthOfMonth(date: LocalDate): Int

    abstract fun withDayOfMonth(date: LocalDate, day: Int): LocalDate

    abstract fun plusMonths(date: LocalDate, months: Long): LocalDate

    fun firstDayOfMonth(date: LocalDate): LocalDate = withDayOfMonth(date, 1)

    fun lastDayOfMonth(date: LocalDate): LocalDate = withDayOfMonth(date, lengthOfMonth(date))

    companion object {
        fun of(locale: Locale): CalendarSystem {
            val preferred = locale.getUnicodeLocaleType("ca") ?: if (locale.language == "fa") "persian" else null
            return if (preferred == "persian") PERSIAN else GREGORIAN
        }

        fun current(): CalendarSystem = of(Locale.getDefault())
    }
}

private data class PersianDate(val year: Int, val month0: Int, val day: Int) {

    fun lengthOfMonth(): Int = when {
        month0 < 6 -> 31
        month0 < 11 -> 30
        isLeapYear(year) -> 30
        else -> 29
    }

    fun toLocalDate(): LocalDate =
        LocalDate.ofEpochDay(yearStartEpochDay(year) + monthOffset(month0) + day - 1)

    companion object {
        private const val EPOCH_DAY_OF_FARVARDIN_1_YEAR_1 = -492_268L

        fun of(date: LocalDate): PersianDate {
            val daysSinceEpoch = date.toEpochDay() - EPOCH_DAY_OF_FARVARDIN_1_YEAR_1
            val year = 1 + Math.floorDiv(33L * daysSinceEpoch + 3, 12_053L).toInt()
            val dayOfYear = (date.toEpochDay() - yearStartEpochDay(year)).toInt()
            val month0 = if (dayOfYear < 216) dayOfYear / 31 else (dayOfYear - 6) / 30
            return PersianDate(year, month0, dayOfYear - monthOffset(month0) + 1)
        }

        private fun yearStartEpochDay(year: Int): Long =
            EPOCH_DAY_OF_FARVARDIN_1_YEAR_1 + 365L * (year - 1) + Math.floorDiv(8L * year + 21, 33L)

        private fun monthOffset(month0: Int): Int = if (month0 < 6) 31 * month0 else 30 * month0 + 6

        private fun isLeapYear(year: Int): Boolean = Math.floorMod(25L * year + 11, 33L) < 8
    }
}

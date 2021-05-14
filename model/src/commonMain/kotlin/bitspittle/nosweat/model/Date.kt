package bitspittle.nosweat.model

import kotlinx.serialization.Serializable

enum class Day {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
}

enum class Month {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    fun numDays(year: Int) = numDays((year % 4) == 0)

    fun numDays(leapYear: Boolean = false): Int {
        return when (this) {
            JANUARY -> 31
            FEBRUARY -> if (leapYear) 29 else 28
            MARCH -> 31
            APRIL -> 30
            MAY -> 31
            JUNE -> 30
            JULY -> 31
            AUGUST -> 31
            SEPTEMBER -> 30
            OCTOBER -> 31
            NOVEMBER -> 30
            DECEMBER -> 31
        }
    }

    fun next(): Month {
        return values()[(this.ordinal + 1) % 12]
    }

    fun prev(): Month {
        return values()[(this.ordinal + 11) % 12]
    }
}

/**
 * @param day Should be between 1 and, e.g. 31 for a month with 31 days
 */
@Serializable
data class Date(
    val year: Int,
    val month: Month,
    val day: Int,
) {
    private val numDaysThisMonth get() = month.numDays(year)

    init {
        require(day in 1..numDaysThisMonth) {
            "For $month, day should be between 1 and $numDaysThisMonth, inclusive, but was: $day"
        }
    }

    companion object {
        private val MONTH_OFFSETS = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    }

    // See: https://en.wikipedia.org/wiki/Determination_of_the_day_of_the_week#Methods_in_computer_code
    fun toDay(): Day {
        var dayIndex = year
        if (month.ordinal <= Month.FEBRUARY.ordinal) {
            dayIndex -= 1
        }

        dayIndex = (dayIndex + dayIndex / 4 - dayIndex / 100 + dayIndex / 400 + MONTH_OFFSETS[month.ordinal] + day) % 7
        return Day.values()[dayIndex]
    }

    fun next(): Date {
        var day = day + 1
        var month = month
        var year = year
        if (day > numDaysThisMonth) {
            day = 1
            month = month.next()
            if (month == Month.JANUARY) {
                ++year
            }
        }
        return Date(year, month, day)
    }

    fun prev(): Date {
        var day = day - 1
        var month = month
        var year = year
        if (day == 0) {
            month = month.prev()
            if (month == Month.DECEMBER) {
                --year
            }
            day = month.numDays(year)
        }
        return Date(year, month, day)
    }
}

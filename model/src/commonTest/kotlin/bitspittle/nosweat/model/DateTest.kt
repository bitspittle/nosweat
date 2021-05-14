package bitspittle.nosweat.model

import com.github.bitspittle.truthish.assertThat
import com.github.bitspittle.truthish.assertThrows
import kotlin.test.Test

class DateTest {
    @Test
    fun dateDayMustBeValid() {
        assertThrows<IllegalArgumentException> {
            Date(2021, Month.AUGUST, 99)
        }

        assertThrows<IllegalArgumentException> {
            Date(2021, Month.MARCH, 0)
        }

        Date(2000, Month.FEBRUARY, 29)
        assertThrows<IllegalArgumentException> {
            Date(2001, Month.FEBRUARY, 29)
        }
    }

    @Test
    fun dateCanConvertToDay() {
        assertThat(Date(2021, Month.MAY, 13).toDay()).isEqualTo(Day.THURSDAY)
        assertThat(Date(2001, Month.JUNE, 15).toDay()).isEqualTo(Day.FRIDAY)
    }

    @Test
    fun dateNextAndPrevWorks() {
        assertThat(Date(2010, Month.SEPTEMBER, 13).next()).isEqualTo(Date(2010, Month.SEPTEMBER, 14))
        assertThat(Date(2010, Month.SEPTEMBER, 30).next()).isEqualTo(Date(2010, Month.OCTOBER, 1))
        assertThat(Date(2014, Month.DECEMBER, 31).next()).isEqualTo(Date(2015, Month.JANUARY, 1))

        assertThat(Date(2010, Month.SEPTEMBER, 14).prev()).isEqualTo(Date(2010, Month.SEPTEMBER, 13))
        assertThat(Date(2010, Month.OCTOBER, 1).prev()).isEqualTo(Date(2010, Month.SEPTEMBER, 30))
        assertThat(Date(2000, Month.JANUARY, 1).prev()).isEqualTo(Date(1999, Month.DECEMBER, 31))
    }
}
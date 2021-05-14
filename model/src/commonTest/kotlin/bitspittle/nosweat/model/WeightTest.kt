package bitspittle.nosweat.model

import com.github.bitspittle.truthish.assertThat
import kotlin.test.Test

class WeightTest {
    @Test
    fun weightsAreRoundedToTwoDecimalPoints() {
        assertThat(Weight.Pounds(1.23456f).value).isEqualTo(1.23f)
        assertThat(Weight.Kilograms(8.76543f).value).isEqualTo(8.77f)
    }

    @Test
    fun canConvertPrimitives() {
        assertThat(22.lbs).isEqualTo(Weight.Pounds(22))
        assertThat(22.kg).isEqualTo(Weight.Kilograms(22))
        assertThat(22.5f.lbs).isEqualTo(Weight.Pounds(22.5f))
        assertThat(22.5f.kg).isEqualTo(Weight.Kilograms(22.5f))
    }

    @Test
    fun canConvertBetweenWeights() {
        assertThat(54.kg.toPounds()).isEqualTo(119.05f.lbs)
        assertThat(144.lbs.toKilograms()).isEqualTo(65.32f.kg)
    }
}
package bitspittle.nosweat.model

import bitspittle.nosweat.model.json.serializers.WeightSerializer
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

private fun Float.round(numDecimals: Int): Float {
    var multiplier = 1
    repeat(numDecimals) { multiplier *= 10 }

    return (this * multiplier).roundToInt() / multiplier.toFloat()
}

private fun kgToLbs(value: Float) = (value * 2.2046f)
private fun lbsToKg(value: Float) = (value * 0.4536f)

@Serializable(with = WeightSerializer::class)
sealed class Weight(value: Float) {
    val value = value.round(2)

    class Pounds(value: Float) : Weight(value) {
        constructor(value: Int): this(value.toFloat())
        fun toKilograms() = Kilograms(lbsToKg(value))
    }

    class Kilograms(value: Float) : Weight(value) {
        constructor(value: Int): this(value.toFloat())
        fun toPounds() = Pounds(kgToLbs(value))
    }

    override fun equals(other: Any?): Boolean {
        return other != null && this::class == other::class && this.value == (other as Weight).value
    }

    override fun hashCode() = this.value.hashCode()

    override fun toString(): String {
        val self = this
        return buildString {
            append(value)
            when (self) {
                is Pounds -> append("lbs")
                is Kilograms -> append("kg")
            }
        }
    }
}

val Float.lbs
    get() = Weight.Pounds(this)

val Float.kg
    get() = Weight.Kilograms(this)

val Int.lbs
    get() = this.toFloat().lbs

val Int.kg
    get() = this.toFloat().kg
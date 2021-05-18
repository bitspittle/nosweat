package bitspittle.nosweat.model.serializers

import bitspittle.nosweat.model.Weight
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@SerialName("Weight")
private class WeightSurrogate(val value: Float, val type: String)

object WeightSerializer : KSerializer<Weight> {
    override val descriptor: SerialDescriptor = WeightSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Weight) {
        @Suppress("UnnecessaryVariable") // rename for readability
        val weight = value
        encoder.encodeSerializableValue(
            WeightSurrogate.serializer(), WeightSurrogate(
                weight.value, when (weight) {
                    is Weight.Pounds -> "Pounds"
                    is Weight.Kilograms -> "Kilograms"
                }
            )
        )
    }

    override fun deserialize(decoder: Decoder): Weight {
        val surrogate = decoder.decodeSerializableValue(WeightSurrogate.serializer())
        return when (val type = surrogate.type) {
            "Pounds" -> Weight.Pounds(surrogate.value)
            "Kilograms" -> Weight.Kilograms(surrogate.value)
            else -> error("Invalid weight type: $type")
        }
    }
}
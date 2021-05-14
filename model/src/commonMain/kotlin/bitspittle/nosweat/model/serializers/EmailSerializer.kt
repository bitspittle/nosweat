package bitspittle.nosweat.model.serializers

import bitspittle.nosweat.model.Email
import bitspittle.nosweat.model.Weight
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object EmailSerializer : KSerializer<Email> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Email", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Email) {
        @Suppress("UnnecessaryVariable") // for readability
        val email = value
        encoder.encodeString(email.value)
    }
    override fun deserialize(decoder: Decoder) = Email(decoder.decodeString())
}
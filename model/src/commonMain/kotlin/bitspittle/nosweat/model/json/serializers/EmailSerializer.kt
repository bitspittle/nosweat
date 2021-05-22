package bitspittle.nosweat.model.json.serializers

import bitspittle.nosweat.model.Email
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object EmailSerializer : KSerializer<Email> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Email", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Email) {
        @Suppress("UnnecessaryVariable") // for readability
        val email = value
        encoder.encodeString(email.value)
    }
    override fun deserialize(decoder: Decoder) = Email(decoder.decodeString())
}
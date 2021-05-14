package bitspittle.nosweat.model

import bitspittle.nosweat.model.serializers.EmailSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EmailSerializer::class)
class Email(value: String) {
    val value = value.toLowerCase()
    val user: String
    val domain: String

    init {
        val parts = this.value.split("@")
        require(parts.size == 2) { "Invalid email: $value" }
        parts.forEach { require(it.isNotEmpty()) { "Invalid email: $value" } }
        user = parts[0]
        domain = parts[1]
    }

    override fun equals(other: Any?): Boolean {
        return other is Email && value == other.value
    }
    override fun hashCode() = value.hashCode()
}

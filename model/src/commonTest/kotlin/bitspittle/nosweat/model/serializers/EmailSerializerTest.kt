package bitspittle.nosweat.model.serializers

import bitspittle.nosweat.model.Email
import com.github.bitspittle.truthish.assertThat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class EmailSerializerTest {
    @Test
    fun canSerializeDeserializeEmail() {
        val email = Email("hey@there")
        val encoded = Json.encodeToString(email)
        assertThat(encoded).isEqualTo("\"hey@there\"")
        val decoded = Json.decodeFromString<Email>(encoded)
        assertThat(decoded).isEqualTo(email)
    }

}
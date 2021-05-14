package bitspittle.nosweat.model.serializers

import bitspittle.nosweat.model.Weight
import com.github.bitspittle.truthish.assertThat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class WeightSerializerTest {
    @Test
    fun canSerializeDeserializeWeights() {
        val pounds = Weight.Pounds(123.45f)
        val kilograms = Weight.Kilograms(123.45f)

        val encodedPounds = Json.encodeToString<Weight>(pounds)
        val encodedKilograms = Json.encodeToString<Weight>(kilograms)

        assertThat(encodedPounds).isEqualTo("""{"value":123.45,"type":"Pounds"}""")
        assertThat(encodedKilograms).isEqualTo("""{"value":123.45,"type":"Kilograms"}""")

        val decodedPounds = Json.decodeFromString<Weight>(encodedPounds) as Weight.Pounds
        val decodedKilograms = Json.decodeFromString<Weight>(encodedKilograms) as Weight.Kilograms

        assertThat(decodedPounds).isEqualTo(pounds)
        assertThat(decodedKilograms).isEqualTo(kilograms)
        assertThat(decodedPounds).isEqualTo(pounds)
        assertThat(decodedKilograms).isEqualTo(kilograms)
    }
}
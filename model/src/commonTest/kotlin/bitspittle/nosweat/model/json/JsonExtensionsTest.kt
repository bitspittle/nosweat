package bitspittle.nosweat.model.json

import bitspittle.nosweat.model.Exercise
import bitspittle.nosweat.model.ExerciseId
import bitspittle.nosweat.model.ExerciseName
import bitspittle.nosweat.model.Username
import com.github.bitspittle.truthish.assertThat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test

class JsonExtensionsTest {
    @Test
    fun testToFromMap() {
        val exercise = Exercise(
            ExerciseId("asdf1234"),
            ExerciseName("Pull up"),
            null,
            Username("user"),
        )

        val map = Json.encodeToJsonElement(exercise).toSimpleMap()
        assertThat(map.entries.map { it.key to it.value })
            .containsExactly(
                "id" to "\"asdf1234\"",
                "name" to "\"Pull up\"",
                "desc" to "null",
                "owner" to "\"user\""
            )

        assertThat(Json.decodeFromString<Exercise>(map.toJsonStr())).isEqualTo(exercise)
    }
}
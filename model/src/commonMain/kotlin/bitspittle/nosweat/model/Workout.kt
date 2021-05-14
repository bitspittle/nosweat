package bitspittle.nosweat.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val exercises: List<Exercise>
)

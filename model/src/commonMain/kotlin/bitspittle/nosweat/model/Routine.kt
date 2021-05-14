package bitspittle.nosweat.model

import kotlinx.serialization.Serializable

@Serializable
data class Routine(
    val workouts: List<Workout>
)

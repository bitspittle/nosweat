package bitspittle.nosweat.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
)

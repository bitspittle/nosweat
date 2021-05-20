package bitspittle.nosweat.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val secret: String,
    val name: String? = null,
    val email: String? = null,
)

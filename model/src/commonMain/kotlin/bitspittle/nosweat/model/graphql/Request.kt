package bitspittle.nosweat.model.graphql

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * A base class for sending GraphQL queries / mutations
 */
interface Request<T> {
    fun intoString(): String

    /**
     * Return a successfully parsed `T` or throw an exception
     */
    fun handleResponse(response: JsonObject): T
}

@Serializable
data class QueryResponse(
    val data: JsonObject? = null,
    val errors: List<JsonObject>? = null,
)

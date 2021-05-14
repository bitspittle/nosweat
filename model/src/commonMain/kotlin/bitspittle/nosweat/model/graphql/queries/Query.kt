package bitspittle.nosweat.model.graphql.queries

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * A base class for sending GraphQL queries
 */
interface Query<T> {
    fun toQueryString(): String

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

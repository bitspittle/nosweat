package bitspittle.nosweat.model.graphql

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

abstract class Messenger {
    protected class GraphQlException(message: String, cause: Throwable? = null) : Exception(message, cause)

    suspend fun <T> send(request: Request<T>): T {
        try {
            val responseStr = send(request.intoString())
            val response = Json.decodeFromString<QueryResponse>(responseStr)
            response.errors?.let { errors -> throw GraphQlException("Found errors in response: $errors") }

            return request.handleResponse(response.data!!)
        }
        catch (ex: GraphQlException) {
            throw ex
        }
        catch (t: Throwable) {
            throw GraphQlException("Unexpected exception", t)
        }
    }

    /**
     * Implementors should handle sending the payload to some destination and returning the response.
     *
     * If anything fails, children are encouraged to use [GraphQlException] for the exception.
     */
    protected abstract suspend fun send(query: String): String
}
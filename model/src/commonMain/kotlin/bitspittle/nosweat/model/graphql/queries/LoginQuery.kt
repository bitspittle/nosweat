package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.User
import bitspittle.nosweat.model.graphql.Request
import bitspittle.nosweat.model.json.decode
import bitspittle.nosweat.model.json.toPrimitiveContent
import kotlinx.serialization.json.JsonObject


sealed class LoginResponse
class LoginSuccess(val user: User, val secret: String) : LoginResponse()
class LoginError(val message: String) : LoginResponse()

data class LoginQuery(
    val username: String,
    val password: String,
) : Request<LoginResponse> {
    override fun intoString(): String {
        return """
            query LoginQuery {
                login(username: "$username", password: "$password") {
                    ... on LoginSuccess {
                        user {
                            id
                            username
                        }
                        secret
                    }
                    ... on LoginError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): LoginResponse {
        val response = response["login"] as JsonObject
        return response["user"]?.let { userElement ->
            LoginSuccess(userElement.decode(), response["secret"].toPrimitiveContent())
        } ?: LoginError(response["message"].toPrimitiveContent())
    }
}


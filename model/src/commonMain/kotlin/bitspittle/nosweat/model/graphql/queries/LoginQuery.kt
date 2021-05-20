package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.User
import bitspittle.nosweat.model.graphql.Request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement


sealed class LoginResponse
class LoginSuccess(val user: User) : LoginResponse()
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
                            username
                            secret
                            name
                            email
                        }
                    }
                    ... on LoginError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): LoginResponse {
        val loginResponse = response["login"] as JsonObject
        return loginResponse["user"]?.let {
            LoginSuccess(Json.decodeFromJsonElement(it))
        } ?: LoginError((loginResponse["message"] as JsonPrimitive).content)
    }
}


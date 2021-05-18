package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.User
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement


sealed class LoginResponse {
    class Success(val user: User) : LoginResponse()
    class Error(val message: String) : LoginResponse()
}

data class LoginQuery(
    val username: String,
    val password: String,
) : Query<LoginResponse> {
    override fun toQueryString(): String {
        return """
            query LoginQuery {
                login(username: "$username", password: "$password") {
                    ... on Success {
                        user {
                            id
                            username
                            name
                            email { value }
                        }
                    }
                    ... on Error {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): LoginResponse {
        val loginResponse = response["login"] as JsonObject
        return loginResponse["user"]?.let {
            LoginResponse.Success(Json.decodeFromJsonElement(it))
        } ?: LoginResponse.Error((loginResponse["message"] as JsonPrimitive).content)
    }
}


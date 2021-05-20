package bitspittle.nosweat.model.graphql.mutations

import bitspittle.nosweat.model.User
import bitspittle.nosweat.model.graphql.Request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement


sealed class CreateAccountResponse
class CreateAccountSuccess(val user: User) : CreateAccountResponse()
class CreateAccountError(val message: String) : CreateAccountResponse()

data class CreateAccountMutation(
    val username: String,
    val password: String,
) : Request<CreateAccountResponse> {
    override fun intoString(): String {
        return """
            mutation CreateAccountMutation {
                createAccount(username: "$username", password: "$password") {
                    ... on CreateAccountSuccess {
                        user {
                            username
                            secret
                            name
                            email
                        }
                    }
                    ... on CreateAccountError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): CreateAccountResponse {
        val loginResponse = response["createAccount"] as JsonObject
        return loginResponse["user"]?.let {
            CreateAccountSuccess(Json.decodeFromJsonElement(it))
        } ?: CreateAccountError((loginResponse["message"] as JsonPrimitive).content)
    }
}


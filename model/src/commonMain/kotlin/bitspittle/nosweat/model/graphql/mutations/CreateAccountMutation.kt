package bitspittle.nosweat.model.graphql.mutations

import bitspittle.nosweat.model.User
import bitspittle.nosweat.model.graphql.Request
import bitspittle.nosweat.model.json.toPrimitiveContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement


sealed class CreateAccountResponse
class CreateAccountSuccess(val user: User, val secret: String) : CreateAccountResponse()
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
                            name
                            email
                        }
                        secret
                    }
                    ... on CreateAccountError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): CreateAccountResponse {
        val response = response["createAccount"] as JsonObject
        return response["user"]?.let {
            CreateAccountSuccess(Json.decodeFromJsonElement(it), response["secret"].toPrimitiveContent())
        } ?: CreateAccountError(response["message"].toPrimitiveContent())
    }
}


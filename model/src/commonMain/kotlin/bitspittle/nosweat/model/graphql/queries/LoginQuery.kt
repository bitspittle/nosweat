package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.User
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement


data class LoginQuery(
    val username: String,
    val password: String,
) : Query<User> {
    override fun toQueryString(): String {
        return """
            query LoginQuery {
                login(username: "$username", password: "$password") {
                    id
                    username
                    name
                    email { value }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): User {
        return Json.decodeFromJsonElement(response["login"]!!)
    }
}
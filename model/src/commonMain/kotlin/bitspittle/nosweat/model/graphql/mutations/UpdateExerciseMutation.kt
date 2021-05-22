package bitspittle.nosweat.model.graphql.mutations

import bitspittle.nosweat.model.User
import bitspittle.nosweat.model.graphql.Request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement


sealed class UpdateExerciseResponse
object UpdateExerciseSuccess : UpdateExerciseResponse()
object UpdateExerciseError : UpdateExerciseResponse()

data class UpdateExerciseMutation(val secret: String) : Request<UpdateExerciseResponse> {
    override fun intoString(): String {
        return """
            mutation UpdateExerciseMutation {
                updateExercise(username: "$secret") {
                    ... on UpdateExerciseSuccess {
                    }
                    ... on UpdateExerciseError {
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): UpdateExerciseResponse {
        val loginResponse = response["createAccount"] as JsonObject
        return loginResponse["user"]?.let {
            UpdateExerciseSuccess
        } ?: UpdateExerciseError
    }
}


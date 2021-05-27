package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.graphql.Request
import bitspittle.nosweat.model.json.decode
import kotlinx.serialization.json.JsonObject

sealed class ExercisesResponse
class ExercisesSuccess(val ids: Collection<String>) : ExercisesResponse()
class ExercisesError(val message: String) : ExercisesResponse()


data class ExercisesQuery(
    val userId: String,
) : Request<ExercisesResponse> {
    override fun intoString(): String {
        return """
            query ExercisesQuery {
                exercises(userId: "$userId") {
                    ... on ExercisesSuccess {
                        ids
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): ExercisesResponse {
        val response = response["exercises"] as JsonObject
        return ExercisesSuccess(response["ids"].decode())
    }
}


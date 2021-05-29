package bitspittle.nosweat.model.graphql.queries

import bitspittle.nosweat.model.Exercise
import bitspittle.nosweat.model.graphql.Properties
import bitspittle.nosweat.model.graphql.Request
import bitspittle.nosweat.model.json.decode
import bitspittle.nosweat.model.json.toPrimitiveContent
import kotlinx.serialization.json.JsonObject

sealed class ExerciseResponse
class ExerciseSuccess(val exercise: Exercise) : ExerciseResponse()
class ExerciseError(val message: String) : ExerciseResponse()

data class ExerciseQuery(
    val exerciseId: String,
) : Request<ExerciseResponse> {
    override fun intoString(): String {
        return """
            query ExerciseQuery {
                exercise(exerciseId: "$exerciseId") {
                    ... on ExerciseSuccess {
                        ${Properties.EXERCISE}
                    }
                    ... on ExerciseError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): ExerciseResponse {
        val response = response["exercise"] as JsonObject
        return response["exercise"]?.let { exerciseElement ->
            ExerciseSuccess(exerciseElement.decode())
        } ?: ExerciseError(response["message"].toPrimitiveContent())
    }
}


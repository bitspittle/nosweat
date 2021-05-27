package bitspittle.nosweat.model.graphql.mutations

import bitspittle.nosweat.model.Exercise
import bitspittle.nosweat.model.graphql.Request
import bitspittle.nosweat.model.json.decode
import bitspittle.nosweat.model.json.toPrimitiveContent
import kotlinx.serialization.json.JsonObject

sealed class CreateExerciseResponse
class CreateExerciseSuccess(val exercise: Exercise) : CreateExerciseResponse()
class CreateExerciseError(val message: String) : CreateExerciseResponse()

data class CreateExerciseMutation(val secret: String, val name: String, val desc: String) : Request<CreateExerciseResponse> {
    override fun intoString(): String {
        return """
            mutation CreateExerciseMutation {
                createExercise(secret: "$secret", name: "$name", desc: "$desc") {
                    ... on CreateExerciseSuccess {
                        exercise {
                            id
                            name
                            desc
                            owner
                        }
                    }
                    ... on CreateExerciseError {
                        message
                    }
                }
            }
        """.trimIndent()
    }

    override fun handleResponse(response: JsonObject): CreateExerciseResponse {
        val response = response["createExercise"] as JsonObject

        return response["exercise"]?.let { exerciseElement ->
            CreateExerciseSuccess(exerciseElement.decode())
        } ?: CreateExerciseError(response["message"].toPrimitiveContent())
    }
}


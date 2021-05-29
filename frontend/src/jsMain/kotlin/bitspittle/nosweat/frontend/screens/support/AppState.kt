package bitspittle.nosweat.frontend.screens.support

import bitspittle.nosweat.model.User

data class AppState(
    val credentials: Credentials? = null,
    val createAccount: CreateAccount = CreateAccount(),
    val editExercise: EditExercise = EditExercise(),
) {
    class Credentials(
        val user: User,
        val secret: String
    )

    class CreateAccount(
        val username: String? = null,
        val password: String? = null,
    )
    class EditExercise(
        val exerciseId: String? = null,
    )
}

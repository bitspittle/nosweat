package bitspittle.nosweat.frontend.screens.support

import bitspittle.nosweat.model.User

data class AppState(
    val defaults: Defaults = Defaults(),
    val loggedIn: LoggedIn? = null,
) {
    data class Defaults(
        val createAccount: CreateAccount = CreateAccount()
    ) {
        class CreateAccount(
            var username: String? = null,
            var password: String? = null,
        )
    }

    data class LoggedIn(
        val user: User,
        val secret: String
    )
}

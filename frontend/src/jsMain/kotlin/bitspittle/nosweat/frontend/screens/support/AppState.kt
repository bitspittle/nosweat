package bitspittle.nosweat.frontend.screens.support

import bitspittle.nosweat.model.User

data class AppState(
    val screens: Screens = Screens(),
    val credentials: Credentials? = null,
) {
    data class Screens(
        val createAccount: CreateAccount = CreateAccount()
    ) {
        class CreateAccount(
            var username: String? = null,
            var password: String? = null,
        )
    }

    data class Credentials(
        val user: User,
        val secret: String
    )
}

package bitspittle.nosweat.frontend.screens.support

import bitspittle.nosweat.model.User

class AppState(
    val defaults: Defaults = Defaults(),
    var user: User? = null,
) {
    class Defaults(
        val createAccount: CreateAccount = CreateAccount()
    ) {
        class CreateAccount(
            var username: String? = null,
            var password: String? = null,
        )
    }
}

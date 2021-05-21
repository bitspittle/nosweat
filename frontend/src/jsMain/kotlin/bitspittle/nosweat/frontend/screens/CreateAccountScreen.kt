package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.attributes.disabled
import androidx.compose.web.elements.*
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.screens.support.Screen
import bitspittle.nosweat.frontend.style.AppStylesheet
import bitspittle.nosweat.model.graphql.mutations.CreateAccountError
import bitspittle.nosweat.model.graphql.mutations.CreateAccountMutation
import bitspittle.nosweat.model.graphql.mutations.CreateAccountSuccess
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun CreateAccountScreen(ctx: Context) {
    var username by remember {
        mutableStateOf(ctx.state.defaults.createAccount.username ?: "")
            .also { ctx.state.defaults.createAccount.username = null }
    }
    var password1 by remember {
        mutableStateOf(ctx.state.defaults.createAccount.password ?: "")
            .also { ctx.state.defaults.createAccount.password = null }
    }
    var password2 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Row {
        Column {
            Text("Username")
            Input(
                InputType.Text,
                username,
                attrs = {
                    onTextInput { event -> username = event.inputValue.trim() }
                }
            )
        }
        Column {
            Text("Password")
            Input(
                InputType.Password,
                password1,
                attrs = {
                    onTextInput { event -> password1 = event.inputValue }
                }

            )
        }
        Column {
            Text("Repeat password")
            Input(
                InputType.Password,
                password2,
                attrs = {
                    onTextInput { event -> password2 = event.inputValue }
                }
            )
        }
        Div {
            Button(attrs = {
                disabled(username.isEmpty() || password1.isEmpty() || password1 != password2)
                onClick {
                    scope.launch {
                        errorMessage = ""
                        when (val result = ctx.messenger.send(CreateAccountMutation(username, password1))) {
                            is CreateAccountSuccess -> {
                                ctx.state.user = result.user
                                ctx.navigator.enter(Screen.Home)
                            }
                            is CreateAccountError -> errorMessage = result.message
                        }
                    }
                }
            }) {
                Text("Create Account")
            }
        }
        Div {
            Button(attrs = {
                onClick { ctx.navigator.back() }
            }) {
                Text("Back")
            }
        }

        if (errorMessage.isNotBlank()) {
            Div(attrs = { classes(AppStylesheet.error) }) {
                Text(errorMessage)
            }
        }
    }
}

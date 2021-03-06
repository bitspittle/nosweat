package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.attributes.disabled
import androidx.compose.web.elements.*
import bitspittle.nosweat.frontend.screens.support.AppState
import bitspittle.nosweat.frontend.screens.support.AppState.Credentials
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.screens.support.Screen
import bitspittle.nosweat.frontend.screens.support.swapWith
import bitspittle.nosweat.frontend.style.AppStylesheet
import bitspittle.nosweat.model.graphql.queries.LoginError
import bitspittle.nosweat.model.graphql.queries.LoginQuery
import bitspittle.nosweat.model.graphql.queries.LoginSuccess
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun LoginScreen(ctx: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                password,
                attrs = {
                    onTextInput { event -> password = event.inputValue }
                }
            )
        }
        Div {
            Button(attrs = {
                disabled(username.isEmpty() || password.isEmpty())
                onClick {
                    errorMessage = ""
                    scope.launch {
                        when (val result = ctx.messenger.send(LoginQuery(username, password))) {
                            is LoginSuccess -> {
                                ctx.state = ctx.state.copy(credentials = Credentials(result.user, result.secret))
                                ctx.navigator.swapWith(Screen.Home)
                            }
                            is LoginError -> errorMessage = result.message
                        }
                    }
                }
            }) {
                Text("Log In")
            }
        }

        if (errorMessage.isNotBlank()) {
            Div {
                Span(attrs = { classes(AppStylesheet.error) }) {
                    Text(errorMessage)
                }
                Text(" - ")
                A(
                    attrs = {
                        classes(AppStylesheet.clickable)
                        onClick {
                            ctx.state = ctx.state.copy(createAccount = AppState.CreateAccount(username, password))
                            ctx.navigator.swapWith(Screen.CreateAccount)
                        }
                    }) {
                    Text("Create account?")
                }
            }
        }
    }
}

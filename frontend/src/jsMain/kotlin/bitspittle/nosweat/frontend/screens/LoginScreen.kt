package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Input
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.ApplicationScope
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.model.graphql.queries.LoginQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.json

@Composable
fun LoginScreen(ctx: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = remember { ApplicationScope() }

    Row {
        Column {
            Text("Username")
            Input(
                InputType.Text,
                username,
                attrs = {
                    onTextInput { event -> username = event.inputValue }
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
        Button(attrs = {
            onClick {
                scope.launch {
                    val user = ctx.messenger.send(LoginQuery(username, password))
                    // TODO: Go to a new screen with the logged in user
                }
            }
        }) {
            Text("Log In")
        }
        Button(attrs = {
            onClick { ctx.navigator.back() }
        }) {
            Text("Back")
        }
    }
}

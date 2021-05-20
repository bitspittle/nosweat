package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.Composable
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.Context
import org.jetbrains.compose.common.foundation.layout.Row
import bitspittle.nosweat.frontend.screens.support.Screen
import bitspittle.nosweat.frontend.screens.support.ScreenNavigator
import bitspittle.nosweat.frontend.style.AppStylesheet

@Composable
fun MainScreen(ctx: Context) {
    Row {
        Div(attrs = { classes(AppStylesheet.title) }) {
            Text("No Sweat \uD83E\uDD75")
        }
        Div(attrs = { classes(AppStylesheet.subtitle) }) {
            Text("An Exercise Tracker")
        }
        Div {
            Button(attrs = {
                onClick { ctx.navigator.enter(Screen.Login) }
            }) {
                Text("Log In")
            }
        }
        Div {
            Button(attrs = {
                onClick { }
            }) {
                Text("Create Account")
            }
        }
    }
}
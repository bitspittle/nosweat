package screens

import androidx.compose.runtime.Composable
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Text
import org.jetbrains.compose.common.foundation.layout.Row
import screens.nav.Screen
import screens.nav.ScreenNavigator
import style.AppStylesheet

@Composable
fun MainScreen(navigator: ScreenNavigator) {
    Row {
        Div(attrs = { classes(AppStylesheet.title) }) {
            Text("No Sweat \uD83E\uDD75")
        }
        Div(attrs = { classes(AppStylesheet.subtitle) }) {
            Text("An Exercise Tracker")
        }
        Div {
            Button(attrs = {
                onClick { navigator.enter(Screen.Login) }
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

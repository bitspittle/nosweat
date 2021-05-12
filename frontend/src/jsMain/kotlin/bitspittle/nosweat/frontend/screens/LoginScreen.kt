package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.Composable
import androidx.compose.web.attributes.InputType
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Input
import androidx.compose.web.elements.Text
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row
import bitspittle.nosweat.frontend.screens.nav.ScreenNavigator

@Composable
fun LoginScreen(navigator: ScreenNavigator) {
    Row {
        Column {
            Text("Username")
            Input(InputType.Text)
        }
        Column {
            Text("Password")
            Input(InputType.Password)
        }
        Button(attrs = {
            onClick { navigator.back() }
        }) {
            Text("Log In")
        }
    }
}

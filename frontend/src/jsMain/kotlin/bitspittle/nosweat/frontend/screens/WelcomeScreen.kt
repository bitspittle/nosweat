package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.Context
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun WelcomeScreen(ctx: Context) {
    val user = remember { requireNotNull(ctx.state.user) }

    Row {
        Div {
            Text("Welcome ${user.username}")
        }
        Div {
            Button(attrs = {
                onClick {
                    ctx.state.user = null
                    ctx.navigator.back()
                }
            }) {
                Text("Back")
            }
        }
    }
}

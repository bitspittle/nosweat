package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.screens.support.Screen
import bitspittle.nosweat.frontend.screens.support.swapWith
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun HomeScreen(ctx: Context) {
    val loggedIn = remember { requireNotNull(ctx.state.loggedIn) }
    val scope = rememberCoroutineScope()

    Row {
        Div {
            Text("Welcome ${loggedIn.user.username}")
        }
        Div {
            Button(attrs = {
                onClick {
                    scope.launch {
                        ctx.navigator.enter(Screen.EditExercise)
                    }
                }
            }) {
                Text("Create exercise")
            }
        }

    }
}

package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.screens.support.Screen
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun HomeScreen(ctx: Context) {
    val credentials = remember { requireNotNull(ctx.state.credentials) }
    val scope = rememberCoroutineScope()

    Row {
        Div {
            Text("Welcome ${credentials.user.username}")
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

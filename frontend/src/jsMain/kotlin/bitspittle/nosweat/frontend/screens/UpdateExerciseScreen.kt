package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.attributes.disabled
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Input
import androidx.compose.web.elements.Text
import bitspittle.nosweat.frontend.screens.support.Context
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun UpdateExerciseScreen(ctx: Context) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Row {
        Column {
            Text("Name (*)")
            Input(
                InputType.Text,
                name,
                attrs = {
                    onTextInput { event -> name = event.inputValue.trim() }
                }
            )
        }
        Column {
            Text("Description")
            Input(
                InputType.Text,
                description,
                attrs = {
                    onTextInput { event -> description = event.inputValue.trim() }
                }
            )
        }
        Div {
            Button(attrs = {
                disabled(name.isEmpty())
                onClick {
                    scope.launch {
//                        when (val result = ctx.messenger.send(UpdateExerciseMutation("")) {
//                        }
                    }
                }
            }) {
                Text("Create Account")
            }
        }
    }
}

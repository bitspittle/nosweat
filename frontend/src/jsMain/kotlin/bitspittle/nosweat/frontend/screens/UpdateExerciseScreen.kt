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
import bitspittle.nosweat.model.graphql.mutations.UpdateExerciseMutation
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
        Div {
            Button(attrs = {
                onClick { ctx.navigator.back() }
            }) {
                Text("Back")
            }
        }
    }
}

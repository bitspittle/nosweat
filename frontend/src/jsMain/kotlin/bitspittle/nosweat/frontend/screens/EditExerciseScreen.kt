package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.attributes.disabled
import androidx.compose.web.elements.*
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.style.AppStylesheet
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseError
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseMutation
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseSuccess
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun EditExerciseScreen(ctx: Context) {
    val credentials = remember { requireNotNull(ctx.state.credentials) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // TODO, support configuring with existing exercise

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
                        when (val result = ctx.messenger.send(CreateExerciseMutation(credentials.secret, name, description))) {
                            is CreateExerciseSuccess -> {
                                ctx.navigator.back()
                            }

                            is CreateExerciseError -> {
                                errorMessage = result.message
                            }
                        }
                    }
                }
            }) {
                Text("Submit")
            }
        }

        if (errorMessage.isNotBlank()) {
            Div {
                Span(attrs = { classes(AppStylesheet.error) }) {
                    Text(errorMessage)
                }
            }
        }
    }
}

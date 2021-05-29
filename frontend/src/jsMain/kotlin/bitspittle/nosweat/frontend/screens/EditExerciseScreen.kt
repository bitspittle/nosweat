package bitspittle.nosweat.frontend.screens

import androidx.compose.runtime.*
import androidx.compose.web.attributes.InputType
import androidx.compose.web.attributes.disabled
import androidx.compose.web.elements.*
import bitspittle.nosweat.frontend.screens.support.AppState
import bitspittle.nosweat.frontend.screens.support.Context
import bitspittle.nosweat.frontend.style.AppStylesheet
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseError
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseMutation
import bitspittle.nosweat.model.graphql.mutations.CreateExerciseSuccess
import bitspittle.nosweat.model.graphql.queries.ExerciseError
import bitspittle.nosweat.model.graphql.queries.ExerciseQuery
import bitspittle.nosweat.model.graphql.queries.ExerciseSuccess
import bitspittle.nosweat.model.graphql.queries.ExercisesQuery
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.common.foundation.layout.Row

@Composable
fun EditExerciseScreen(ctx: Context) {
    val credentials = remember { requireNotNull(ctx.state.credentials) }
    val screenState = remember { ctx.state.editExercise }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        screenState.exerciseId?.let { exreciseId ->
            when (val result = ctx.messenger.send(ExerciseQuery(exreciseId))) {
                is ExerciseSuccess -> {
                    name = result.exercise.name
                    description = result.exercise.desc ?: ""
                    errorMessage = ""
                }
                is ExerciseError -> {
                    errorMessage = result.message
                }
            }
        }
    }

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
                        when (val result =
                            ctx.messenger.send(CreateExerciseMutation(credentials.secret, name, description))) {
                            is CreateExerciseSuccess -> {
                                // Save ID so if user presses "forward" we'll repopulate this screen
                                ctx.state = ctx.state.copy(editExercise = AppState.EditExercise(result.exercise.id) )
                                ctx.navigator.back()
                            }

                            is CreateExerciseError -> {
                                errorMessage = result.message
                            }
                        }
                    }
                }
            }) {
                Text("Save")
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

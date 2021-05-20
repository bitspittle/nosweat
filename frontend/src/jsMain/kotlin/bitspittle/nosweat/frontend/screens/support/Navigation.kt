package bitspittle.nosweat.frontend.screens.support

import androidx.compose.runtime.*
import androidx.compose.web.css.Style
import androidx.compose.web.elements.Section
import androidx.compose.web.renderComposable
import bitspittle.nosweat.frontend.graphql.HttpMessenger
import bitspittle.nosweat.frontend.screens.LoginScreen
import bitspittle.nosweat.frontend.screens.MainScreen
import bitspittle.nosweat.frontend.style.AppStylesheet
import kotlin.math.min

interface ScreenNavigator {
    fun enter(screen: Screen, popCount: Int = 0)
    fun back()
}

sealed class Screen {
    @Composable
    internal abstract fun compose(ctx: Context)

    object Main : Screen() {
        @Composable
        override fun compose(ctx: Context) = MainScreen(ctx)
    }

    object Login : Screen() {
        @Composable
        override fun compose(ctx: Context) = LoginScreen(ctx)
    }

    object CreateAccount : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }

    object Overview : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }

    object Workout : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }

    object WorkoutComplete : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }
}

private class ScreenNavigatorImpl(initialScreen: Screen) : ScreenNavigator {
    private val backStack = mutableListOf(initialScreen)
    private var _activeScreen = mutableStateOf(initialScreen)
    val activeScreen: State<Screen> = _activeScreen

    override fun enter(screen: Screen, popCount: Int) {
        require(popCount >= 0) { "Invalid pop count: $popCount"}

        for (i in 0 until min(popCount, backStack.size)) backStack.removeLast()
        backStack.add(screen)
        _activeScreen.value = screen
    }

    override fun back() {
        if (backStack.size >= 2) {
            backStack.removeLast()
            _activeScreen.value = backStack.last()
        }
    }
}

fun startApp() {
    val navigator = ScreenNavigatorImpl(Screen.Main)
    val ctx = Context(navigator, HttpMessenger())

    renderComposable(rootElementId = "root") {
        val activeScreen by navigator.activeScreen

        Style(AppStylesheet)
        Section(attrs = { classes(AppStylesheet.container) }) {
            activeScreen.compose(ctx)
        }
    }
}
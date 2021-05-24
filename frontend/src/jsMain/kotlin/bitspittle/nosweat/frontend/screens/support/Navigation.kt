package bitspittle.nosweat.frontend.screens.support

import androidx.compose.runtime.*
import androidx.compose.web.css.*
import androidx.compose.web.css.selectors.attr
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Section
import androidx.compose.web.elements.Span
import androidx.compose.web.elements.Text
import androidx.compose.web.renderComposable
import bitspittle.nosweat.frontend.graphql.HttpMessenger
import bitspittle.nosweat.frontend.screens.CreateAccountScreen
import bitspittle.nosweat.frontend.screens.HomeScreen
import bitspittle.nosweat.frontend.screens.LoginScreen
import bitspittle.nosweat.frontend.screens.TitleScreen
import bitspittle.nosweat.frontend.style.AppStylesheet
import bitspittle.nosweat.frontend.style.Cursor
import bitspittle.nosweat.frontend.style.cursor
import bitspittle.nosweat.model.graphql.Messenger
import kotlin.math.min

/**
 * Value you can pass into [ScreenNavigator.enter] if you want to clear all screens before entering the new one.
 * This is useful if you want to reset the state back to some root screen, e.g. pressing the "home" button.
 */
const val POP_ALL = Int.MAX_VALUE

interface ScreenNavigator {
    val canGoBack: Boolean
    val canGoForward: Boolean

    fun enter(screen: Screen, popCount: Int = 0)
    fun back()
    fun forward()
}

fun ScreenNavigator.swapWith(screen: Screen) = enter(screen, popCount = 1)

sealed class Screen {
    @Composable
    internal abstract fun compose(ctx: Context)

    object Title : Screen() {
        @Composable
        override fun compose(ctx: Context) = TitleScreen(ctx)
    }

    object Login : Screen() {
        @Composable
        override fun compose(ctx: Context) = LoginScreen(ctx)
    }

    object CreateAccount : Screen() {
        @Composable
        override fun compose(ctx: Context) = CreateAccountScreen(ctx)
    }

    object Home : Screen() {
        @Composable
        override fun compose(ctx: Context) = HomeScreen(ctx)
    }

    object Workout : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }

    object WorkoutSummary : Screen() {
        @Composable
        override fun compose(ctx: Context) = Unit
    }
}

private class ScreenNavigatorImpl(messenger: Messenger, initialScreen: Screen, initialState: AppState = AppState()) :
    ScreenNavigator {
    val ctx = Context(this, messenger, initialState)

    private val backStack = mutableListOf(initialScreen to initialState)
    private val forwardStack = mutableListOf<Pair<Screen, AppState>>()

    private var _activeScreen = mutableStateOf(initialScreen)
    val activeScreen: State<Screen> = _activeScreen

    private fun updateActiveScreen(screen: Screen, state: AppState? = null) {
        if (state != null) {
            ctx.state = state
        }
        _activeScreen.value = screen
    }

    override val canGoBack get() = backStack.size > 1
    override val canGoForward get() = forwardStack.isNotEmpty()

    override fun enter(screen: Screen, popCount: Int) {
        require(popCount >= 0) { "Invalid pop count: $popCount" }

        backStack.add(_activeScreen.value to ctx.state)
        for (i in 0 until min(popCount, backStack.size)) backStack.removeLast()
        forwardStack.clear()

        updateActiveScreen(screen)
    }

    override fun back() {
        if (backStack.size <= 1) return

        forwardStack.add(activeScreen.value to ctx.state)

        val savedScreenState = backStack.removeLast()
        updateActiveScreen(savedScreenState.first, savedScreenState.second)
    }

    override fun forward() {
        if (forwardStack.isEmpty()) return

        backStack.add(_activeScreen.value to ctx.state)

        val savedScreenState = forwardStack.removeFirst()
        updateActiveScreen(savedScreenState.first, savedScreenState.second)
    }
}

@Composable
private fun SideArrow(
    navigator: ScreenNavigatorImpl,
    label: String,
    flexGrow: Int,
    isActive: () -> Boolean,
    onClick: () -> Unit
) {
    // If the active screen has changed, so has back/forward state
    navigator.activeScreen.recomposeWhenChanged()
    Section(
        attrs = {
            classes(AppStylesheet.container)
            onClick { onClick() }
        },
        style = {
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            flexGrow(flexGrow)
        }) {
        Span(
            attrs = {
                classes(AppStylesheet.sideArrow)
            },
            style = {
                isActive().let { isActive ->
                    opacity(if (isActive) 100.percent else 10.percent)
                    cursor(if (isActive) Cursor.POINTER else Cursor.DEFAULT)
                }
            }
        ) {
            Text(label)
        }
    }
}

fun startApp() {
    val navigator = ScreenNavigatorImpl(HttpMessenger(), Screen.Title)

    val mainContentPercent = 90
    val sideContentPercent = (100 - mainContentPercent) / 2

    renderComposable(rootElementId = "root") {
        val activeScreen by navigator.activeScreen

        Style(AppStylesheet)

        Section(
            attrs = { classes(AppStylesheet.container) },
            style = {
                height(100.percent)
            }
        ) {
            SideArrow(navigator, "<", sideContentPercent, { navigator.canGoBack }, { navigator.back() })
            Section(
                attrs = {
                    classes(AppStylesheet.container)
                },
                style = {
                    justifyContent(JustifyContent.Center)
                    flexGrow(mainContentPercent)
                }
            ) {
                activeScreen.compose(navigator.ctx)
            }
            SideArrow(navigator, ">", sideContentPercent, { navigator.canGoForward }, { navigator.forward() })
        }
    }
}

private fun <T : Any> State<T>.recomposeWhenChanged() {
    val value by this
    value.hashCode() // No-op without side effect but triggers recomposition
}

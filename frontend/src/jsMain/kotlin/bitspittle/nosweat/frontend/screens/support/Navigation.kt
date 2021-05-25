package bitspittle.nosweat.frontend.screens.support

import androidx.compose.runtime.*
import androidx.compose.web.css.*
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

interface ScreenNavigator {
    val canGoBack: Boolean
    val canGoForward: Boolean

    /**
     * Enter a new screen.
     *
     * Before entering, however, remove items from the backstack until [popWhile] return false.
     * This is useful, for example, if you have a series of screens where you are collecting
     * information from the user that, once submitted, you don't want to leave in the back stack
     * (think, in a game, going from title screen through player options to starting the game --
     * when you exit the game, you should go back to the title screen.
     */
    fun enter(screen: Screen, popWhile: (Screen) -> Boolean = { false })
    fun back()
    fun forward()
}

fun ScreenNavigator.enter(screen: Screen, popCount: Int) {
    require(popCount >= 1) { "Invalid pop count: $popCount" }

    var popCount = popCount
    enter(screen) {
        popCount--
        popCount >= 0
    }
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

    override fun enter(screen: Screen, popWhile: (Screen) -> Boolean) {
        backStack.add(_activeScreen.value to ctx.state)
        while (backStack.isNotEmpty() && popWhile(backStack.last().first)) {
            backStack.removeLast()
        }
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

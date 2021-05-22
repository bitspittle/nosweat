package bitspittle.nosweat.frontend.screens.support

import bitspittle.nosweat.model.graphql.Messenger

class Context(
    val navigator: ScreenNavigator,
    val messenger: Messenger,
    /**
     * This will be snapshotted when entering a screen.
     *
     * Overwrite with `state = state.copy(...)` if you need to change some values.
     */
    var state: AppState,
)
package bitspittle.nosweat.frontend.style

import androidx.compose.web.css.StyleBuilder
import androidx.compose.web.css.value

fun StyleBuilder.fontFamily(value: String) {
    property("font-family", value(value))
}

enum class FontStyle(val value: String) {
    NORMAL("normal"),
    ITALIC("italic"),
}

fun StyleBuilder.fontStyle(style: FontStyle) {
    property("font-style", value(style.value))
}

enum class FontWeight(val value: String) {
    NORMAL("normal"),
    BOLD("bold"),
    LIGHTER("lighter"),
    BOLDER("bolder"),
}

fun StyleBuilder.fontWeight(weight: FontWeight) {
    property("font-weight", value(weight.value))
}

fun StyleBuilder.fontWeight(value: Int) {
    require(value in 1..1000) { "Font weight must be between 1 and 1000. Got: $value" }
    property("font-weight", value(value.toString()))
}

enum class TextAlign(val value: String) {
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    JUSTIFY("justify"),
    JUSTIFY_ALL("justify-all"),
    START("start"),
    END("end"),
    MATCH_PARENT("match-parent"),
}

fun StyleBuilder.textAlign(textAlign: TextAlign) {
    property("text-align", value(textAlign.value))
}

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
enum class Cursor(val value: String) {
    // General
    AUTO("auto"),
    DEFAULT("default"),
    NONE("none"),

    // Links and status
    CONTEXT_MENU("context-menu"),
    HELP("help"),
    POINTER("pointer"),
    PROGRESS("progress"),
    WAIT("wait"),

    // Selection
    CELL("cell"),
    CROSSHAIR("crosshair"),
    TEXT("text"),
    VERTICAL_TEXT("vertical-text"),

    // Drag and drop
    ALIAS("alias"),
    COPY("copy"),
    MOVE("move"),
    NO_DROP("no-drop"),
    NOT_ALLOWED("not-allowed"),
    GRAB("grab"),
    GRABBING("grabbing"),

    // Resizing and scrolling
    ALL_SCROLL("all-scroll"),
    COLUMN_RESIZE("col-resize"),
    ROW_RESIZE("row-resize"),
    N_RESIZE("n-resize"),
    NE_RESIZE("ne-resize"),
    E_RESIZE("e-resize"),
    SE_RESIZE("se-resize"),
    S_RESIZE("s-resize"),
    SW_RESIZE("sw-resize"),
    W_RESIZE("w-resize"),
    NW_RESIZE("nw-resize"),
    EW_RESIZE("ew-resize"),
    NS_RESIZE("ns-resize"),
    NESW_RESIZE("nesw-resize"),
    NWSE_RESIZE("nwse-resize"),

    // Zoom
    ZOOM_IN("zoom-in"),
    ZOOM_OUT("zoom-out"),
}

fun StyleBuilder.cursor(cursor: Cursor) {
    property("cursor", value(cursor.value))
}
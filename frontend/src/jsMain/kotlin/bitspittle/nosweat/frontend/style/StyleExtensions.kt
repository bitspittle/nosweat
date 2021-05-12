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
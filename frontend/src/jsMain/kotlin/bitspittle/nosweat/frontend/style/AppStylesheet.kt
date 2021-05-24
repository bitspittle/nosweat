package bitspittle.nosweat.frontend.style

import androidx.compose.web.css.*

object AppStylesheet : StyleSheet() {
    init {
        "html, body, #root" style {
            height(100.percent)
            margin(0.px)
        }
    }


    val container by style {
        display(DisplayStyle.Flex)
    }

    val title by style {
        fontSize(45.px)
        fontWeight(FontWeight.BOLD)
        textAlign(TextAlign.CENTER)
    }

    val subtitle by style {
        fontSize(30.px)
        fontWeight(FontWeight.LIGHTER)
        textAlign(TextAlign.CENTER)
    }

    val sideArrow by style {
        fontSize(80.px)
        fontWeight(FontWeight.BOLD)
        textAlign(TextAlign.CENTER)
    }

    val clickable by style {
        cursor(Cursor.POINTER)
        color(Color("blue"))
    }

    val error by style {
        color(Color("red"))
    }
}
package style

import androidx.compose.web.css.*

object AppStylesheet : StyleSheet() {
    val container by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
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
}
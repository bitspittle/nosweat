package bitspittle.nosweat.model.json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

fun JsonElement?.toPrimitiveContent() = (this as JsonPrimitive).content


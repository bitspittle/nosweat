package bitspittle.nosweat.model.json

import bitspittle.nosweat.model.Exercise
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.*

fun JsonElement?.toPrimitiveContent() = (this as JsonPrimitive).content

inline fun <reified T> JsonElement?.decode(): T {
    return Json.decodeFromJsonElement(this!!)
}

fun JsonElement?.toSimpleMap(): Map<String, String> {
    return (this as JsonObject).mapValues { entry -> entry.value.toString() }
}

fun Map<String, String>.toJsonStr(): String {
    return buildString {
        append('{')
        append(entries.joinToString { entry -> "\"${entry.key}\" : ${entry.value}" })
        append('}')
    }
}

inline fun <reified T> Map<String, String>.decode(serializer: KSerializer<T>): T {
    return Json.decodeFromString(serializer, this.toJsonStr())
}

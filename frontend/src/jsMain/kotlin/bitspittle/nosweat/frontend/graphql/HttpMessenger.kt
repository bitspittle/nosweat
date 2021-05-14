package bitspittle.nosweat.frontend.graphql

import bitspittle.nosweat.model.graphql.Messenger
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

class HttpMessenger : Messenger() {
    override suspend fun send(query: String): String {
        return suspendCoroutine { cont ->
            val request = XMLHttpRequest()
            request.onload = { cont.resume(request.responseText) }
            request.onerror = { cont.resumeWithException(GraphQlException("Request failed for: $query")) }
            request.ontimeout = { cont.resumeWithException(GraphQlException("Request timed out for: $query")) }
            request.open("POST", "http://127.0.0.1:8080/api")
            request.setRequestHeader("Content-Type", "application/json")
            request.setRequestHeader("Accept", "application/json")
            request.send(JSON.stringify(json("query" to query)))
        }
    }
}
package bitspittle.nosweat.backend.server

import bitspittle.nosweat.model.*
import com.apurebase.kgraphql.GraphQL
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced by Ktor
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        header(HttpHeaders.ContentType) // Needed to allow JSON content
        anyHost()
    }
    install(ContentNegotiation) {
        json()
    }
    install(GraphQL) {
        playground = true
        endpoint = "/api"
        schema {
            configure {
                useDefaultPrettyPrinter = true
            }

            enum<Day>()
            enum<Month>()

            type<Date>()
            type<Email>()
            type<Exercise>()
            type<Login>()
            type<User>()
            type<Routine>()
            type<Weight>()
            type<Workout>()

            query("login") {
                resolver { username: String, password: String ->
                    User("asdfasdf", username)
                }
            }
            query("user") {
                resolver { ->
                    User("fakeid", "fakename")
                }
            }
        }
    }
}
package bitspittle.nosweat.backend.server

import bitspittle.nosweat.backend.server.api.SchemaContext
import bitspittle.nosweat.backend.server.api.registerApi
import bitspittle.nosweat.backend.server.redis.toKedisPool
import com.apurebase.kgraphql.GraphQL
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced by Ktor
fun Application.module(testing: Boolean = false) {
    val kedisPool = JedisPool(JedisPoolConfig(), "localhost").toKedisPool()
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

            registerApi(SchemaContext(log, kedisPool))
        }
    }
}
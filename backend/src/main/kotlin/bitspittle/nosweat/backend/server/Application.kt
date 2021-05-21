package bitspittle.nosweat.backend.server

import bitspittle.nosweat.backend.server.account.Password
import bitspittle.nosweat.backend.server.redis.Kedis
import bitspittle.nosweat.backend.server.redis.toKedisPool
import bitspittle.nosweat.model.*
import bitspittle.nosweat.model.Date
import bitspittle.nosweat.model.graphql.mutations.CreateAccountResponse
import bitspittle.nosweat.model.graphql.mutations.CreateAccountError
import bitspittle.nosweat.model.graphql.mutations.CreateAccountSuccess
import bitspittle.nosweat.model.graphql.queries.LoginResponse
import bitspittle.nosweat.model.graphql.queries.LoginError
import bitspittle.nosweat.model.graphql.queries.LoginSuccess
import com.apurebase.kgraphql.GraphQL
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration
import java.util.*


fun main(args: Array<String>): Unit = EngineMain.main(args)

private fun Kedis.updateSecret(username: String): String {
    val secret = UUID.randomUUID().toString()
    val secretKey = "secrets:$username"
    map.set(secretKey, secret)
    expiration.expire(secretKey, Duration.ofHours(1))

    return secret
}

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

            enum<Day>()
            enum<Month>()

            type<Date>()
            type<Email>()
            type<Exercise>()
            type<Login>()
            type<User>()
            type<Routine>()
            type<Workout>()

            unionType<LoginResponse>()
            unionType<CreateAccountResponse>()
            unionType<Weight>()

            query("login") {
                resolver { username: String, password: String ->
                    log.info("Received request to log in: $username")

                    kedisPool.useResource { kedis ->
                        val userIdKey = kedis.map.get("users:name:$username")
                        if (userIdKey != null) {
                            val (passSalt, passHash) = kedis.hash.get(userIdKey, "pass.salt", "pass.hash")

                            @Suppress("NAME_SHADOWING")
                            val password = Password(password, Password.decode(passSalt))
                            if (password.hash.contentEquals(Password.decode(passHash))) {
                                log.info("Login successful for: $username [key: \"$userIdKey\"]")
                                val secret = kedis.updateSecret(username)
                                return@useResource LoginSuccess(User(username, secret))
                            }
                            else {
                                log.info("Login failed for: $username [key: \"$userIdKey\"]. Reason: bad password")
                            }
                        }
                        else {
                            log.info("Login failed for: $username. Reason: username not found")
                        }

                        LoginError("Username not found or password incorrect")
                    }
                }
            }
            mutation("createAccount") {
                resolver { username: String, password: String ->
                    log.info("Received request to create account: $username")

                    kedisPool.useResource { kedis ->
                        val usernameKey = "users:name:$username"
                        var userIdKey = kedis.map.get(usernameKey)
                        if (userIdKey != null) {
                            log.info("Account creation failed for: $username. Reason: username already exists")
                            return@useResource CreateAccountError("Username is already taken")
                        }

                        val count = kedis.numMap.increment("num.users")
                        userIdKey = "user:$count"

                        @Suppress("NAME_SHADOWING")
                        val password = Password(password)
                        kedis.hash.set(
                            userIdKey, mapOf(
                                "username" to username,
                                "pass.salt" to Password.encode(password.salt),
                                "pass.hash" to Password.encode(password.hash),
                            )
                        )
                        kedis.map.set(usernameKey, userIdKey)
                        val secret = kedis.updateSecret(username)
                        log.info("Created account for user: $username [key: \"$userIdKey\"]")

                        CreateAccountSuccess(User(username, secret))
                    }
                }
            }
        }
    }
}
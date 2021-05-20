package bitspittle.nosweat.backend.server

import bitspittle.nosweat.backend.server.account.Password
import bitspittle.nosweat.backend.server.redis.toKedisPool
import bitspittle.nosweat.model.*
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


fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced by Ktor
fun Application.module(testing: Boolean = false) {
    val kedisPool = JedisPool(JedisPoolConfig(), "localhost").toKedisPool()
    kedisPool.useResource { kedis ->
        val pass = Password("asdf")
        kedis.numMap.set("num.users", 1)
        kedis.hash.set(
            "user:1", mapOf(
                "username" to "dherman",
                "pass.salt" to Password.encode(pass.salt),
                "pass.hash" to Password.encode(pass.hash),
            )
        )
        kedis.map.set("users:name:dherman", "user:1")
    }

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
                            val idPart = userIdKey.split(":")[1]
                            val (passSalt, passHash) = kedis.hash.get(userIdKey, "pass.salt", "pass.hash")

                            val password = Password(password, Password.decode(passSalt))
                            if (password.hash.contentEquals(Password.decode(passHash))) {
                                log.info("Login successful for: $username")
                                return@useResource LoginSuccess(User(idPart, username))
                            }
                            else {
                                log.info("Login failed for: $username. Reason: bad password")
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
                        var userIdKey = kedis.map.get("users:name:$username")
                        if (userIdKey != null) {
                            log.info("Account creation failed for: $username. Reason: username already exists")
                            return@useResource CreateAccountError("Username is already taken")
                        }

                        val count = kedis.numMap.increment("num.users")
                        userIdKey = "user:$count"

                        val password = Password(password)
                        kedis.hash.set(
                            userIdKey, mapOf(
                                "username" to username,
                                "pass.salt" to Password.encode(password.salt),
                                "pass.hash" to Password.encode(password.hash),
                            )
                        )
                        kedis.map.set("users:name:$username", userIdKey)
                        log.info("Created account for user: $username [key: \"$userIdKey\"]")

                        CreateAccountSuccess(User(count.toString(), username))
                    }
                }
            }
        }
    }
}
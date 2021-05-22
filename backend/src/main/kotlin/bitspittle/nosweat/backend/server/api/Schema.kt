package bitspittle.nosweat.backend.server.api

import bitspittle.nosweat.backend.server.account.Password
import bitspittle.nosweat.backend.server.redis.Kedis
import bitspittle.nosweat.backend.server.redis.KedisPool
import bitspittle.nosweat.model.*
import bitspittle.nosweat.model.Date
import bitspittle.nosweat.model.graphql.mutations.CreateAccountError
import bitspittle.nosweat.model.graphql.mutations.CreateAccountResponse
import bitspittle.nosweat.model.graphql.mutations.CreateAccountSuccess
import bitspittle.nosweat.model.graphql.queries.LoginError
import bitspittle.nosweat.model.graphql.queries.LoginResponse
import bitspittle.nosweat.model.graphql.queries.LoginSuccess
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.slf4j.Logger
import java.time.Duration
import java.util.*

private fun Kedis.updateSecret(username: String): String {
    val secret = UUID.randomUUID().toString()
    val secretKey = "secrets:$username"
    map.set(secretKey, secret)
    expiration.expire(secretKey, Duration.ofHours(1))

    return secret
}

class SchemaContext(
    val log: Logger,
    val kedisPool: KedisPool,
)

fun SchemaBuilder.registerApi(ctx: SchemaContext) {
    registerTypes()
    registerQueries(ctx)
    registerMutations(ctx)
}

private fun SchemaBuilder.registerTypes() {
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
}

private fun SchemaBuilder.registerQueries(ctx: SchemaContext) {
    query("login") {
        resolver { username: String, password: String ->
            ctx.log.info("Received request to log in: $username")

            ctx.kedisPool.useResource { kedis ->
                val userIdKey = kedis.map.get("users:name:$username")
                if (userIdKey != null) {
                    val (passSalt, passHash) = kedis.hash.get(userIdKey, "pass.salt", "pass.hash")

                    @Suppress("NAME_SHADOWING")
                    val password = Password(password, Password.decode(passSalt))
                    if (password.hash.contentEquals(Password.decode(passHash))) {
                        ctx.log.info("Login successful for: $username [key: \"$userIdKey\"]")
                        val secret = kedis.updateSecret(username)
                        return@useResource LoginSuccess(User(username, secret))
                    }
                    else {
                        ctx.log.info("Login failed for: $username [key: \"$userIdKey\"]. Reason: bad password")
                    }
                }
                else {
                    ctx.log.info("Login failed for: $username. Reason: username not found")
                }

                LoginError("Username not found or password incorrect")
            }
        }
    }
}

private fun SchemaBuilder.registerMutations(ctx: SchemaContext) {
    mutation("createAccount") {
        resolver { username: String, password: String ->
            ctx.log.info("Received request to create account: $username")

            ctx.kedisPool.useResource { kedis ->
                val usernameKey = "users:name:$username"
                var userIdKey = kedis.map.get(usernameKey)
                if (userIdKey != null) {
                    ctx.log.info("Account creation failed for: $username. Reason: username already exists")
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
                ctx.log.info("Created account for user: $username [key: \"$userIdKey\"]")

                CreateAccountSuccess(User(username, secret))
            }
        }
    }
}

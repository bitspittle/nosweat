@file:Suppress("NAME_SHADOWING")

package bitspittle.nosweat.backend.server.api

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

inline class UserId(val value: Long) { override fun toString() = value.toString() }
inline class Username(val value: String) { override fun toString() = value }
inline class Secret(val value: String) { override fun toString() = value }

object Key {
    fun secretFrom(id: UserId) = "secrets:id:$id"
    fun idFrom(secret: Secret) = "ids:secret:$secret"
    fun idFrom(username: Username) = "ids:username:$username"
    fun usernameFrom(id: UserId) = "usernames:id:$id"
    fun userFrom(id: UserId) = "users:id:$id"
}

private fun Kedis.updateSecret(id: UserId): Secret {
    map.remove(Key.secretFrom(id))?.let { obsoleteSecret ->
        common.delete(Key.idFrom(Secret(obsoleteSecret)))
    }

    val secret = Secret(UUID.randomUUID().toString())
    val expiresIn = Duration.ofHours(1)
    map.set(Key.secretFrom(id), secret.value, expiresIn)
    numMap.set(Key.idFrom(secret), id.value, expiresIn)

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
                val username = Username(username)
                val id = kedis.numMap.get(Key.idFrom(username))?.let { UserId(it) }
                if (id != null) {
                    val (passSalt, passHash) = kedis.hash.get(Key.userFrom(id), "pass.salt", "pass.hash")

                    val password = Password(password, Password.decode(passSalt))
                    if (password.hash.contentEquals(Password.decode(passHash))) {
                        ctx.log.info("Login successful for: $username [id: \"$id\"]")
                        val secret = kedis.updateSecret(id)
                        return@useResource LoginSuccess(User(username.value), secret.value)
                    } else {
                        ctx.log.info("Login failed for: $username [id: \"$id\"]. Reason: bad password")
                    }
                } else {
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
                val username = Username(username)

                run {
                    val id = kedis.numMap.get(Key.idFrom(username))
                    if (id != null) {
                        ctx.log.info("Account creation failed for: $username. Reason: username already exists [id: $id]")
                        return@useResource CreateAccountError("Username is already taken")
                    }
                }

                val count = kedis.numMap.increment("num.users")
                val id = UserId(count)

                val password = Password(password)
                kedis.hash.set(
                    Key.userFrom(id), mapOf(
                        "username" to username.value,
                        "pass.salt" to Password.encode(password.salt),
                        "pass.hash" to Password.encode(password.hash),
                    )
                )
                kedis.map.set(Key.usernameFrom(id), username.value)
                kedis.numMap.set(Key.idFrom(username), id.value)

                val secret = kedis.updateSecret(id)
                ctx.log.info("Created account for user: $username [id: \"$id\"]")

                CreateAccountSuccess(User(username.value), secret.value)
            }
        }
    }
}

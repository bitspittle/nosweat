@file:Suppress("NAME_SHADOWING")

package bitspittle.nosweat.backend.server.api

import bitspittle.nosweat.backend.server.redis.Kedis
import bitspittle.nosweat.backend.server.redis.KedisPool
import bitspittle.nosweat.model.*
import bitspittle.nosweat.model.Date
import bitspittle.nosweat.model.graphql.mutations.*
import bitspittle.nosweat.model.graphql.queries.*
import bitspittle.nosweat.model.json.decode
import bitspittle.nosweat.model.json.toSimpleMap
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.time.Duration
import java.util.*

inline class ExerciseId(val value: String) { override fun toString() = value }
inline class UserId(val value: String) { override fun toString() = value }
inline class Username(val value: String) { override fun toString() = value }
inline class Secret(val value: String = UUID.randomUUID().toString()) { override fun toString() = value }

object Key {
    fun userIdFrom(secret: Secret) = "user.ids/secret:$secret"
    fun userIdFrom(username: Username) = "user.ids/username:$username"
    fun usernameFrom(id: UserId) = "usernames/user.id:$id"
    fun credentialsFrom(id: UserId) = "credentials/user.id:$id"
    fun exerciseFrom(id: ExerciseId) = "exercises/exercise.id:$id"
    fun exerciseIdsFrom(id: UserId) = "exercise.ids/user.id:$id"
}

private val SECRET_DURATION = Duration.ofHours(1)

private fun Kedis.addSecret(id: UserId): Secret {
    val secret = Secret(UUID.randomUUID().toString())
    map.set(Key.userIdFrom(secret), id.value, SECRET_DURATION)

    return secret
}
private fun Kedis.extend(secret: Secret): Boolean = common.expire(Key.userIdFrom(secret), SECRET_DURATION)

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

    unionType<CreateAccountResponse>()
    unionType<CreateExerciseResponse>()
    unionType<ExerciseResponse>()
    unionType<ExercisesResponse>()
    unionType<LoginResponse>()
    unionType<Weight>()
}

private fun SchemaBuilder.registerQueries(ctx: SchemaContext) {
    query("login") {
        resolver { username: String, password: String ->
            ctx.log.info("Received request to log in: $username")

            ctx.kedisPool.useResource { kedis ->
                val username = Username(username)
                val userId = kedis.map[Key.userIdFrom(username)]?.let { UserId(it) }
                if (userId != null) {
                    val (passSalt, passHash) = kedis.hash[Key.credentialsFrom(userId), "pass.salt", "pass.hash"]

                    val password = Password(password, Password.decode(passSalt))
                    if (password.hash.contentEquals(Password.decode(passHash))) {
                        val secret = kedis.addSecret(userId)
                        ctx.log.info("Login successful for: $username [id: $userId, secret: $secret]")
                        return@useResource LoginSuccess(User(userId.value, username.value), secret.value)
                    } else {
                        ctx.log.info("Login failed for: $username [id: $userId]. Reason: bad password")
                    }
                } else {
                    ctx.log.info("Login failed for: $username. Reason: username not found")
                }

                LoginError("Username not found or password incorrect")
            }
        }
    }

    query("exercises") {
        resolver { userId: String ->
            ctx.log.info("Received request for all exercise for user id: $userId")

            ctx.kedisPool.useResource { kedis ->
                val userId = UserId(userId)
                ExercisesSuccess(kedis.set[Key.exerciseIdsFrom(userId)])
            }
        }
    }

    query("exercise") {
        resolver { exerciseId: String ->
            ctx.log.info("Received request for exercise: $exerciseId")

            ctx.kedisPool.useResource { kedis ->
                val exerciseId = ExerciseId(exerciseId)

                kedis.hash.getAll(Key.exerciseFrom(exerciseId)).takeIf { it.isNotEmpty() }?.let { map ->
                    ctx.log.info("Exercise found: $exerciseId")
                    ExerciseSuccess(map.decode(Exercise.serializer()))
                } ?: run {
                    ctx.log.info("Exercise not found: $exerciseId")
                    ExerciseError("Exercise could not be found")
                }
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
                    val id = kedis.map[Key.userIdFrom(username)]
                    if (id != null) {
                        ctx.log.info("Account creation failed for: $username. Reason: username already exists [id: $id]")
                        return@useResource CreateAccountError("Username is already taken")
                    }
                }

                val userId = UserId(UUID.randomUUID().toString())

                val password = Password(password)
                kedis.hash[Key.credentialsFrom(userId)] = mapOf(
                    "pass.salt" to Password.encode(password.salt),
                    "pass.hash" to Password.encode(password.hash),
                )
                kedis.map[Key.usernameFrom(userId)] = username.value
                kedis.map[Key.userIdFrom(username)] = userId.value

                val secret = kedis.addSecret(userId)
                ctx.log.info("Created account for user: $username [id: $userId, secret: $secret]")

                CreateAccountSuccess(User(userId.value, username.value), secret.value)
            }
        }
    }

    mutation("createExercise") {
        resolver { secret: String, name: String, desc: String ->
            ctx.log.info("Received request to create exercise: $name [secret: $secret]")

            ctx.kedisPool.useResource { kedis ->
                val secret = Secret(secret)
                if (!kedis.extend(secret)) {
                    ctx.log.info("Secret not found (expired?): $secret")
                    return@useResource CreateExerciseError("User credentials invalid (log out and back in?)")
                }

                val userId = kedis.map[Key.userIdFrom(secret)]?.let { UserId(it) }
                if (userId == null) {
                    ctx.log.info("User not found for secret: $secret")
                    return@useResource CreateExerciseError("User credentials invalid (log out and back in?)")
                }

                val username = Username(kedis.map[Key.usernameFrom(userId)]!!)
                ctx.log.info("secret corresponds to username: $username")

                val exerciseId = ExerciseId(UUID.randomUUID().toString())
                val desc = desc.takeIf { it.isNotEmpty() }
                val exercise = Exercise(exerciseId.value, name, desc, username.value)


                kedis.hash[Key.exerciseFrom(exerciseId)] =
                    Json.encodeToJsonElement(Exercise.serializer(), exercise).toSimpleMap()
                kedis.set.add(Key.exerciseIdsFrom(userId), exerciseId.value)

                ctx.log.info("Exercise created: $name [id: $exerciseId, username: $username]")
                CreateExerciseSuccess(exercise)
            }
        }
    }
}

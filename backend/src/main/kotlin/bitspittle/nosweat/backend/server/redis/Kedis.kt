package bitspittle.nosweat.backend.server.redis

import redis.clients.jedis.Jedis
import java.time.Duration

/** Wrap [Jedis] and provide a more organized, Kotlin-idiomatic API */
class Kedis(private val jedis: Jedis) {
    /** Methods that work across multiple data structure types */
    inner class CommonMethods {
        fun delete(key: String): Boolean = jedis.del(key) == 1L
        fun expire(key: String, duration: Duration): Boolean {
            require(duration.toMillis() >= 1)
            return (jedis.pexpire(key, duration.toMillis()) == 1L)
        }
        fun persist(key: String): Boolean = jedis.persist(key) == 1L
        fun timeToLive(key: String) = Duration.ofMillis(jedis.pttl(key))
    }
    abstract inner class MapMethodsBase<T> {
        fun exists(key: String): Boolean = jedis.exists(key) && tryParse(jedis.get(key)) != null
        fun set(key: String, value: T, expiresIn: Duration? = null) {
            jedis.set(key, value.toString())
            expiresIn?.let { common.expire(key, it) }
        }
        fun get(key: String): T? = if (jedis.exists(key)) tryParse(jedis.get(key)) else null
        fun remove(key: String): T? = get(key)?.also { common.delete(key) }

        protected abstract fun tryParse(value: String): T?
    }

    inner class MapMethods: MapMethodsBase<String>() {
        override fun tryParse(value: String) = value // Always works
    }
    inner class NumMapMethods: MapMethodsBase<Long>() {
        fun increment(key: String): Long = jedis.incr(key)
        fun decrement(key: String): Long = jedis.decr(key)

        override fun tryParse(value: String) = value.toLongOrNull()
    }
    inner class HashMethods {
        fun exists(key: String, field: String) = jedis.hexists(key, field)
        fun set(key: String, values: Map<String, String>, expiresIn: Duration? = null) {
            jedis.hmset(key, values)
            expiresIn?.let { common.expire(key, it) }
        }
        fun get(key: String, vararg fields: String): List<String> = jedis.hmget(key, *fields)
        fun remove(key: String): List<String> = get(key).also { common.delete(key) }
    }
    inner class SetMethods {
        fun add(key: String, vararg members: String) { jedis.sadd(key, *members) }
        fun contains(key: String, value: String) = jedis.sismember(key, value)
        fun members(key: String): Set<String> = jedis.smembers(key)
        fun size(key: String): Long = jedis.scard(key)
        fun remove(key: String): Set<String> = members(key).also { common.delete(key) }
        fun intersection(vararg keys: String): Set<String> = jedis.sinter(*keys)
        fun difference(vararg keys: String): Set<String> = jedis.sdiff(*keys)
    }

    val common = CommonMethods()
    val map = MapMethods()
    val numMap = NumMapMethods()
    val hash = HashMethods()
    val set = SetMethods()
}
fun Jedis.toKedis() = Kedis(this)


package bitspittle.nosweat.backend.server.redis

import redis.clients.jedis.Jedis
import java.time.Duration

/** Wrap [Jedis] and provide a more organized, Kotlin-idiomatic API */
class Kedis(private val jedis: Jedis) {
    inner class ExpirationMethods {
        fun expire(key: String, duration: Duration): Boolean = jedis.pexpire(key, duration.toMillis()) == 1L
        fun timeToLive(key: String) = Duration.ofMillis(jedis.pttl(key))
    }
    inner class MapMethods {
        fun set(key: String, value: String) { jedis.set(key, value) }
        fun get(key: String): String? = jedis.get(key)
        fun expire(key: String, secs: Long): Boolean = jedis.expire(key, secs) == 1L
        fun expireMs(key: String, secs: Long): Boolean = jedis.expire(key, secs) == 1L
    }
    inner class NumMapMethods {
        fun set(key: String, value: Long) { jedis.set(key, value.toString()) }
        fun get(key: String): Long? = jedis.get(key).toLongOrNull()
        fun increment(key: String): Long = jedis.incr(key)
        fun decrement(key: String): Long = jedis.decr(key)
    }
    inner class HashMethods {
        fun set(key: String, values: Map<String, String>) { jedis.hmset(key, values) }
        fun get(key: String, vararg fields: String): List<String> = jedis.hmget(key, *fields)
    }
    inner class SetMethods {
        fun add(key: String, vararg members: String) { jedis.sadd(key, *members) }
        fun contains(key: String, value: String) = jedis.sismember(key, value)
        fun members(key: String): Set<String> = jedis.smembers(key)
        fun size(key: String): Long = jedis.scard(key)
        fun intersection(vararg keys: String): Set<String> = jedis.sinter(*keys)
        fun difference(vararg keys: String): Set<String> = jedis.sdiff(*keys)
    }

    val expiration = ExpirationMethods()
    val map = MapMethods()
    val numMap = NumMapMethods()
    val hash = HashMethods()
    val set = SetMethods()
}
fun Jedis.toKedis() = Kedis(this)



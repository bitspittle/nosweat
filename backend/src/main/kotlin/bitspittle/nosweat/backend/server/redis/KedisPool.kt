package bitspittle.nosweat.backend.server.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

/** Wrap [JedisPool] and provide a more organized, Kotlin-idiomatic API */
class KedisPool(private val jedisPool: JedisPool) {
    fun <T> useResource(block: (Kedis) -> T): T {
        return jedisPool.resource.use { jedis ->
            block(jedis.toKedis())
        }
    }
}
fun JedisPool.toKedisPool() = KedisPool(this)



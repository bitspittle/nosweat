package bitspittle.nosweat.backend.server.api

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// From https://www.baeldung.com/java-password-hashing#2-implementing-pbkdf2-in-java
private const val ITERATION_COUNT = 65536
private const val KEY_LEN = 128
private const val HASH_ALGO = "PBKDF2WithHmacSHA1"

class Password(val value: String, saltOverride: ByteArray? = null) {
    companion object {
        fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
        fun decode(base64: String): ByteArray = Base64.getDecoder().decode(base64)
    }

    val salt = saltOverride ?: ByteArray(16).apply {
        val random = SecureRandom()
        random.nextBytes(this)
    }

    val hash: ByteArray = PBEKeySpec(value.toCharArray(), salt, ITERATION_COUNT, KEY_LEN).let { spec ->
        val factory = SecretKeyFactory.getInstance(HASH_ALGO)
        factory.generateSecret(spec).encoded
    }

    override fun toString() = "${encode(salt)}:${encode(hash)}"
}

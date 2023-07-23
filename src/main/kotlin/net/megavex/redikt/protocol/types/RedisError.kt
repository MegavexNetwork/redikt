package net.megavex.redikt.protocol.types

import net.megavex.redikt.exception.RedisErrorException

@JvmInline
public value class RedisError(public val message: String) : RedisType<Nothing> {
    override fun unwrap(): Nothing {
        throw RedisErrorException(this)
    }
}
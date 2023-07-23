package net.megavex.redikt.protocol.types

@JvmInline
public value class RedisInteger(public val value: Long) : RedisType<Long> {
    override fun unwrap(): Long {
        return value
    }
}

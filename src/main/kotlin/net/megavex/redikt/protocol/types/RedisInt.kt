package net.megavex.redikt.protocol.types

@JvmInline
public value class RedisInt(public val value: Long) : RedisType<Long> {
    override fun value(): Long {
        return value
    }
}

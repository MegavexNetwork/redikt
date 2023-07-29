package net.megavex.redikt.protocol.types

@JvmInline
public value class RedisSimpleString(public val value: String) : RedisType<String> {
    override fun value(): String {
        return value
    }
}

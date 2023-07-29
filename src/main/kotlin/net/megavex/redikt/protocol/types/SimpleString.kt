package net.megavex.redikt.protocol.types

@JvmInline
public value class SimpleString(public val value: String) : RedisType<String> {
    override fun value(): String {
        return value
    }
}

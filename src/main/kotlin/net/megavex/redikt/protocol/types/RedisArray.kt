package net.megavex.redikt.protocol.types

@JvmInline
public value class RedisArray<T, E : RedisType<T>> internal constructor(
    public val elements: List<E>
) : RedisType<List<T>> {
    override fun unwrap(): List<T> {
        return elements.map { it.unwrap() }
    }
}

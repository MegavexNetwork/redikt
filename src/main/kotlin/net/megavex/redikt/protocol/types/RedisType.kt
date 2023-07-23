package net.megavex.redikt.protocol.types

/**
 * Represents types of the Redis serialization protocol (RESP).
 *
 * @link [RESP protocol spec](https://redis.io/docs/reference/protocol-spec/)
 */
public sealed interface RedisType<T> {
    public fun unwrap(): T
}

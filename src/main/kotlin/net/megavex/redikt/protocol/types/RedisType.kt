package net.megavex.redikt.protocol.types

import net.megavex.redikt.exception.RedisErrorException

/**
 * Represents types of the Redis serialization protocol (RESP).
 *
 * @link [RESP protocol spec](https://redis.io/docs/reference/protocol-spec/)
 */
public sealed interface RedisType<T> {
    /**
     * Gets the inner value of this type.
     *
     * @throws RedisErrorException if this type is a [RedisError]
     */
    public fun value(): T
}

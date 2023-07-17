package net.megavex.redikt.protocol

/**
 * Represents types of the Redis serialization protocol (RESP).
 *
 * @link [RESP protocol spec](https://redis.io/docs/reference/protocol-spec/)
 */
public sealed interface RedisType {
    @JvmInline
    public value class SimpleString internal constructor(public val value: String) : RedisType

    @JvmInline
    public value class Error internal constructor(public val message: String) : RedisType

    @JvmInline
    public value class Integer internal constructor(public val value: Int) : RedisType

    public data object NullBulkString : RedisType

    @JvmInline
    public value class BulkString internal constructor(internal val value: ByteArray) : RedisType {
        public fun asBytes(): ByteArray {
            return value.copyOf()
        }

        public fun asString(): String {
            return value.decodeToString()
        }

        public operator fun get(index: Int): Byte {
            return value.get(index)
        }

        override fun toString(): String {
            return "BulkString(value=${asString()})"
        }
    }

    @JvmInline
    public value class Array<T : RedisType> internal constructor(public val elements: List<T>) : RedisType
}

public fun ByteArray.toBulkString(): RedisType.BulkString {
    return RedisType.BulkString(this.copyOf())
}

public fun String.toBulkString(): RedisType.BulkString {
    return RedisType.BulkString(encodeToByteArray())
}

package net.megavex.redikt.protocol

/**
 * Represents types of the Redis serialization protocol (RESP).
 *
 * @link [RESP protocol spec](https://redis.io/docs/reference/protocol-spec/)
 */
public sealed interface RedisType<T> {
    public fun unwrap(): T

    @JvmInline
    public value class SimpleString internal constructor(public val value: String) : RedisType<String> {
        override fun unwrap(): String {
            return value
        }
    }

    @JvmInline
    public value class Error internal constructor(public val message: String) : RedisType<String> {
        override fun unwrap(): String {
            return message
        }
    }

    @JvmInline
    public value class Integer internal constructor(public val value: Long) : RedisType<Long> {
        override fun unwrap(): Long {
            return value
        }
    }

    public data object NullBulkString : RedisType<String?> {
        override fun unwrap(): String? {
            return null
        }
    }

    @JvmInline
    public value class BulkString internal constructor(internal val value: ByteArray) : RedisType<String> {
        public fun asBytes(): ByteArray {
            return value.copyOf()
        }

        public fun asString(): String {
            return value.decodeToString()
        }

        public operator fun get(index: Int): Byte {
            return value[index]
        }

        override fun unwrap(): String {
            return asString()
        }

        override fun toString(): String {
            return "BulkString(value=${asString()})"
        }
    }

    @JvmInline
    public value class Array<T, E : RedisType<T>> internal constructor(
        public val elements: List<E>
    ) : RedisType<List<T>> {
        override fun unwrap(): List<T> {
            return elements.map { it.unwrap() }
        }
    }
}

public fun ByteArray.toBulkString(): RedisType.BulkString {
    return RedisType.BulkString(this.copyOf())
}

public fun String.toBulkString(): RedisType.BulkString {
    return RedisType.BulkString(encodeToByteArray())
}

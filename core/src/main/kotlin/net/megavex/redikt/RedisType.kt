package net.megavex.redikt

public sealed interface RedisType {
    public data class SimpleString internal constructor(public val value: String) : RedisType

    public data class Error internal constructor(public val message: String) : RedisType

    public data class Integer internal constructor(public val value: Int) : RedisType

    public class BulkString internal constructor(public val value: ByteArray?) : RedisType {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BulkString

            if (value != null) {
                if (other.value == null) return false
                if (!value.contentEquals(other.value)) return false
            } else if (other.value != null) return false

            return true
        }

        override fun toString(): String {
            return "BulkString(value=${value?.decodeToString()})"
        }

        override fun hashCode(): Int {
            return value?.contentHashCode() ?: 0
        }
    }

    public data class Array<T : RedisType> internal constructor(public val elements: List<T>) : RedisType
}

public fun ByteArray.toBulkString(): RedisType.BulkString {
    return RedisType.BulkString(this)
}

public fun String.toBulkString(): RedisType.BulkString {
    return encodeToByteArray().toBulkString()
}

package net.megavex.redikt.protocol.types

public sealed interface BulkString : RedisType<String?> {
    public fun orNull(): OccupiedBulkString? {
        return this as? OccupiedBulkString
    }
}

public data object NullBulkString : BulkString {
    override fun unwrap(): String? {
        return null
    }
}

public class OccupiedBulkString : BulkString {
    internal val value: ByteArray

    internal constructor(value: ByteArray, @Suppress("UNUSED_PARAMETER") dummy: Boolean) {
        this.value = value
    }

    public constructor(value: ByteArray) {
        this.value = value.copyOf()
    }

    public constructor(value: String) {
        this.value = value.encodeToByteArray()
    }

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

    override fun equals(other: Any?): Boolean {
        return this === other || (other as? OccupiedBulkString)?.value?.contentEquals(value) == true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }

    override fun toString(): String {
        return "BulkString(value=${asString()})"
    }
}

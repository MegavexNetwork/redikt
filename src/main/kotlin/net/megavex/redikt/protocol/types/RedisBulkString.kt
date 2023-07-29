package net.megavex.redikt.protocol.types

public fun RedisBulkString(value: ByteArray?): RedisBulkString {
    return if (value != null) {
        OccupiedBulkString(value)
    } else {
        NullBulkString
    }
}

public fun RedisBulkString(value: String?): RedisBulkString {
    return if (value != null) {
        OccupiedBulkString(value)
    } else {
        NullBulkString
    }
}

public sealed interface RedisBulkString : RedisType<String?> {
    public fun asBytes(): ByteArray?
}

public data object NullBulkString : RedisBulkString {
    override fun value(): String? {
        return null
    }

    override fun asBytes(): ByteArray? {
        return null
    }
}

public class OccupiedBulkString : RedisBulkString {
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

    override fun value(): String {
        return value.decodeToString()
    }

    public override fun asBytes(): ByteArray {
        return value.copyOf()
    }

    public operator fun get(index: Int): Byte {
        return value[index]
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other as? OccupiedBulkString)?.value?.contentEquals(value) == true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }

    override fun toString(): String {
        return "BulkString(value=${value()})"
    }
}

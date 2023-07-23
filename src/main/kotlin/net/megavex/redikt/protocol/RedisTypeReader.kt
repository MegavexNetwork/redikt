package net.megavex.redikt.protocol

import net.megavex.redikt.buffer.ByteReader
import net.megavex.redikt.protocol.types.NullBulkString
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import net.megavex.redikt.protocol.types.RedisError
import net.megavex.redikt.protocol.types.RedisInteger
import net.megavex.redikt.protocol.types.RedisType
import net.megavex.redikt.protocol.types.SimpleString

internal object RedisTypeReader {
    suspend fun read(reader: ByteReader): RedisType<*> {
        return when (val type = reader.readAsciiChar()) {
            ProtocolConstants.SIMPLE_STRING -> SimpleString(readSimpleString(reader))
            ProtocolConstants.ERROR -> RedisError(readSimpleString(reader))
            ProtocolConstants.INTEGER -> RedisInteger(readInteger(reader))
            ProtocolConstants.BULK_STRING -> {
                val value = readBulkString(reader)
                if (value != null) {
                    OccupiedBulkString(value, false)
                } else {
                    NullBulkString
                }
            }

            ProtocolConstants.ARRAY -> {
                fun <T> genericsMoment(list: List<T>): RedisArray<T, RedisType<T>> {
                    @Suppress("UNCHECKED_CAST")
                    return RedisArray(list as List<RedisType<T>>)
                }

                genericsMoment(readArray(reader))
            }

            else -> error("unknown redis type '$type'")
        }
    }

    private suspend fun readSimpleString(reader: ByteReader): String {
        val builder = StringBuilder(8)

        while (true) {
            val c = reader.readAsciiChar()
            if (c == '\r') {
                reader.discard(1) // \n
                break
            }

            builder.append(c)
        }

        return builder.toString()
    }

    private suspend fun readInteger(reader: ByteReader): Long {
        var value = 0L
        var multiplier = 1
        var i = 0

        while (true) {
            val c = reader.readAsciiChar()
            if (i == 0 && c == '-') {
                multiplier = -1
                continue
            }

            if (c == '\r') {
                reader.discard(1) // \n
                break
            }

            value = (value * 10) + (c - '0')
            i++
        }

        return value * multiplier
    }

    private suspend fun readBulkString(reader: ByteReader): ByteArray? {
        val length = readInteger(reader)
        if (length == -1L) {
            return null
        }

        if (length > ProtocolConstants.MAX_BULK_STRING_SIZE) {
            error("bulk string too long: $length bytes (max ${ProtocolConstants.MAX_BULK_STRING_SIZE})")
        }

        val value = reader.readBytes(length.toInt())
        reader.discard(2) // \r\n
        return value
    }

    private suspend fun readArray(reader: ByteReader): List<RedisType<*>> {
        val length = readInteger(reader)
        val elements = (0 until length).map {
            read(reader)
        }
        return elements
    }
}
package net.megavex.redikt.protocol

import net.megavex.redikt.buffer.ByteReader

internal object RedisTypeReader {
    suspend fun read(reader: ByteReader): RedisType {
        return when (val type = reader.readAsciiChar()) {
            ProtocolConstants.SIMPLE_STRING -> RedisType.SimpleString(readSimpleString(reader))
            ProtocolConstants.ERROR -> RedisType.Error(readSimpleString(reader))
            ProtocolConstants.INTEGER -> RedisType.Integer(readInteger(reader))
            ProtocolConstants.BULK_STRING -> {
                val value = readBulkString(reader)
                if (value != null) {
                    RedisType.BulkString(value)
                } else {
                    RedisType.NullBulkString
                }
            }

            ProtocolConstants.ARRAY -> RedisType.Array(readArray(reader))
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

    private suspend fun readArray(reader: ByteReader): List<RedisType> {
        val length = readInteger(reader)
        val elements = (0 until length).map {
            read(reader)
        }
        return elements
    }
}
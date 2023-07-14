package net.megavex.redikt.protocol

import net.megavex.redikt.buffer.ByteReader

internal object RedisTypeReader {
    class RedisProtocolException(override val message: String?) : Exception()

    suspend fun read(reader: ByteReader): Result<RedisType> {
        return when (val type = reader.readAsciiChar()) {
            ProtocolConstants.SIMPLE_STRING -> readSimpleString(reader).map { RedisType.SimpleString(it) }
            ProtocolConstants.ERROR -> readSimpleString(reader).map { RedisType.Error(it) }
            ProtocolConstants.INTEGER -> readInteger(reader).map { RedisType.Integer(it) }
            ProtocolConstants.BULK_STRING -> readBulkString(reader).map { RedisType.BulkString(it) }
            ProtocolConstants.ARRAY -> readArray(reader).map { RedisType.Array(it) }
            else -> return Result.failure(RedisProtocolException("unknown redis type '$type'"))
        }
    }

    private suspend fun readSimpleString(reader: ByteReader): Result<String> {
        val builder = StringBuilder(8)

        while (true) {
            val c = reader.readAsciiChar()
            if (c == '\r') {
                reader.discard(1) // \n
                break
            }

            builder.append(c)
        }

        return Result.success(builder.toString())
    }

    private suspend fun readInteger(reader: ByteReader): Result<Int> {
        var value = 0

        while (true) {
            val c = reader.readAsciiChar()
            if (c == '\r') {
                reader.discard(1) // \n
                break
            }

            value = (value * 10) + (c - '0')
        }

        return Result.success(value)
    }

    private suspend fun readBulkString(reader: ByteReader): Result<ByteArray?> {
        val length = readInteger(reader).getOrElse { return Result.failure(it) }
        if (length == -1) {
            reader.discard(2) // \r\n
            return Result.success(null)
        }

        if (length > ProtocolConstants.MAX_BULK_STRING_SIZE) {
            return Result.failure(RedisProtocolException("bulk string too long: $length bytes (max ${ProtocolConstants.MAX_BULK_STRING_SIZE})"))
        }

        val value = reader.readBytes(length)
        reader.discard(2) // \r\n
        return Result.success(value)
    }

    private suspend fun readArray(reader: ByteReader): Result<List<RedisType>> {
        val length = readInteger(reader).getOrElse { return Result.failure(it) }
        val elements = (0 until length).map {
            read(reader).getOrElse { return Result.failure(it) }
        }
        return Result.success(elements)
    }
}
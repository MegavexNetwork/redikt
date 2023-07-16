package net.megavex.redikt.protocol

import net.megavex.redikt.RedisType
import net.megavex.redikt.buffer.ByteWriter

internal object RedisTypeWriter {
    suspend fun write(writer: ByteWriter, type: RedisType.Array<RedisType.BulkString>) {
        writer.writeAsciiChar(ProtocolConstants.ARRAY)
        writeInteger(writer, type.elements.size)
        writeCrlf(writer)

        for (element in type.elements) {
            writeBulkString(writer, element.value)
        }
    }

    private suspend fun writeInteger(writer: ByteWriter, value: Int) {
        value.toString().forEach { writer.writeAsciiChar(it) }
    }

    private suspend fun writeBulkString(writer: ByteWriter, value: ByteArray?) {
        writer.writeAsciiChar(ProtocolConstants.BULK_STRING)

        if (value != null) {
            writeInteger(writer, value.size)
            writeCrlf(writer)
            writer.writeBytes(value)
        } else {
            writeInteger(writer, -1)
        }

        writeCrlf(writer)
    }

    private suspend fun writeCrlf(writer: ByteWriter) {
        writer.writeAsciiChar('\r')
        writer.writeAsciiChar('\n')
    }
}

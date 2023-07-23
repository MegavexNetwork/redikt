package net.megavex.redikt.protocol

import net.megavex.redikt.buffer.ByteWriter
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray

internal object RedisTypeWriter {
    suspend fun write(writer: ByteWriter, type: RedisArray<String?, OccupiedBulkString>) {
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

    private suspend fun writeBulkString(writer: ByteWriter, value: ByteArray) {
        writer.writeAsciiChar(ProtocolConstants.BULK_STRING)

        writeInteger(writer, value.size)
        writeCrlf(writer)
        writer.writeBytes(value)

        writeCrlf(writer)
    }

    private suspend fun writeCrlf(writer: ByteWriter) {
        writer.writeAsciiChar('\r')
        writer.writeAsciiChar('\n')
    }
}

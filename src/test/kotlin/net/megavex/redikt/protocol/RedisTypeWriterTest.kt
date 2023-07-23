package net.megavex.redikt.protocol

import kotlinx.coroutines.runBlocking
import net.megavex.redikt.buffer.ByteWriter
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RedisTypeWriterTest {
    @Test
    fun writeTest() {
        val elements = listOf(
            OccupiedBulkString("GET"),
            OccupiedBulkString("test")
        )
        val args = RedisArray(elements)
        val writer = TestByteWriter()
        runBlocking { RedisTypeWriter.write(writer, args) }
        assertEquals("*2\r\n$3\r\nGET\r\n$4\r\ntest\r\n", writer.sb.toString())
    }

    private class TestByteWriter : ByteWriter {
        val sb = StringBuilder()

        override suspend fun writeAsciiChar(char: Char) {
            sb.append(char)
        }

        override suspend fun writeBytes(bytes: ByteArray) {
            sb.append(bytes.decodeToString())
        }
    }
}
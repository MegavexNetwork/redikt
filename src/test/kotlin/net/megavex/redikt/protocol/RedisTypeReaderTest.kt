package net.megavex.redikt.protocol

import kotlinx.coroutines.runBlocking
import net.megavex.redikt.buffer.ByteReader
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.types.NullBulkString
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import net.megavex.redikt.protocol.types.RedisError
import net.megavex.redikt.protocol.types.RedisInt
import net.megavex.redikt.protocol.types.RedisSimpleString
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RedisTypeReaderTest {
    @Test
    fun simpleStringTest() {
        val encoded = "+OK\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(RedisSimpleString("OK"), type)
        assertEquals("OK", type.value())
    }

    @Test
    fun errorTest() {
        val encoded = "-Error message\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(RedisError("Error message"), type)
        assertThrows<RedisErrorException> { type.value() }
    }

    @Test
    fun integerTest() {
        val encoded = ":69420\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(RedisInt(69420), type)
        assertEquals(69420L, type.value())
    }

    @Test
    fun negativeIntegerTest() {
        val encoded = ":-50\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(RedisInt(-50), type)
        assertEquals(-50L, type.value())
    }

    @Test
    fun bulkStringTest() {
        val encoded = "$5\r\nhello\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(OccupiedBulkString("hello".encodeToByteArray(), false), type)
        assertEquals("hello", type.value())
    }

    @Test
    fun nullBulkStringTest() {
        val encoded = "$-1\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(NullBulkString, type)
        assertEquals(null, type.value())
    }

    @Test
    fun arrayTest() {
        val encoded = "*3\r\n$5\r\nhello\r\n$-1\r\n$5\r\nworld\r\n"
        val type = runBlocking {
            RedisTypeReader.read(TestByteReader(encoded))
        }
        assertEquals(RedisArray(listOf(OccupiedBulkString("hello"), NullBulkString, OccupiedBulkString("world"))), type)
        assertEquals(listOf("hello", null, "world"), type.value())
    }

    private class TestByteReader(encoded: String) : ByteReader {
        private val buffer = ByteBuffer.wrap(encoded.encodeToByteArray())

        override suspend fun readAsciiChar(): Char {
            return buffer.get().toInt().toChar()
        }

        override suspend fun readBytes(len: Int): ByteArray {
            val array = ByteArray(len)
            buffer.get(array)
            return array
        }

        override suspend fun discard(len: Int) {
            buffer.position(buffer.position() + len)
        }
    }
}
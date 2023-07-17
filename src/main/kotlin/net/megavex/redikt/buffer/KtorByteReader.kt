package net.megavex.redikt.buffer

import io.ktor.utils.io.*

internal class KtorByteReader(private val channel: ByteReadChannel) : ByteReader {
    override suspend fun readAsciiChar(): Char {
        return channel.readByte().toInt().toChar()
    }

    override suspend fun readBytes(len: Int): ByteArray {
        val array = ByteArray(len)
        channel.readFully(array)
        return array
    }

    override suspend fun discard(len: Int) {
        channel.discard(len.toLong())
    }
}
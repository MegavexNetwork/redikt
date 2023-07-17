package net.megavex.redikt.buffer

import io.ktor.utils.io.*

internal class KtorByteWriter(private val channel: ByteWriteChannel) : ByteWriter {
    override suspend fun writeAsciiChar(char: Char) {
        channel.writeByte(char.code.toByte())
    }

    override suspend fun writeBytes(bytes: ByteArray) {
        channel.writeFully(bytes)
    }
}
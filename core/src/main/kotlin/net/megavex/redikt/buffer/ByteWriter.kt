package net.megavex.redikt.buffer

internal interface ByteWriter {
    suspend fun writeAsciiChar(char: Char)

    suspend fun writeBytes(bytes: ByteArray)
}
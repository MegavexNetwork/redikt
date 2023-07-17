package net.megavex.redikt.buffer

internal interface ByteReader {
    suspend fun readAsciiChar(): Char

    suspend fun readBytes(len: Int): ByteArray

    suspend fun discard(len: Int)
}
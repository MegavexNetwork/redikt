package net.megavex.redikt.client

import io.ktor.network.sockets.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.megavex.redikt.buffer.KtorByteReader
import net.megavex.redikt.buffer.KtorByteWriter
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import net.megavex.redikt.protocol.RedisTypeReader
import net.megavex.redikt.protocol.RedisTypeWriter
import java.io.IOException

internal class RedisClientImpl internal constructor(private val socket: Socket) : RedisClient {
    private val reader = socket.openReadChannel()
    private val writer = socket.openWriteChannel()
    private val mutex = Mutex()

    override suspend fun <T> execute(command: Command<T>): T = mutex.withLock {
        try {
            RedisTypeWriter.write(KtorByteWriter(writer), command.args)
        } catch (e: IOException) {
            throw RedisConnectionException(e)
        }

        writer.flush()

        val response = try {
            RedisTypeReader.read(KtorByteReader(reader)).getOrThrow()
        } catch (e: ClosedReceiveChannelException) {
            throw RedisConnectionException(e)
        }

        command.parseResponse(response)
    }

    override suspend fun close() {
        mutex.withLock {
            socket.close()
        }
    }
}
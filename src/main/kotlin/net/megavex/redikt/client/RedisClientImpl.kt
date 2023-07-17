package net.megavex.redikt.client

import io.ktor.network.sockets.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.megavex.redikt.buffer.KtorByteReader
import net.megavex.redikt.buffer.KtorByteWriter
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import net.megavex.redikt.protocol.RedisTypeReader
import net.megavex.redikt.protocol.RedisTypeWriter
import java.io.IOException

internal class RedisClientImpl(private val socket: Socket) : RedisClient {
    private val reader = socket.openReadChannel()
    private val writer = socket.openWriteChannel()
    private val mutex = Mutex()

    private var closedException: Throwable? = null

    override val isConnected: Boolean
        get() = socket.isActive

    override suspend fun <T> exec(command: Command<T>): T = mutex.withLock {
        closedException?.let { throw RedisConnectionException(it) }

        try {
            RedisTypeWriter.write(KtorByteWriter(writer), command.arguments)
        } catch (e: IOException) {
            closeWithException(e)
            throw RedisConnectionException(e)
        }

        writer.flush()

        val response = try {
            RedisTypeReader.read(KtorByteReader(reader))
        } catch (e: ClosedReceiveChannelException) {
            closeWithException(e)
            throw RedisConnectionException(e)
        }

        command.response(response)
    }

    override suspend fun close() = mutex.withLock {
        closeWithException(IllegalStateException("client is closed"))
    }

    private fun closeWithException(exception: Throwable) {
        if (closedException != null) {
            socket.close()
            closedException = exception
        }
    }
}
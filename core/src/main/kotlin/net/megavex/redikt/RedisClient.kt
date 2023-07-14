package net.megavex.redikt

import io.ktor.network.sockets.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.megavex.redikt.buffer.KtorByteReader
import net.megavex.redikt.buffer.KtorByteWriter
import net.megavex.redikt.command.Command
import net.megavex.redikt.protocol.RedisTypeReader
import net.megavex.redikt.protocol.RedisTypeWriter

public class RedisClient internal constructor(socket: Socket) : RedisExecutor {
    private val reader = socket.openReadChannel()
    private val writer = socket.openWriteChannel()
    private val mutex = Mutex()

    override suspend fun <T> execute(command: Command<T>): T = mutex.withLock {
        RedisTypeWriter.write(KtorByteWriter(writer), command.args)
        writer.flush()

        val response = RedisTypeReader.read(KtorByteReader(reader)).getOrThrow()
        println("got resp $response")
        command.parseResponse(response)
    }
}
package net.megavex.redikt.pooled

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException

internal class ReconnectingClient(private val clientInit: suspend () -> RedisClient) : RedisExecutor {
    private var client: RedisClient? = null
    private val mutex = Mutex()
    private var isClosed = false

    override suspend fun <T> exec(command: Command<T>): T = mutex.withLock {
        require(!isClosed) { "redis pool is closed" }

        val client = client.let { client ->
            if (client == null || !client.isConnected) {
                clientInit().also { this.client = it }
            } else {
                client
            }
        }

        try {
            return client.exec(command)
        } catch (e: RedisConnectionException) {
            this.client = null
            throw e
        }
    }

    suspend fun close() = mutex.withLock {
        isClosed = true
        client?.close()
        client = null
    }
}
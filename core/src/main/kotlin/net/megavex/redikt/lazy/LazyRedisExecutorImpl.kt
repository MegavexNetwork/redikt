package net.megavex.redikt.lazy

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.megavex.redikt.client.RedisClient
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException

internal class LazyRedisExecutorImpl(private val clientInit: suspend () -> RedisClient) : LazyRedisExecutor {
    private var client: RedisClient? = null
    private val mutex = Mutex()
    private var isClosed = false

    override suspend fun <T> exec(command: Command<T>): T = mutex.withLock {
        require(!isClosed) { "lazy redis executor is closed" }

        if (client?.isConnected == false) {
            this.client = null
        }

        val client = client ?: clientInit()
        this.client = client

        try {
            return client.exec(command)
        } catch (e: RedisConnectionException) {
            this.client = null
            throw e
        }
    }

    override suspend fun close() = mutex.withLock {
        isClosed = true
        client?.close()
        client = null
    }
}
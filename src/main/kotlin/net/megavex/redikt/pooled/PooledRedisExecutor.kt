package net.megavex.redikt.pooled

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient

public fun PooledRedisExecutor(capacity: Int, clientInit: suspend () -> RedisClient): PooledRedisExecutor {
    val executors = List(capacity) { ReconnectingClient(clientInit) }
    return PooledRedisExecutorImpl(executors)
}

public interface PooledRedisExecutor : RedisExecutor {
    public suspend fun close()
}
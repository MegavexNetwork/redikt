package net.megavex.redikt.pooled

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient
import net.megavex.redikt.lazy.LazyRedisExecutor

public fun PooledRedisExecutor(capacity: Int, clientCreator: suspend () -> RedisClient): PooledRedisExecutor {
    val executors = List(capacity) { LazyRedisExecutor(clientCreator) }
    return PooledRedisExecutorImpl(executors)
}

public interface PooledRedisExecutor : RedisExecutor {
    public suspend fun close()
}
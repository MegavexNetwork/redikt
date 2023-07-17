package net.megavex.redikt.pooled

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient

/**
 * Creates a pooled Redis executor.
 *
 * @param size the number of clients that should be in the pool
 * @param clientInit a function that creates a [RedisClient] for the pool.
 *                   The function may be called concurrently at any time during
 *                   the lifecycle of the pool. You may choose to do some
 *                   initialisation logic on the client here (e.g. authentication)
 */
public fun PooledRedisExecutor(size: Int, clientInit: suspend () -> RedisClient): PooledRedisExecutor {
    val executors = List(size) { ReconnectingClient(clientInit) }
    return PooledRedisExecutorImpl(executors)
}

/**
 * A pool of Redis clients where calls to [exec] are distributed between them.
 * Automatically tries to reconnect if a connection is lost.
 */
public interface PooledRedisExecutor : RedisExecutor {
    public suspend fun close()
}
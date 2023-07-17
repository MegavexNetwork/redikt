package net.megavex.redikt.lazy

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient

public fun LazyRedisExecutor(clientInit: suspend () -> RedisClient): LazyRedisExecutor {
    return LazyRedisExecutorImpl(clientInit)
}

/**
 * A lazy Redis executor that only connects to a server when needed.
 */
public interface LazyRedisExecutor : RedisExecutor {
    /**
     * Closes this executor and the connection if connected.
     * Calls to [exec] will start throwing [IllegalStateException].
     */
    public suspend fun close()
}
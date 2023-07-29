package net.megavex.redikt.pool

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.client.RedisClient
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a pooled Redis executor with a specified [size] of clients in the pool.
 *
 * @param size the number of clients that should be in the pool
 * @param clientInit a function that creates a [RedisClient] for the pool.
 * The function may be called concurrently at any time during the lifecycle of the pool.
 * You may choose to do some initialisation logic on the client here (e.g. authentication).
 * The function should not catch any [RedisConnectionException]s thrown by the [RedisClient].
 * @throws RedisConnectionException if a client in the pool couldn't connect
 */
public suspend fun PooledRedisExecutor(size: Int, clientInit: suspend () -> RedisClient): PooledRedisExecutor {
    require(size > 0) { "invalid pool size $size" }

    val clients = ArrayList<ReconnectingClient>(size)
    try {
        repeat(size) {
            val client = ReconnectingClient(clientInit)
            clients += client
            client.init()
        }
    } catch (e: RedisConnectionException) {
        for (client in clients) {
            client.close()
        }
        throw e
    }

    return PooledRedisExecutorImpl(clients)
}

/**
 * A pool of Redis clients where calls to [exec] are distributed between them.
 * Automatically tries to reconnect if a connection is lost.
 */
public interface PooledRedisExecutor : RedisExecutor {
    /**
     * Gets a client from this pool.
     */
    public fun nextClient(): RedisExecutor

    /**
     * Closes all clients in the pool. Calls to [nextClient] will now throw [IllegalStateException].
     */
    public suspend fun close()

    override suspend fun <T> exec(command: Command<T>): T {
        return nextClient().exec(command)
    }
}

/**
 * Calls the specified function [block] with a client from the pool.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> PooledRedisExecutor.multi(block: RedisExecutor.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.nextClient())
}

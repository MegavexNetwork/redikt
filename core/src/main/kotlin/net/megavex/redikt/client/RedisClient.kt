package net.megavex.redikt.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import net.megavex.redikt.RedisEndpoint
import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.exception.RedisConnectionException
import java.net.SocketException

/**
 * Connects to a Redis server.
 *
 * @throws RedisConnectionException if couldn't connect to the server
 */
public suspend fun RedisClient(selectorManager: SelectorManager, endpoint: RedisEndpoint): RedisClient {
    val builder = aSocket(selectorManager).tcp()

    val socket = try {
        builder.connect(endpoint.address, endpoint.port)
    } catch (e: SocketException) {
        throw RedisConnectionException(e)
    }

    return RedisClientImpl(socket)
}

/**
 * A single connection to a Redis server.
 * If the connection is lost, calls to [exec] will start throwing [RedisConnectionException].
 */
public interface RedisClient : RedisExecutor {
    /**
     * Whether the connection is active.
     */
    public val isConnected: Boolean

    /**
     * Closes the connection. Does nothing if already closed or connection was lost.
     * If successful, calls to [exec] will start throwing [IllegalStateException].
     */
    public suspend fun close()
}
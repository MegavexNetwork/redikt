package net.megavex.redikt.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.RedisHost
import net.megavex.redikt.exception.RedisConnectionException
import java.net.SocketException

/**
 * A connection to a Redis server.
 * All calls to [execute] are guaranteed to be ran on the same client.
 * If the connection is lost or closed with [close], all calls to [execute] will start throwing [RedisConnectionException].
 */
public interface RedisClient : RedisExecutor {
    public companion object {
        /**
         * Connects to a Redis server.
         *
         * @throws RedisConnectionException if couldn't connect to the server
         */
        public suspend fun connect(host: RedisHost): RedisClient {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val builder = aSocket(selectorManager).tcp()

            val socket = try {
                builder.connect(host.address, host.port)
            } catch (e: SocketException) {
                throw RedisConnectionException(e)
            }

            return RedisClientImpl(selectorManager, socket)
        }
    }

    public suspend fun close()
}
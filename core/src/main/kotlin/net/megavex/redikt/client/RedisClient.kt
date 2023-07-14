package net.megavex.redikt.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.RedisHost
import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import java.net.SocketException

public interface RedisClient : RedisExecutor {
    public companion object {
        /**
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

            return RedisClientImpl(socket)
        }
    }

    override suspend fun <T> execute(command: Command<T>): T

    public suspend fun close()
}
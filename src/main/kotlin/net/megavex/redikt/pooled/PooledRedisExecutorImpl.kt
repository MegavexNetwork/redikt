package net.megavex.redikt.pooled

import net.megavex.redikt.RedisExecutor
import net.megavex.redikt.command.Command
import java.util.concurrent.atomic.AtomicInteger

internal class PooledRedisExecutorImpl(private val clients: List<ReconnectingClient>) : PooledRedisExecutor {
    private val index = AtomicInteger(0)

    override suspend fun <T> exec(command: Command<T>): T {
        return nextClient().exec(command)
    }

    override fun nextClient(): RedisExecutor {
        val i = index.getAndUpdate { (it + 1) % clients.size }
        return clients[i]
    }

    override suspend fun close() {
        for (executor in clients) {
            executor.close()
        }
    }
}
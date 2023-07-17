package net.megavex.redikt.pooled

import net.megavex.redikt.command.Command
import net.megavex.redikt.lazy.LazyRedisExecutor
import java.util.concurrent.atomic.AtomicInteger

internal class PooledRedisExecutorImpl(private val executors: List<LazyRedisExecutor>) : PooledRedisExecutor {
    private val index = AtomicInteger(0)

    override suspend fun <T> exec(command: Command<T>): T {
        val nextIndex = index.getAndUpdate { (it + 1) % executors.size }
        val client = executors[nextIndex]
        return client.exec(command)
    }

    override suspend fun close() {
        for (executor in executors) {
            executor.close()
        }
    }
}
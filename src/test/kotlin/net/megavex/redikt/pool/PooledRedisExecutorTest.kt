package net.megavex.redikt.pool

import kotlinx.coroutines.runBlocking
import net.megavex.redikt.client.RedisClient
import net.megavex.redikt.command.Command
import net.megavex.redikt.protocol.types.NullBulkString
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import net.megavex.redikt.protocol.types.RedisType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PooledRedisExecutorTest {
    @Test
    fun execTest() {
        val clients = (0..3)
            .map { FakeRedisClient() }

        val pool = PooledRedisExecutorImpl(clients.map { ReconnectingClient { it } })
        runBlocking {
            repeat(5) { pool.exec(TestCommand()) }
        }

        assertEquals(2, clients[0].execAmount)
        assertEquals(1, clients[1].execAmount)
        assertEquals(1, clients[2].execAmount)
        assertEquals(1, clients[3].execAmount)
    }

    @Test
    fun closeTest() {
        val client = FakeRedisClient()
        val pool = PooledRedisExecutorImpl(listOf(ReconnectingClient { client }))
        runBlocking {
            pool.exec(TestCommand())
            pool.close()
        }
        assertFalse(client.isConnected)
    }

    private class TestCommand : Command<Unit> {
        override val arguments: RedisArray<String?, OccupiedBulkString>
            get() = RedisArray(listOf())

        override fun response(type: RedisType<*>) {}
    }

    private class FakeRedisClient : RedisClient {
        var execAmount = 0
            private set

        override var isConnected: Boolean = true

        override suspend fun <T> exec(command: Command<T>): T {
            execAmount++
            return command.response(NullBulkString)
        }

        override suspend fun close() {
            isConnected = false
        }
    }
}
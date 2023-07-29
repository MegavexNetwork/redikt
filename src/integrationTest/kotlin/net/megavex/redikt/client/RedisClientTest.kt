package net.megavex.redikt.client

import io.ktor.network.selector.SelectorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.megavex.redikt.RedisEndpoint
import net.megavex.redikt.command.commands.ping
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RedisClientTest {
    @Test
    fun connectionTest() = runBlocking {
        val uri = System.getProperty("redis.uri")
        assumeTrue(uri != null, "redis.uri system property not set")

        val selectorManager = SelectorManager(Dispatchers.IO)
        val client = RedisClient(selectorManager, RedisEndpoint(URI(uri)))
        assertEquals("PONG", client.exec(ping()).value())
        client.close()
        selectorManager.close()
    }
}
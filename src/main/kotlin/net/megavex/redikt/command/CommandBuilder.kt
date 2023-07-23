package net.megavex.redikt.command

import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import net.megavex.redikt.protocol.types.RedisType

/**
 * DSL builder for [Command]s.
 */
public fun <T> command(block: CommandBuilder<T>.() -> Unit): Command<T> {
    val builder = CommandBuilder<T>()
    block(builder)
    return builder.build()
}

public class CommandBuilder<T> {
    private lateinit var arguments: List<OccupiedBulkString>
    private lateinit var responseImpl: (RedisType<*>) -> T

    public fun arguments(capacity: Int = 4, init: ArgumentsBuilder.() -> Unit) {
        val builder = ArgumentsBuilder(capacity)
        init(builder)
        arguments = builder.build()
    }

    public fun response(response: (RedisType<*>) -> T) {
        this.responseImpl = response
    }

    internal fun build(): Command<T> {
        return CommandImpl(RedisArray(arguments), responseImpl)
    }

    public class ArgumentsBuilder(capacity: Int) {
        private val args = ArrayList<OccupiedBulkString>(capacity)

        public fun add(value: OccupiedBulkString) {
            args += value
        }

        public fun add(value: String) {
            add(OccupiedBulkString(value.toByteArray()))
        }

        public fun add(value: ByteArray) {
            add(OccupiedBulkString(value))
        }

        internal fun build(): List<OccupiedBulkString> {
            return args
        }
    }
}

private class CommandImpl<T>(
    override val arguments: RedisArray<String?, OccupiedBulkString>,
    val responseImpl: (RedisType<*>) -> T
) : Command<T> {
    override fun response(type: RedisType<*>): T {
        return responseImpl(type)
    }
}

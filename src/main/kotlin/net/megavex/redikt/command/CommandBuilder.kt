package net.megavex.redikt.command

import net.megavex.redikt.protocol.RedisType

/**
 * DSL builder for [Command]s.
 */
public fun <T> command(block: CommandBuilder<T>.() -> Unit): Command<T> {
    val builder = CommandBuilder<T>()
    block(builder)
    return builder.build()
}

public class CommandBuilder<T> {
    private lateinit var arguments: List<RedisType.BulkString>
    private lateinit var responseImpl: (RedisType) -> T

    public fun arguments(capacity: Int = 4, init: ArgumentsBuilder.() -> Unit) {
        val builder = ArgumentsBuilder(capacity)
        init(builder)
        arguments = builder.build()
    }

    public fun response(response: (RedisType) -> T) {
        this.responseImpl = response
    }

    internal fun build(): Command<T> {
        return CommandImpl(RedisType.Array(arguments), responseImpl)
    }

    public class ArgumentsBuilder(capacity: Int) {
        private val args = ArrayList<RedisType.BulkString>(capacity)

        public fun add(value: RedisType.BulkString) {
            args += value
        }

        public fun add(value: String) {
            add(RedisType.BulkString(value.toByteArray()))
        }

        public fun add(value: ByteArray) {
            add(RedisType.BulkString(value))
        }

        internal fun build(): List<RedisType.BulkString> {
            return args
        }
    }
}

private class CommandImpl<T>(
    override val arguments: RedisType.Array<RedisType.BulkString>,
    val responseImpl: (RedisType) -> T
) : Command<T> {
    override fun response(type: RedisType): T {
        return responseImpl(type)
    }
}

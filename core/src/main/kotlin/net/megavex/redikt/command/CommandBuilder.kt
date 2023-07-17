package net.megavex.redikt.command

import net.megavex.redikt.protocol.RedisType

public fun <T> command(block: CommandBuilder<T>.() -> Unit): Command<T> {
    val builder = CommandBuilder<T>()
    block(builder)
    return builder.build()
}

public class CommandBuilder<T> {
    private lateinit var args: List<RedisType.BulkString>
    private lateinit var resp: (RedisType) -> T

    public fun args(capacity: Int = 4, init: ArgsBuilder.() -> Unit) {
        val builder = ArgsBuilder(capacity)
        init(builder)
        args = builder.build()
    }

    public fun resp(resp: (RedisType) -> T) {
        this.resp = resp
    }

    internal fun build(): Command<T> {
        return CommandImpl(RedisType.Array(args), resp)
    }

    public class ArgsBuilder(capacity: Int) {
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
    override val args: RedisType.Array<RedisType.BulkString>,
    val respImpl: (RedisType) -> T
) : Command<T> {
    override fun resp(type: RedisType): T {
        return respImpl(type)
    }
}

package net.megavex.redikt.command

import net.megavex.redikt.protocol.RedisType

public inline fun SimpleCommand(block: ArgumentWriter.() -> Unit): SimpleCommand {
    val builder = ArgumentWriter()
    block(builder)
    return SimpleCommand(builder.build())
}

public class SimpleCommand(override val args: RedisType.Array<RedisType.BulkString>) : Command<RedisType> {
    override fun parseResponse(type: RedisType): RedisType {
        return type
    }
}
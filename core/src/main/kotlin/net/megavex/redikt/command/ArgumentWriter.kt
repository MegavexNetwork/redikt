package net.megavex.redikt.command

import net.megavex.redikt.protocol.RedisType

public class ArgumentWriter {
    private val args = mutableListOf<RedisType.BulkString>()

    public fun add(value: String) {
        args += RedisType.BulkString(value.toByteArray())
    }

    public fun build(): RedisType.Array<RedisType.BulkString> {
        return RedisType.Array(args)
    }
}
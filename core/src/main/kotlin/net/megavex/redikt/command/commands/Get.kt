package net.megavex.redikt.command.commands

import net.megavex.redikt.protocol.RedisType
import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command

public fun get(key: RedisType.BulkString): Command<RedisType.BulkString> = command {
    args(2) {
        add("GET")
        add(key)
    }

    resp { type ->
        type as? RedisType.BulkString ?: error("unexpected GET response: $type")
    }
}

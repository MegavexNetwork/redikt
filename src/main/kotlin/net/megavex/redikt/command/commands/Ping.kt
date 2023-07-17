package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.RedisType

private val pingCommand = command {
    arguments(1) {
        add("PING")
    }

    response { type ->
        when (type) {
            is RedisType.SimpleString -> type
            is RedisType.Error -> throw RedisErrorException(type)
            else -> error("unexpected PING response: $type")
        }
    }
}

public fun ping(): Command<RedisType.SimpleString> {
    return pingCommand
}

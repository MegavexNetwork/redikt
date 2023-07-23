package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.types.RedisError
import net.megavex.redikt.protocol.types.SimpleString

private val pingCommand = command {
    arguments(1) {
        add("PING")
    }

    response { type ->
        when (type) {
            is SimpleString -> type
            is RedisError -> throw RedisErrorException(type)
            else -> error("unexpected PING response: $type")
        }
    }
}

/**
 * Wrapper for the `PING` command.
 *
 * @see [PING documentation](https://redis.io/commands/ping/)
 */
public fun ping(): Command<SimpleString> {
    return pingCommand
}

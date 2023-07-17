package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.RedisType

/**
 * Wrapper for the `GET` command.
 *
 * @see [GET documentation](https://redis.io/commands/get/)
 */
public fun get(key: RedisType.BulkString): Command<RedisType.BulkString> = command {
    arguments(2) {
        add("GET")
        add(key)
    }

    response { type ->
        when (type) {
            is RedisType.BulkString -> return@response type
            is RedisType.Error -> throw RedisErrorException(type)
            else -> error("unexpected GET response: $type")
        }
    }
}

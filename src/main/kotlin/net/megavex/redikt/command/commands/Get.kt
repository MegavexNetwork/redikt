package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.types.RedisBulkString
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisError

/**
 * Wrapper for the `GET` command.
 *
 * @see [GET documentation](https://redis.io/commands/get/)
 */
public fun get(key: OccupiedBulkString): Command<RedisBulkString> = command {
    arguments(2) {
        add("GET")
        add(key)
    }

    response { type ->
        when (type) {
            is RedisBulkString -> return@response type
            is RedisError -> throw RedisErrorException(type)
            else -> error("unexpected GET response: $type")
        }
    }
}

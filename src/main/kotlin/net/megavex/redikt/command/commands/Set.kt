package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.command.types.ExistenceModifier
import net.megavex.redikt.command.types.ExpiryOption
import net.megavex.redikt.command.types.apply
import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.types.BulkString
import net.megavex.redikt.protocol.types.NullBulkString
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisError
import net.megavex.redikt.protocol.types.SimpleString

/**
 * Wrapper for the `SET` command.
 *
 * @see [SET documentation](https://redis.io/commands/set/)
 */
public fun set(
    key: OccupiedBulkString,
    value: OccupiedBulkString,
    existenceMod: ExistenceModifier? = null,
    expiry: ExpiryOption? = null
): Command<Boolean> = command {
    arguments {
        add("SET")
        add(key)
        add(value)
        existenceMod?.apply(this)
        expiry?.apply(this)
    }

    response { type ->
        when (type) {
            is SimpleString -> true
            is NullBulkString -> false
            is RedisError -> throw RedisErrorException(type)
            else -> error("unexpected SET response: $type")
        }
    }
}

/**
 * Wrapper for the `SET` command with the `GET` option.
 *
 * @see [SET documentation](https://redis.io/commands/set/)
 */
public fun setAndGet(
    key: OccupiedBulkString,
    value: OccupiedBulkString,
    existenceMod: ExistenceModifier? = null,
    expiry: ExpiryOption? = null
): Command<BulkString> = command {
    arguments {
        add("SET")
        add(key)
        add(value)
        add("GET")
        existenceMod?.apply(this)
        expiry?.apply(this)
    }

    response { type ->
        when (type) {
            is BulkString -> type
            is RedisError -> throw RedisErrorException(type)
            else -> error("unexpected SET response: $type")
        }
    }
}

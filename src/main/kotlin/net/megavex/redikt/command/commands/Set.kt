package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.command.types.ExistenceModifier
import net.megavex.redikt.command.types.ExpiryOption
import net.megavex.redikt.command.types.apply
import net.megavex.redikt.protocol.RedisType

public fun set(
    key: RedisType.BulkString,
    value: RedisType.BulkString,
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
            is RedisType.SimpleString -> {
                return@response true
            }

            is RedisType.BulkString -> {
                if (type.isNull) {
                    return@response false
                }
            }

            else -> {}
        }

        error("unexpected SET response: $type")
    }
}

public fun setAndGet(
    key: RedisType.BulkString,
    value: RedisType.BulkString,
    existenceMod: ExistenceModifier? = null,
    expiry: ExpiryOption? = null
): Command<RedisType.BulkString> = command {
    arguments {
        add("SET")
        add(key)
        add(value)
        add("GET")
        existenceMod?.apply(this)
        expiry?.apply(this)
    }

    response { type ->
        type as? RedisType.BulkString ?: error("unexpected SET response: $type")
    }
}

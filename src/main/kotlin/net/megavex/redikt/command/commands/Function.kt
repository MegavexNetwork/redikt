package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.protocol.types.RedisSimpleString
import net.megavex.redikt.protocol.types.RedisType

/**
 * Wrapper for the 'FUNCTION LOAD' command.
 *
 * @see [FUNCTION LOAD documentation](https://redis.io/commands/function-load/)
 */
public fun functionLoad(functionCode: String, replace: Boolean = false): Command<String> = command {
    arguments(4) {
        add("FUNCTION")
        add("LOAD")
        if (replace) {
            add("REPLACE")
        }
        add(functionCode)
    }

    response { (it as RedisSimpleString).value() }
}

/**
 * Wrapper for the `FCALL` and `FCALL_RO` commands.
 *
 * @see [FCALL documentation](https://redis.io/commands/fcall/)
 * @see [FCALL_RO documentation](https://redis.io/commands/fcall_ro/)
 */
public fun functionCall(
    function: String,
    keys: Collection<String> = emptyList(),
    args: Collection<String> = emptyList(),
    isReadOnly: Boolean = false
): Command<RedisType<*>> = command {
    arguments(3 + keys.size + args.size) {
        add(if (isReadOnly) "FCALL_RO" else "FCALL")
        add(function)
        add(keys.size.toString())
        keys.forEach { add(it) }
        args.forEach { add(it) }
    }

    response { it }
}

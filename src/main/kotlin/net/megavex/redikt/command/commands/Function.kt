package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.protocol.RedisType

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
): Command<RedisType> = command {
    arguments {
        add(if (isReadOnly) "FCALL_RO" else "FCALL")
        add(function)
        add(keys.size.toString())
        keys.forEach { add(it) }
        args.forEach { add(it) }
    }

    response { it }
}

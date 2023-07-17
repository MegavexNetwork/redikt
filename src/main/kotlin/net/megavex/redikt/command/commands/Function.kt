package net.megavex.redikt.command.commands

import net.megavex.redikt.command.Command
import net.megavex.redikt.command.command
import net.megavex.redikt.protocol.RedisType

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

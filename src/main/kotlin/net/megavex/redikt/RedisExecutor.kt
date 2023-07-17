package net.megavex.redikt

import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import net.megavex.redikt.exception.RedisErrorException

/**
 * Something that can execute Redis commands.
 */
public interface RedisExecutor {
    /**
     * Executes a [command] with the arguments in [Command.arguments] and returns the result from [Command.response].
     *
     * @throws RedisErrorException if the server replied with an error that the [Command] did not handle
     * @throws RedisConnectionException if a connection related error occurred
     */
    public suspend fun <T> exec(command: Command<T>): T
}

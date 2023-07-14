package net.megavex.redikt

import net.megavex.redikt.command.Command
import net.megavex.redikt.exception.RedisConnectionException
import net.megavex.redikt.exception.RedisErrorException

public interface RedisExecutor {
    /**
     * @throws RedisErrorException if the server replied with an error that the [Command] did not handle
     * @throws RedisConnectionException if a connection related error occurred
     */
    public suspend fun <T> execute(command: Command<T>): T
}

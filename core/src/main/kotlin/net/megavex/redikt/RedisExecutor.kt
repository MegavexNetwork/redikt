package net.megavex.redikt

import net.megavex.redikt.command.Command

public interface RedisExecutor {
    public suspend fun <T> execute(command: Command<T>): T
}

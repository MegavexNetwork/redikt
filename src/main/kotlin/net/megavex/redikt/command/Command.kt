package net.megavex.redikt.command

import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.RedisType

/**
 * Represents a Redis command.
 */
public interface Command<T> {
    /**
     * The arguments of the command, including its name.
     */
    public val arguments: RedisType.Array<RedisType.BulkString>

    /**
     * Transforms the Redis server response of the [arguments] into type [T].
     *
     * @throws RedisErrorException if [type] is a [RedisType.Error] and couldn't be handled
     */
    public fun response(type: RedisType): T
}
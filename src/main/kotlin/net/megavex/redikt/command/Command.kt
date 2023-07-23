package net.megavex.redikt.command

import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.types.OccupiedBulkString
import net.megavex.redikt.protocol.types.RedisArray
import net.megavex.redikt.protocol.types.RedisError
import net.megavex.redikt.protocol.types.RedisType

/**
 * Represents a Redis command.
 */
public interface Command<T> {
    /**
     * The arguments of the command, including its name.
     */
    public val arguments: RedisArray<String?, OccupiedBulkString>

    /**
     * Transforms the Redis server response of the [arguments] into type [T].
     *
     * @throws RedisErrorException if [type] is a [RedisError] and couldn't be handled
     */
    public fun response(type: RedisType<*>): T
}
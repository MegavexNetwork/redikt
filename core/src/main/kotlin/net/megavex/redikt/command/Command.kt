package net.megavex.redikt.command

import net.megavex.redikt.protocol.RedisType

public interface Command<T> {
    public val args: RedisType.Array<RedisType.BulkString>

    public fun parseResponse(type: RedisType): T
}
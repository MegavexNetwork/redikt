package net.megavex.redikt.command

import net.megavex.redikt.exception.RedisErrorException
import net.megavex.redikt.protocol.RedisType

public interface Command<T> {
    public val args: RedisType.Array<RedisType.BulkString>

    /**
     * @throws RedisErrorException
     */
    public fun resp(type: RedisType): T
}
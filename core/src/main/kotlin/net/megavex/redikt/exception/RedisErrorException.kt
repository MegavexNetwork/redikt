package net.megavex.redikt.exception

import net.megavex.redikt.RedisType

public class RedisErrorException(public val value: RedisType.Error) : Exception(value.message)
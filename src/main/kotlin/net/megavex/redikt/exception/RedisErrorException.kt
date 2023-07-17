package net.megavex.redikt.exception

import net.megavex.redikt.protocol.RedisType

public data class RedisErrorException(val value: RedisType.Error) : Exception(value.message)
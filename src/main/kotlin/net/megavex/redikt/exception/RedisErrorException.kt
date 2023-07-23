package net.megavex.redikt.exception

import net.megavex.redikt.protocol.types.RedisError

public data class RedisErrorException(val value: RedisError) : Exception(value.message)
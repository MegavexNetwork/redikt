package net.megavex.redikt

public data class RedisEndpoint(val address: String, val port: Int = DEFAULT_PORT) {
    public companion object {
        public const val DEFAULT_PORT: Int = 6379
    }

    init {
        require(port in 0..65535) { "invalid port $port" }
    }
}
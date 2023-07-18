package net.megavex.redikt

import java.net.URI

public data class RedisEndpoint(val host: String, val port: Int = DEFAULT_PORT) {
    public companion object {
        public const val DEFAULT_PORT: Int = 6379
    }

    public constructor(uri: URI) : this(uri.host, if (uri.port == -1) DEFAULT_PORT else uri.port) {
        require(uri.scheme == "redis") { "invalid scheme ${uri.scheme}" }
    }

    init {
        require(port in 0..65535) { "invalid port $port" }
    }
}
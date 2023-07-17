package net.megavex.redikt.command.types

import net.megavex.redikt.command.CommandBuilder

public sealed interface ExpiryOption {
    public data class Duration(val millis: Long) : ExpiryOption

    public data class Instant(val unixTimeMillis: Long) : ExpiryOption

    public object KeepTtl : ExpiryOption
}

internal fun ExpiryOption.apply(builder: CommandBuilder.ArgsBuilder) {
    when (this) {
        is ExpiryOption.Duration -> {
            builder.add("PX")
            builder.add(millis.toString())
        }

        is ExpiryOption.Instant -> {
            builder.add("PXAT")
            builder.add(unixTimeMillis.toString())
        }

        is ExpiryOption.KeepTtl -> {
            builder.add("KEEPTTL")
        }
    }
}
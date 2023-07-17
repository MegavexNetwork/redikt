package net.megavex.redikt.command.types

import net.megavex.redikt.command.CommandBuilder

public enum class ExistenceModifier {
    NX, XX
}

internal fun ExistenceModifier.apply(builder: CommandBuilder.ArgumentsBuilder) {
    builder.add(name)
}
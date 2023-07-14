package net.megavex.redikt.protocol

internal object ProtocolConstants {
    const val SIMPLE_STRING = '+'
    const val ERROR = '-'
    const val INTEGER = ':'
    const val BULK_STRING = '$'
    const val ARRAY = '*'

    const val MAX_BULK_STRING_SIZE = 1024 * 1024 * 512 // 512 MB
}
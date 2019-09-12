package tw.cchi.mec_dl_poc.util

infix fun Byte.and(other: Int): Byte = (this.toInt() and other).toByte()

//infix fun Byte.or(other: Byte): Int = this.toInt() and other.toInt()

infix fun Byte.shl(other: Int): Byte = this.toInt().shl(other).toByte()

infix fun Byte.shr(other: Int): Int = this.toInt().shr(other)

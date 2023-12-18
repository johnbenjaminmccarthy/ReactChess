typealias SquareBitIndex = Int //Least significant digit has index 0

fun SquareBitIndex.toBitBoard(): BitBoard {
    return (0b1u.toULong() shl this)
}

fun SquareBitIndex.rank(): Int {
    return this / 8 + 1
}

fun SquareBitIndex.file(): Int {
    return 8-(this % 8)
}

fun SquareBitIndex.toCoords(): Pair<Int, Int> {
    return Pair(this.file() -1, this.rank() -1)
}

fun SquareBitIndex.fileChar(): Char {
    return ('a'.code + (this.file() - 1)).toChar()
}

fun SquareBitIndex.toAlgebraic(): String {
    return "${this.fileChar()}${this.rank()}"
}

fun SquareBitIndex.isHFile(): Boolean {
    return this.file() == 8
}

fun SquareBitIndex.isAFile(): Boolean {
    return this.file() == 1
}

fun SquareBitIndex.isFirstRank(): Boolean {
    return this.rank() == 1
}

fun SquareBitIndex.isFinalRank(): Boolean {
    return this.rank() == 8
}
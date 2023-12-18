typealias BitBoard = ULong

val index64: Array<SquareBitIndex> = arrayOf(
    0,  1, 48,  2, 57, 49, 28,  3,
    61, 58, 50, 42, 38, 29, 17,  4,
    62, 55, 59, 36, 53, 51, 43, 22,
    45, 39, 33, 30, 24, 18, 12,  5,
    63, 47, 56, 27, 60, 41, 37, 16,
    54, 35, 52, 21, 44, 32, 23, 11,
    46, 26, 40, 15, 34, 20, 31, 10,
    25, 14, 19,  9, 13,  8,  7,  6
)

const val debruijn64: ULong = 0x03f79d71b4cb0a89u


fun BitBoard.toBinaryString(delim: String = ""): String {
    return if (this == 0u.toULong()) { "0".repeat(64) } else { ("0".repeat(this.countLeadingZeroBits()) + this.toString(2)) }.chunked(8).joinToString(delim)
}

fun BitBoard.toBitArray(): Array<Array<Int>> {
    return Array(8) { i -> Array(8) { j ->
            ((this shr (i*8+j)) % 2u).toInt() //Checks if i*8+j'th bit is 1 or 0
        }
    }
}

fun BitBoard.lowestBitIndex(): SquareBitIndex {
    return index64[(((this and (0.toULong()-this)) * debruijn64) shr 58).toInt()]
}

fun BitBoard.bitScan(): List<SquareBitIndex> { // Returns a list of indices where set bits are located on a bitboard
    if (this == 0.toULong()) {
        return listOf()
    }

    var workingBoard = this
    var workingList = mutableListOf<SquareBitIndex>()
    while (workingBoard != 0.toULong()) {
        workingList.add(workingBoard.lowestBitIndex())
        workingBoard = workingBoard and (workingBoard - 0b1u)
    }
    return workingList
}

fun BitBoard.flipBit(index: SquareBitIndex): BitBoard {
    return this xor index.toBitBoard()
}

fun BitBoard.bitIsSet(index: SquareBitIndex): Boolean {
    return (this and (0b1u.toULong() shl index)) != 0u.toULong()
}

fun BitBoard.bitSize(): Int {
    return this.bitScan().size
}
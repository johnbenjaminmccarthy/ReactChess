import kotlin.math.abs

val knightDeltas = listOf(
    -17, -15, -10, -6, 6, 10, 15, 17
)

val rookDeltas = listOf(
    -1, 1, -8, 8
)

val bishopDeltas = listOf(
    -7, 7, -9, 9
)

val whitePawnAttackDeltas = listOf(
    7, 9
)

val blackPawnAttackDeltas = listOf(
    -7, -9
)

val queenDeltas = rookDeltas + bishopDeltas

val kingDeltas = rookDeltas + bishopDeltas

fun deltaToCoords(delta: Int): Pair<Int, Int> {
    return when (delta) {
        8 -> Pair(0,1)
        -8 -> Pair(0,-1)
        16 -> Pair(0,2)
        -16 -> Pair(0,-2)
        1 -> Pair(1,0)
        -1 -> Pair(-1,0)
        7 -> Pair(-1,1)
        9 -> Pair(1,1)
        -7 -> Pair(1,-1)
        -9 -> Pair(-1,-1)
        -17 -> Pair(-1,-2)
        -10 -> Pair(-2,-1)
        6 -> Pair(-2,1)
        15 -> Pair(-1,2)
        17 -> Pair(1,2)
        10 -> Pair(2,1)
        -6 -> Pair(2,-1)
        -15 -> Pair(1,-2)
        else -> Pair(0,0)
    }
}


val whitePawnAttacks: Map<SquareBitIndex, BitBoard> = generateWhitePawnAttacks()
val whitePawnAdvances: Map<SquareBitIndex, BitBoard> = generateWhitePawnAdvances()
val blackPawnAttacks: Map<SquareBitIndex, BitBoard> = generateBlackPawnAttacks()
val blackPawnAdvances: Map<SquareBitIndex, BitBoard> = generateBlackPawnAdvances()
val knightAttacks: Map<SquareBitIndex, BitBoard> = generateKnightAttacks()
val kingAttacks: Map<SquareBitIndex, BitBoard> = generateKingAttacks()

val sliderAttacks: Map<Int, BitBoard> = generateSliderAttacks()
fun bishopAttacks(square: SquareBitIndex, occupied: BitBoard): BitBoard = sliderAttacks.getOrElse(bishopMagic[square].bishopIndex(occupied)) { 0u.toULong() }
fun rookAttacks(square: SquareBitIndex, occupied: BitBoard): BitBoard = sliderAttacks.getOrElse(rookMagic[square].rookIndex(occupied)) { 0u.toULong() }
fun queenAttacks(square: SquareBitIndex, occupied: BitBoard): BitBoard = bishopAttacks(square, occupied) xor rookAttacks(square, occupied)

val between: List<List<BitBoard>> = generateBetween()
val rays: List<List<BitBoard>> = generateRays()

fun aligned(a: SquareBitIndex, b: SquareBitIndex, c: SquareBitIndex): Boolean = rays[a][b].bitIsSet(c)

fun generateBetween(): List<List<BitBoard>> {
    val between: MutableList<MutableList<BitBoard>> = mutableListOf()
    for (a in 0..63) {
        between.add(a, mutableListOf())
        for (b in 0..63) {
            if (slidingAttack(a, 0u, rookDeltas).bitIsSet(b)) {
                between[a].add(b, slidingAttack(a, 0b1u.toULong() shl b, rookDeltas) and slidingAttack(b, 0b1u.toULong() shl a, rookDeltas))
            }
            else if (slidingAttack(a, 0u, bishopDeltas).bitIsSet(b)) {
                between[a].add(b, slidingAttack(a, 0b1u.toULong() shl b, bishopDeltas) and slidingAttack(b, 0b1u.toULong() shl a, bishopDeltas))
            }
            else {
                between[a].add(b, 0u)
            }
        }
    }
    return between
}

fun generateRays(): List<List<BitBoard>> {
    val rays: MutableList<MutableList<BitBoard>> = mutableListOf()
    for (a in 0..63) {
        rays.add(a, mutableListOf())
        for (b in 0..63) {
            if (slidingAttack(a, 0u, rookDeltas).bitIsSet(b)) {
                rays[a].add(b, (0b1u.toULong() shl a) or (0b1u.toULong() shl b) or slidingAttack(a, 0u, rookDeltas) and slidingAttack(b, 0u, rookDeltas))
            }
            else if (slidingAttack(a, 0u, bishopDeltas).bitIsSet(b)) {
                rays[a].add(b, (0b1u.toULong() shl a) or (0b1u.toULong() shl b) or slidingAttack(a, 0u, bishopDeltas) and slidingAttack(b, 0u, bishopDeltas))
            }
            else {
                rays[a].add(b, 0u)
            }
        }
    }
    return rays
}


fun slidingAttack(square: SquareBitIndex, occupied: BitBoard, deltas: List<Int>): BitBoard {
    var attacks = 0u.toULong()
    for (delta in deltas) {
        var sq = square
        do {
            val outOfBounds = !inBounds(sq, delta)
            sq += delta
            if (!outOfBounds) {
                attacks = attacks or (0b1u.toULong() shl sq)
            }
        }
        while (!(occupied.bitIsSet(sq) || outOfBounds))
    }
    return attacks
}

fun generateSliderAttacks(): Map<SquareBitIndex, BitBoard> {
    var subset = 0u.toULong()

    var magicMap: Map<SquareBitIndex, BitBoard> = mapOf()
    for (square in 0..63) {
        magicMap += initMagics(square, rookMagic[square], 12, rookDeltas) + initMagics(square, bishopMagic[square], 9, bishopDeltas)
    }

    return magicMap
}

fun initMagics(square: SquareBitIndex, magic: Magic, shift: Int, deltas: List<Int>): Map<Int, BitBoard> {
    var subset = 0u.toULong()

    val magicMap: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    do {
        val attack = slidingAttack(square, subset, deltas)
        val idx = ((magic.factor * subset) shr (64-shift)).toInt() + magic.offset
        magicMap[idx] = attack
        subset = (subset - magic.mask) and magic.mask
    }
    while (subset != 0u.toULong())

    return magicMap.toMap()
}



fun distance(sq1: SquareBitIndex, sq2: SquareBitIndex): Int {
    return maxOf(abs(sq1.rank() - sq2.rank()), abs(sq1.file() - sq2.file()))
}

fun inBounds(square: SquareBitIndex, delta: Int): Boolean {
    return (square + delta in 0..63) && distance(square, square+delta) <= 2
}

fun generateWhitePawnAdvances(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        if (inBounds(bit, 8)) {
            board = board.flipBit(bit+8)
        }

        if (bit.rank() == 2) { //If white pawn is on starting rank
            board = board.flipBit(bit+16)
        }

        map.put(bit, board)
    }
    return map.toMap()
}

fun generateBlackPawnAdvances(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        if (inBounds(bit, -8)) {
            board = board.flipBit(bit-8)
        }

        if (bit.rank() == 7) { //If black pawn is on starting rank
            board = board.flipBit(bit-16)
        }

        map.put(bit, board)
    }
    return map.toMap()
}

private fun generateBlackPawnAttacks(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        for (delta in blackPawnAttackDeltas) {
            if (inBounds(bit, delta)) {
                board = board.flipBit(bit+delta)
            }
        }
        map.put(bit, board)
    }
    return map.toMap()
}

private fun generateWhitePawnAttacks(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        for (delta in whitePawnAttackDeltas) {
            if (inBounds(bit, delta)) {
                board = board.flipBit(bit+delta)
            }
        }
        map.put(bit, board)
    }
    return map.toMap()
}

fun generateKingAttacks(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        for (delta in kingDeltas) {
            if (inBounds(bit, delta)) {
                board = board.flipBit(bit+delta)
            }
        }
        map.put(bit, board)
    }
    return map.toMap()
}

fun generateKnightAttacks(): Map<SquareBitIndex, BitBoard> {
    val map: MutableMap<SquareBitIndex, BitBoard> = mutableMapOf()
    for (bit in 0..63) {
        var board: BitBoard = 0u
        for (delta in knightDeltas) {
            if (inBounds(bit, delta)) {
                board = board.flipBit(bit+delta)
            }
        }
        map.put(bit, board)
    }
    return map.toMap()
}

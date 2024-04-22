import kotlin.math.abs

const val WHITEPAWN = 0
const val WHITEKNIGHT = 1
const val WHITEBISHOP = 2
const val WHITEROOK = 3
const val WHITEQUEEN = 4
const val WHITEKING = 5
const val BLACKPAWN = 6
const val BLACKKNIGHT = 7
const val BLACKBISHOP = 8
const val BLACKROOK = 9
const val BLACKQUEEN = 10
const val BLACKKING = 11




data class BoardState(
    val pieces: List<BitBoard> = listOf(
        0b1111111100000000u, //WHite Pawns
        0b1000010u, //White Knights
        0b100100u, //White Bishops
        0b10000001u, //White Rooks
        0b10000u, //White Queens
        0b1000u, //White King

        0b11111111000000000000000000000000000000000000000000000000u, //Black Pawns
        0b100001000000000000000000000000000000000000000000000000000000000u, //Black Knights
        0b10010000000000000000000000000000000000000000000000000000000000u, //Black Bishops
        0b1000000100000000000000000000000000000000000000000000000000000000u, //Black Rooks
        0b1000000000000000000000000000000000000000000000000000000000000u, //Black Queens
        0b100000000000000000000000000000000000000000000000000000000000u //Black King
    ),

    var halfMoveNumber: Int = 0, //Incremented every move
    var fullMoveNumber: Int = 1, //Incremented every white move
    var turn: Colour = Colour.WHITE,
    var gameState: GameState = GameState.NORMAL,
    var fiftyMoveCounter: Int = 0, //Reset whenever a capture or pawn move occurs

    var queensideCastling: Map<Colour, Boolean> = mapOf(Colour.WHITE to true, Colour.BLACK to true),
    var kingsideCastling: Map<Colour, Boolean> = mapOf(Colour.WHITE to true, Colour.BLACK to true),

    var enPassantSquare: SquareBitIndex? = null,

    var lastMove: Move? = null,
    var lastMoveAlgebraic: String = ""

) {
    private val allPieces: BitBoard = pieces[WHITEPAWN] or pieces[WHITEKNIGHT] or pieces[WHITEBISHOP] or pieces[WHITEROOK] or pieces[WHITEQUEEN] or pieces[WHITEKING] or
            pieces[BLACKPAWN] or pieces[BLACKKNIGHT] or pieces[BLACKBISHOP] or pieces[BLACKROOK] or pieces[BLACKQUEEN] or pieces[BLACKKING]
    private val whitePieces: BitBoard = pieces[WHITEPAWN] or pieces[WHITEKNIGHT] or pieces[WHITEBISHOP] or pieces[WHITEROOK] or pieces[WHITEQUEEN] or pieces[WHITEKING]
    private val blackPieces: BitBoard = pieces[BLACKPAWN] or pieces[BLACKKNIGHT] or pieces[BLACKBISHOP] or pieces[BLACKROOK] or pieces[BLACKQUEEN] or pieces[BLACKKING]
    private val whiteSliders: BitBoard = pieces[WHITEROOK] or pieces[WHITEBISHOP] or pieces[WHITEQUEEN]
    private val blackSliders: BitBoard = pieces[BLACKROOK] or pieces[BLACKBISHOP] or pieces[BLACKQUEEN]

    private val possibleMoves: List<Move> = generateMoves()

    fun copy(): BoardState {
        return BoardState(pieces.toList(), halfMoveNumber, fullMoveNumber, turn, gameState, fiftyMoveCounter, queensideCastling.toMap(), kingsideCastling.toMap(), enPassantSquare, lastMove?.copy(), lastMoveAlgebraic)
    }


    fun occupied(square: SquareBitIndex): Boolean {
        return (square.toBitBoard() and allPieces) != 0.toULong()
    }

    fun occupiedByEnemy(square: SquareBitIndex): Boolean {
        return (square.toBitBoard() and if (turn == Colour.WHITE) { blackPieces } else { whitePieces }) != 0.toULong()
    }

    fun occupiedByFriend(square: SquareBitIndex): Boolean {
        return (square.toBitBoard() and if (turn == Colour.WHITE) { whitePieces } else { blackPieces }) != 0.toULong()
    }


    fun identifySquare(square: SquareBitIndex): Int? {
        val bit = square.toBitBoard()
        for (i in 0..11) {
            if (bit and pieces[i] != 0.toULong()) {
                return i
            }
        }
        return null
    }

    // fromPiece removes a piece from view, for checking if squares the king moves to are attacked once removing the king from previous square
    fun attackers(square: SquareBitIndex, attacker: Colour, fromPiece: SquareBitIndex? = null, ePsquare: SquareBitIndex? = null): BitBoard {
        val enemy = if (attacker == Colour.WHITE) { whitePieces } else { blackPieces }
        var all = allPieces
        if (fromPiece != null) {
            all = all.flipBit(fromPiece)
        }
        if (ePsquare != null) {
            all = all.flipBit(ePsquare)
            console.log("All:\n" + all.toBinaryString("\n"))
        }
        return enemy and (
                    (rookAttacks(square, all) and (pieces[WHITEROOK] xor pieces[BLACKROOK] xor pieces[WHITEQUEEN] xor pieces[BLACKQUEEN])) or
                    (bishopAttacks(square, all) and (pieces[WHITEBISHOP] xor pieces[BLACKBISHOP] xor pieces[WHITEQUEEN] xor pieces[BLACKQUEEN])) or
                    ((knightAttacks[square] ?: 0u.toULong()) and (pieces[WHITEKNIGHT] xor pieces[BLACKKNIGHT])) or
                    ((kingAttacks[square] ?: 0u.toULong()) and (pieces[WHITEKING] xor pieces[BLACKKING])) or
                    (if (attacker == Colour.WHITE) { blackPawnAttacks[square] ?: 0u.toULong() } else { whitePawnAttacks[square] ?: 0u.toULong() } and (pieces[WHITEPAWN] xor pieces[BLACKPAWN]))
                )
    }

    fun moves(piece: SquareBitIndex? = null): List<Move> {
        return if (piece == null) { possibleMoves } else { possibleMoves.filter { it -> it.from == piece } }
    }

    private fun generateMoves(): List<Move> {
        val moves = mutableListOf<Move>()

        if (inCheck()) {
            val checkers = checkers() //must have size at least 1
            if (checkers.size > 2) {
                return moves
            }
            else if (checkers.size == 2) {
                moves.addAll(generateKingMoves().filter { !causesCheck(it) })
            }
            else if (checkers.size == 1) {
                val checker = checkers[0]
                moves.addAll(generateKingMoves(checker))
                moves.addAll(generatePawnMoves(checker))
                moves.addAll(generateKnightMoves(checker))
                moves.addAll(generateBishopMoves(checker))
                moves.addAll(generateRookMoves(checker))
                moves.addAll(generateQueenMoves(checker))
            }
        }
        else {
            moves.addAll(generatePawnMoves())
            moves.addAll(generateKnightMoves())
            moves.addAll(generateBishopMoves())
            moves.addAll(generateRookMoves())
            moves.addAll(generateQueenMoves())
            moves.addAll(generateKingMoves())
            moves.addAll(generateCastleMoves())
        }

        return moves.toList().filter { !causesCheck(it) }
    }

    private fun causesCheck(move: Move): Boolean {
        val kingColour = if (turn == Colour.WHITE) { WHITEKING } else { BLACKKING }
        if (identifySquare(move.from) == kingColour) {
            return attackers(move.to, !turn, move.from) != 0u.toULong()
        }

        val king = findKing()

        if (move.enPassantCapture == true) {
            return attackers(king, !turn, move.from, move.to + if (turn == Colour.WHITE) { -8 } else { 8 }) != 0u.toULong()
        }

        val blockers = sliderBlockers()
        if (blockers.bitIsSet(move.from)) {
            if (!aligned(move.from, move.to, king)) {
                return true
            }
        }

        return false
    }

    private fun inCheck(): Boolean = attackers(findKing(), !turn) != 0u.toULong()

    private fun checkers(): List<SquareBitIndex> = attackers(findKing(), !turn).bitScan()

    fun findKing(): SquareBitIndex = if (turn == Colour.WHITE) { pieces[WHITEKING].bitScan()[0] } else { pieces[BLACKKING].bitScan()[0] }

    private fun sliderBlockers(): BitBoard {
        val king = findKing()
        val snipers = if (turn == Colour.WHITE) { blackPieces } else { whitePieces } and (
            rookAttacks(king, 0u) and ((pieces[BLACKQUEEN] xor pieces[WHITEQUEEN]) xor (pieces[BLACKROOK] xor pieces[WHITEROOK])) or
            bishopAttacks(king, 0u) and ((pieces[BLACKQUEEN] xor pieces[WHITEQUEEN]) xor (pieces[BLACKBISHOP] xor pieces[WHITEBISHOP]))
        )
        var blockers: BitBoard = 0u
        for (sniper in snipers.bitScan()) {
            val bet = between[king][sniper] and allPieces
            if (bet.bitSize() == 1) {
                blockers = blockers or bet
            }
        }
        return blockers
    }



    private fun generateKingMoves(checker: SquareBitIndex? = null): List<Move> {
        val king = findKing()

        val moveList = mutableListOf<Move>()
        for (attack in kingAttacks.getOrElse(king) { 0u }.bitScan()) {
            if (!occupiedByFriend(attack)) {
                moveList.add(Move(king, attack))
            }
        }

        return moveList.toList()
    }

    private fun generateCastleMoves(): List<Move> {
        val king = findKing()

        val moveList = mutableListOf<Move>()

        val kingsideCastlingAllowed = kingsideCastling[turn]!!
        val queensideCastlingAllowed = queensideCastling[turn]!!
        val kingStartingSquare = if (turn == Colour.WHITE) { 3 } else { 59 }
        val kingsideRook = if (turn == Colour.WHITE) { 0 } else { 56 }
        val queensideRook = if (turn == Colour.WHITE) { 7 } else { 63 }

        if (kingsideCastlingAllowed) { //King is on starting square and kingside rook has not moved
            if (!inCheck() && !occupied(king-1) && !occupied(king-2) && !causesCheck(Move(king, king-1))) {
                moveList.add(Move(king, king-2))
            }
        }

        if (queensideCastlingAllowed) {
            if (!inCheck() && !occupied(king+1) && !occupied(king+2) && !occupied(king+3) && !causesCheck(Move(king, king+1))) {
                moveList.add(Move(king, king+2))
            }
        }

        return moveList.toList()
    }

    private fun generateQueenMoves(checker: SquareBitIndex? = null): List<Move> {
        val queens = if (turn == Colour.WHITE) { pieces[WHITEQUEEN].bitScan() } else { pieces[BLACKQUEEN].bitScan() }
        val moveList = mutableListOf<Move>()

        var blockingSquares = (0u.toULong()).inv() //full board is allowed
        if (checker != null) {
            val king = findKing()
            blockingSquares = between[king][checker].flipBit(checker)
        }

        for (queen in queens) {
            for (attack in (queenAttacks(queen, allPieces) and blockingSquares).bitScan()) {
                if (!occupiedByFriend(attack)) {
                    moveList.add(Move(queen, attack))
                }
            }
        }

        return moveList
    }

    private fun generateRookMoves(checker: SquareBitIndex? = null): List<Move> {
        val rooks = if (turn == Colour.WHITE) { pieces[WHITEROOK].bitScan() } else { pieces[BLACKROOK].bitScan() }
        val moveList = mutableListOf<Move>()

        var blockingSquares = (0u.toULong()).inv() //full board is allowed
        if (checker != null) {
            val king = findKing()
            blockingSquares = between[king][checker].flipBit(checker)
        }

        for (rook in rooks) {
            for (attack in (rookAttacks(rook, allPieces) and blockingSquares).bitScan()) {
                if (!occupiedByFriend(attack)) {
                    moveList.add(Move(rook, attack))
                }
            }
        }
        return moveList
    }

    private fun generateBishopMoves(checker: SquareBitIndex? = null): List<Move> {
        val bishops = if (turn == Colour.WHITE) { pieces[WHITEBISHOP].bitScan() } else { pieces[BLACKBISHOP].bitScan() }
        val moveList = mutableListOf<Move>()

        var blockingSquares = (0u.toULong()).inv() //full board is allowed
        if (checker != null) {
            val king = findKing()
            blockingSquares = between[king][checker].flipBit(checker)
        }

        for (bishop in bishops) {
            for (attack in (bishopAttacks(bishop, allPieces) and blockingSquares).bitScan()) {
                if (!occupiedByFriend(attack)) {
                    moveList.add(Move(bishop, attack))
                }
            }
        }
        return moveList
    }

    private fun generateKnightMoves(checker: SquareBitIndex? = null): List<Move> {
        val knights = if (turn == Colour.WHITE) { pieces[WHITEKNIGHT].bitScan() } else { pieces[BLACKKNIGHT].bitScan() }

        val moveList = mutableListOf<Move>()

        var blockingSquares = (0u.toULong()).inv() //full board is allowed
        if (checker != null) {
            val king = findKing()
            blockingSquares = between[king][checker].flipBit(checker)
        }

        for (knight in knights) {
            for (attack in (knightAttacks.getOrElse(knight) { 0u } and blockingSquares).bitScan()) {
                if (!occupiedByFriend(attack)) {
                    moveList.add(Move(knight, attack))
                }
            }
        }

        return moveList
    }

    private fun generatePawnMoves(checker: SquareBitIndex? = null): List<Move> {
        val pawns = if (turn == Colour.WHITE) { pieces[WHITEPAWN].bitScan() } else { pieces[BLACKPAWN].bitScan() }
        val startingRank = if (turn == Colour.WHITE) { 2 } else { 7 }
        val doubleAdvanceRank = if (turn == Colour.WHITE) { 4 } else { 5 }
        val enPassantDelta = if (turn == Colour.WHITE) { 8 } else { -8 }
        val backRank = if (turn == Colour.WHITE) { 8 } else { 1 }
        
        val advances = if (turn == Colour.WHITE) { whitePawnAdvances } else { blackPawnAdvances }
        val attacks = if (turn == Colour.WHITE) { whitePawnAttacks } else { blackPawnAttacks }

        var blockingSquares = (0u.toULong()).inv() //full board is allowed
        if (checker != null) {
            val king = findKing()
            blockingSquares = between[king][checker].flipBit(checker)
        }

        val moveList = mutableListOf<Move>()
        
        for (pawn in pawns) {
            for (advance in (advances.getOrElse(pawn) { 0u } and blockingSquares).bitScan()) {
                if (!occupied(advance)) {
                    if (advance.rank() == backRank) {
                        moveList.add(Move(pawn, advance, promotionPiece = if (turn == Colour.WHITE) { WHITEKNIGHT } else { BLACKKNIGHT }))
                        moveList.add(Move(pawn, advance, promotionPiece = if (turn == Colour.WHITE) { WHITEBISHOP } else { BLACKBISHOP }))
                        moveList.add(Move(pawn, advance, promotionPiece = if (turn == Colour.WHITE) { WHITEQUEEN } else { BLACKQUEEN }))
                        moveList.add(Move(pawn, advance, promotionPiece = if (turn == Colour.WHITE) { WHITEROOK } else { BLACKROOK }))
                    }
                    else {
                        moveList.add(Move(pawn, advance, enPassant = if (pawn.rank() == startingRank && advance.rank() == doubleAdvanceRank) { pawn + enPassantDelta } else { null }))
                    }
                }
            }
            
            for (attack in (attacks.getOrElse(pawn) { 0u } and blockingSquares).bitScan()) {
                if (occupiedByEnemy(attack)) {
                    if (attack.rank() == backRank) {
                        moveList.add(Move(pawn, attack, promotionPiece = if (turn == Colour.WHITE) { WHITEKNIGHT } else { BLACKKNIGHT }))
                        moveList.add(Move(pawn, attack, promotionPiece = if (turn == Colour.WHITE) { WHITEBISHOP } else { BLACKBISHOP }))
                        moveList.add(Move(pawn, attack, promotionPiece = if (turn == Colour.WHITE) { WHITEQUEEN } else { BLACKQUEEN }))
                        moveList.add(Move(pawn, attack, promotionPiece = if (turn == Colour.WHITE) { WHITEROOK } else { BLACKROOK }))
                    }
                    else {
                        moveList.add(Move(pawn, attack))
                    }
                }
                else if (!occupied(attack) && attack == enPassantSquare) {
                    moveList.add(Move(pawn, attack, enPassantCapture = true))
                }
            }
        }

        return moveList.toList()
    }

    fun makeMove(move: Move): BoardState {
        val newTurn = if (this.turn == Colour.WHITE) { Colour.BLACK } else { Colour.WHITE }
        val newHalfMoveNumber = this.halfMoveNumber + 1
        val newFullMoveNumber = this.fullMoveNumber + if (turn == Colour.BLACK) { 1 } else { 0 }

        var newQueensideCastling = this.queensideCastling.toMutableMap()
        var newKingsideCastling = this.kingsideCastling.toMutableMap()

        val fromPiece = identifySquare(move.from)!! //always set
        val toPiece = identifySquare(move.to) //Only set if move is a capture

        val newPieces = this.pieces.toMutableList()

        newPieces[fromPiece] = this.pieces[fromPiece].flipBit(move.from).flipBit(move.to)

        if (toPiece != null) { //if capture, delete captured piece
            newPieces[toPiece] = this.pieces[toPiece].flipBit(move.to)
        }
        else if (move.enPassantCapture) { //if en passant capture, delete double advanced pawn
            val enemyPawns = if (this.turn == Colour.WHITE) { BLACKPAWN } else { WHITEPAWN }
            val delta = if (this.turn == Colour.WHITE) { -8 } else { 8 }
            newPieces[enemyPawns] = newPieces[enemyPawns].flipBit(move.to + delta)
        }

        if (move.promotionPiece != null) {
            newPieces[fromPiece] = newPieces[fromPiece].flipBit(move.to) //Delete pawn
            newPieces[move.promotionPiece] = newPieces[move.promotionPiece].flipBit(move.to) //Add promoted piece
        }

        val rookQueensideStart = if (turn == Colour.WHITE) { 7 } else { 63 }
        val rookKingsideStart = if (turn == Colour.WHITE) { 0 } else { 56 }
        val rookEnemyQueensideStart = if (turn == Colour.WHITE) { 63 } else { 7 }
        val rookEnemyKingsideStart = if (turn == Colour.WHITE) { 56 } else { 0 }
        val rook = if (turn == Colour.WHITE) { WHITEROOK } else { BLACKROOK }
        val enemyRook = if (turn == Colour.WHITE) { BLACKROOK } else { WHITEROOK }
        var queensideCastle = false
        var kingsideCastle = false
        if ((fromPiece == WHITEKING || fromPiece == BLACKKING) && distance(move.from, move.to) == 2) { //If castling


            if (move.to - move.from == 2) { // queenside castling
                newPieces[rook] = this.pieces[rook].flipBit(rookQueensideStart).flipBit(rookQueensideStart - 3)
                queensideCastle = true
            }
            else if (move.to - move.from == -2) { //kingside castling
                newPieces[rook] = this.pieces[rook].flipBit(rookKingsideStart).flipBit(rookKingsideStart + 2)
                kingsideCastle = true
            }

            newKingsideCastling[turn] = false
            newQueensideCastling[turn] = false
        }

        if (fromPiece == rook && move.from == rookQueensideStart) {
            newQueensideCastling[turn] = false
        }
        else if (fromPiece == rook && move.from == rookKingsideStart) {
            newKingsideCastling[turn] = false
        }
        else if (fromPiece == WHITEKING || fromPiece == BLACKKING) {
            newKingsideCastling[turn] = false
            newQueensideCastling[turn] = false
        }
        else if (toPiece != null && toPiece == enemyRook) {
            if (move.to == rookEnemyKingsideStart) {
                newKingsideCastling[!turn] = false
            }
            else if (move.to == rookEnemyQueensideStart) {
                newQueensideCastling[!turn] = false
            }
        }




        val newGame = BoardState (
            newPieces,
            newHalfMoveNumber,
            newFullMoveNumber,
            newTurn,

            kingsideCastling = newKingsideCastling.toMap(),
            queensideCastling = newQueensideCastling.toMap(),
            enPassantSquare = move.enPassant
        )

        val nextKing = if (turn == Colour.WHITE) { BLACKKING } else { WHITEKING }
        if (newGame.moves().isEmpty() && newGame.attackers(newGame.pieces[nextKing].bitScan()[0], turn) != 0u.toULong()) {
            newGame.gameState = GameState.CHECKMATE
        }
        else if (newGame.moves().isEmpty() && newGame.attackers(newGame.pieces[nextKing].bitScan()[0], turn) == 0u.toULong()) {
            newGame.gameState = GameState.STALEMATE
        }
        else if (newGame.inCheck()) {
            newGame.gameState = GameState.CHECK
        }
        else if (newGame.fiftyMoveCounter >= 50) {
            newGame.gameState = GameState.DRAW
        }
        else {
            newGame.gameState = GameState.NORMAL
        }

        newGame.lastMove = move
        newGame.lastMoveAlgebraic = algebraicNotation(move, newGame.gameState, kingsideCastle, queensideCastle)
        return newGame
    }

    fun algebraicNotation(
            move: Move,
            newGameState: GameState,
            kingsideCastle: Boolean,
            queensideCastle: Boolean
        ): String {


        var str = ""
        if (newGameState == GameState.CHECKMATE) {
            str = str + "#"
        }
        else if (newGameState == GameState.CHECK) {
            str = str + "+"
        }

        if (kingsideCastle) {
            return "O-O" + str
        }
        else if (queensideCastle) {
            return "O-O-O" + str
        }

        fun pieceNumberToLetter(num: Int?): Char {
            return when (num) {
                WHITEBISHOP, BLACKBISHOP -> 'B'
                WHITEKNIGHT, BLACKKNIGHT -> 'N'
                WHITEQUEEN, BLACKQUEEN -> 'Q'
                WHITEROOK, BLACKROOK -> 'R'
                WHITEKING, BLACKKING -> 'K'
                else -> ' '
            }
        }

        if (move.promotionPiece != null) {
            str = "=" + pieceNumberToLetter(move.promotionPiece) + str
        }

        str = move.to.toAlgebraic() + str
        val fromPiece = identifySquare(move.from)!!





        if (occupiedByEnemy(move.to) || move.enPassantCapture) {
            str = "x" + str
            if (fromPiece == WHITEPAWN || fromPiece == BLACKPAWN) {
                str = move.from.fileChar() + str
            }
            else {
                var attackers = (attackers(move.to, turn) and pieces[fromPiece]).bitScan().toMutableList()
                console.log(attackers.toString())
                attackers.remove(move.from)
                console.log(attackers.toString())
                val files = attackers.map { it.file() }
                val ranks = attackers.map { it.rank() }
                if (files.contains(move.from.file()) && ranks.contains(move.from.rank())) {
                    str = move.from.fileChar().toString() + move.from.rank().toString() + str
                }
                else if (files.contains(move.from.file())) {
                    str = move.from.rank().toString() + str
                }
                else if (ranks.contains(move.from.rank())) {
                    str = move.from.fileChar().toString() + str
                }
            }
        }

        if (!(fromPiece == WHITEPAWN || fromPiece == BLACKPAWN)) {
            str = pieceNumberToLetter(fromPiece) + str
        }

        return str
    }
}
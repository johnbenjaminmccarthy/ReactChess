import dom.html.HTMLButtonElement
import kotlinx.browser.window
import react.*
import react.dom.html.ButtonHTMLAttributes
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2

enum class Colour {
    WHITE, BLACK
}

operator fun Colour.not(): Colour = if (this == Colour.WHITE) { Colour.BLACK } else { Colour.WHITE }

enum class Piece {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

enum class GameState {
    NORMAL, CHECK, CHECKMATE, DRAW, STALEMATE
}


val App = FC<Props> {
    h1 {
        +"ReactChess"
    }


    var boardState by useState(BoardState())
    var possibleMoves: List<Move> by useState(listOf())
    var selectedSquare: SquareBitIndex? by useState(null)
    var historyState: List<Pair<BoardState, String>> by useState(listOf())
    var boardFrozen by useState(false)
    var promotionList: List<Move> by useState(listOf())


    fun newGame() {
        boardState = BoardState()
        historyState = listOf()
        possibleMoves = listOf()
        selectedSquare = null
        boardFrozen = false
        promotionList = listOf()
    }

    fun squareClickFun (square: SquareBitIndex, moves: List<Move>? = null) {
        console.log("Square: ${square.toAlgebraic()} Moves: ${moves?.joinToString (", ")}")
        if (boardFrozen) {
            return
        }
        var move: Move? = null
        if (moves != null && moves.size == 1) {
            move = moves[0]
        }
        else if (moves != null && moves.size == 4) { // Promotion
            promotionList = moves
            return
        }

        if (square == selectedSquare) {
            selectedSquare = null
            possibleMoves = listOf()
            promotionList = listOf()
            return
        }
        else if (selectedSquare == null) {
            selectedSquare = square
            possibleMoves = boardState.moves(square)
            promotionList = listOf()
            return
        }
        else {
            if (move != null && move.to == square) {
                val oldBoardState = boardState.copy()
                val newBoardState = oldBoardState.makeMove(move)
                historyState = historyState.toList() + listOf(Pair(oldBoardState, newBoardState.lastMoveAlgebraic))
                boardState = newBoardState

                selectedSquare = null
                possibleMoves = listOf()
                promotionList = listOf()
                if (boardState.gameState == GameState.DRAW || boardState.gameState == GameState.CHECKMATE || boardState.gameState == GameState.STALEMATE) {
                    boardFrozen = true
                }
                return
            }
            else {
                selectedSquare = square
                possibleMoves = boardState.moves(square)
                promotionList = listOf()
                return
            }
        }
    }

    fun historyClickFun(index: Int) {

    }

    div {
        id = "container"

        +"board frozen: $boardFrozen"

        BoardDisplay {
            clickFun = ::squareClickFun
            board = boardState
            moves = possibleMoves
            selected = selectedSquare
            promotions = promotionList
        }

        HistoryDisplay {
            clickFun = ::historyClickFun
            history = historyState
            newGameFun = ::newGame
        }
    }
}




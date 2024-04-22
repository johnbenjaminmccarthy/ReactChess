import csstype.ClassName
import dom.html.HTMLDivElement
import react.*
import kotlinx.browser.window
import react.dom.events.DragEvent
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr

external interface BoardDisplayProps : Props {
    var clickFun: (SquareBitIndex, List<Move>?) -> Unit
    var board: BoardState
    var moves: List<Move>
    var selected: SquareBitIndex?
    var promotions: List<Move>
    var newGameFun: () -> Unit
}

val BoardDisplay = FC<BoardDisplayProps> { props ->
    fun pieceChar(piece: Int?): String {
        return when (piece) {
            WHITEPAWN -> "whitepawn"
            WHITEKNIGHT -> "whiteknight"
            WHITEBISHOP -> "whitebishop"
            WHITEROOK -> "whiterook"
            WHITEQUEEN -> "whitequeen"
            WHITEKING -> "whiteking"
            BLACKPAWN -> "blackpawn"
            BLACKKNIGHT -> "blackknight"
            BLACKBISHOP -> "blackbishop"
            BLACKROOK -> "blackrook"
            BLACKQUEEN -> "blackqueen"
            BLACKKING -> "blackking"
            else -> ""
        }
    }

    fun squareColour(sq: SquareBitIndex): Colour {
        return if (sq % 2 == (sq / 8) % 2) { Colour.WHITE } else { Colour.BLACK }
    }



    val moveTos = props.moves.groupBy { it.to }
    val promotionSquareTo = if (props.promotions.isNotEmpty()) { props.promotions[0].to } else { null }

    /*fun onDrop(event: DragEvent<HTMLDivElement>, bitIndex: SquareBitIndex): Unit {
        props.clickFun(bitIndex, moveTos.get(bitIndex))
    }*/

    div {
        id = "board"

        table {
            tbody {
                tr {
                    td {
                        className = ClassName("border")
                        div {
                            className = ClassName("content")
                        }
                    }
                    for (col in 1..8) {
                        td {
                            className = ClassName("border top")
                            div {
                                className = ClassName("content")
                                +('a'.code + col - 1).toChar().toString()
                            }
                        }
                    }
                    td {
                        className = ClassName("border")
                        div {
                            className = ClassName("content")
                        }
                    }
                }
                for (rank in 8 downTo 1) {
                    tr {
                        td {
                            className = ClassName("border left")
                            div {
                                className = ClassName("content")
                                +rank.toString()
                            }
                        }
                        for (file in 1..8) {
                            val bitIndex: SquareBitIndex = (rank - 1)*8 + (8-file)
                            td {
                                className = ClassName("square" + if (squareColour(bitIndex) == Colour.WHITE) { " white" } else { " black" })
                                div {
                                    className = ClassName("content" + if (bitIndex == props.selected) { " selected" } else { "" } + if (moveTos.containsKey(bitIndex)) { " moveOption" } else { "" })

                                    div {
                                        className = ClassName("piece " + pieceChar(props.board.identifySquare(bitIndex)).toString())
                                        /*draggable = true
                                        onDrag = { e ->
                                            e.preventDefault()
                                            console.log("dragging ${bitIndex.toAlgebraic()}")
                                        }*/
                                    }
                                    onClick = {
                                        props.clickFun(bitIndex, moveTos.get(bitIndex))
                                        console.log("Clicked $bitIndex")
                                        console.log(props.board.lastMoveAlgebraic)
                                    }
                                    /*onDrop = { e ->
                                        console.log("dropped on ${bitIndex.toAlgebraic()}")
                                        if (moveTos.containsKey(bitIndex)) {
                                            onDrop(e, bitIndex)
                                            console.log("dropped on ${bitIndex.toAlgebraic()}")
                                        }
                                    }*/
                                }
                                if (promotionSquareTo != null && bitIndex == promotionSquareTo) {
                                    PromotionPicker {
                                        promotionList = props.promotions
                                        clickFunction = props.clickFun
                                        square = promotionSquareTo
                                        pieceCharFun = ::pieceChar
                                    }
                                }
                            }
                        }
                        td {
                            className = ClassName("border right")
                            div {
                                className = ClassName("content")
                                +rank.toString()
                            }
                        }
                    }
                }
                tr {
                    td {
                        className = ClassName("border")
                        div {
                            className = ClassName("content")
                        }
                    }
                    for (col in 1..8) {
                        td {
                            className = ClassName("border bottom")
                            div {
                                className = ClassName("content")
                                +('a'.code + col - 1).toChar().toString()
                            }
                        }
                    }
                    td {
                        className = ClassName("border")
                        div {
                            className = ClassName("content")
                        }
                    }
                }
            }
        }
        div {
            id = "state"
            div {
                id ="gamestate"
                +"Game state: ${props.board.gameState}"
            }
            div {
                id ="turnnumber"
                +"Move: ${props.board.fullMoveNumber}"
            }
            div {
                id="turn"
                when (props.board.gameState) {
                    GameState.NORMAL, GameState.CHECK -> +"${if (props.board.turn == Colour.WHITE) { "White" } else { "Black" }} to play"
                    GameState.CHECKMATE -> +"${if (props.board.turn == Colour.WHITE) { "Black" } else { "White"}} wins!"
                    else -> +""
                }
            }
        }

        button {
            className = ClassName("newgame")
            +"New game"
            onClick = {
                props.newGameFun()
            }
        }
    }

}

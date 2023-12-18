data class Move (
    val from: SquareBitIndex,
    val to: SquareBitIndex,
    val algebraicNotation: String = "",
    val enPassant: SquareBitIndex? = null,
    val enPassantCapture: Boolean = false,
    val promotionPiece: Int? = null
) {
    override fun toString(): String {
        return "${from.toAlgebraic()} to ${to.toAlgebraic()}" + if(promotionPiece != null) { "=$promotionPiece" } else { "" }
    }
}
import csstype.ClassName
import react.FC
import react.Props
import react.dom.html.ReactHTML.div

external interface PromotionPickerProps : Props {
    var clickFunction: (SquareBitIndex, List<Move>?) -> Unit
    var square: SquareBitIndex
    var promotionList: List<Move>
    var pieceCharFun: (Int?) -> String
}

val PromotionPicker = FC<PromotionPickerProps> { props ->
    div {
        className = ClassName("promotionPicker")
        for (move in props.promotionList) {
            div {
                className = ClassName("piece " + props.pieceCharFun(move.promotionPiece) + " floating")
                onClick = {
                    props.clickFunction(props.square, listOf(move))
                }
            }
        }
    }
}
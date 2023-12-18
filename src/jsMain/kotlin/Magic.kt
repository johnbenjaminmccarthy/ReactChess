data class Magic(
    val mask: BitBoard,
    val factor: BitBoard,
    val offset: Int
) {
    fun bishopIndex(occupied: BitBoard): Int = (factor * (occupied and mask) shr (64 - 9)).toInt() + offset
    fun rookIndex(occupied: BitBoard): Int = (factor * (occupied and mask) shr (64 - 12)).toInt() + offset
}

val rookMagic = arrayOf<Magic>(
    Magic(0x000101010101017eu, 0x00280077ffebfffeu, 26304),
    Magic(0x000202020202027cu, 0x2004010201097fffu, 35520),
    Magic(0x000404040404047au, 0x0010020010053fffu, 38592),
    Magic(0x0008080808080876u, 0x0040040008004002u, 8026),
    Magic(0x001010101010106eu, 0x7fd00441ffffd003u, 22196),
    Magic(0x002020202020205eu, 0x4020008887dffffeu, 80870),
    Magic(0x004040404040403eu, 0x004000888847ffffu, 76747),
    Magic(0x008080808080807eu, 0x006800fbff75fffdu, 30400),
    Magic(0x0001010101017e00u, 0x000028010113ffffu, 11115),
    Magic(0x0002020202027c00u, 0x0020040201fcffffu, 18205),
    Magic(0x0004040404047a00u, 0x007fe80042ffffe8u, 53577),
    Magic(0x0008080808087600u, 0x00001800217fffe8u, 62724),
    Magic(0x0010101010106e00u, 0x00001800073fffe8u, 34282),
    Magic(0x0020202020205e00u, 0x00001800e05fffe8u, 29196),
    Magic(0x0040404040403e00u, 0x00001800602fffe8u, 23806),
    Magic(0x0080808080807e00u, 0x000030002fffffa0u, 49481),
    Magic(0x00010101017e0100u, 0x00300018010bffffu, 2410),
    Magic(0x00020202027c0200u, 0x0003000c0085fffbu, 36498),
    Magic(0x00040404047a0400u, 0x0004000802010008u, 24478),
    Magic(0x0008080808760800u, 0x0004002020020004u, 10074),
    Magic(0x00101010106e1000u, 0x0001002002002001u, 79315),
    Magic(0x00202020205e2000u, 0x0001001000801040u, 51779),
    Magic(0x00404040403e4000u, 0x0000004040008001u, 13586),
    Magic(0x00808080807e8000u, 0x0000006800cdfff4u, 19323),
    Magic(0x000101017e010100u, 0x0040200010080010u, 70612),
    Magic(0x000202027c020200u, 0x0000080010040010u, 83652),
    Magic(0x000404047a040400u, 0x0004010008020008u, 63110),
    Magic(0x0008080876080800u, 0x0000040020200200u, 34496),
    Magic(0x001010106e101000u, 0x0002008010100100u, 84966),
    Magic(0x002020205e202000u, 0x0000008020010020u, 54341),
    Magic(0x004040403e404000u, 0x0000008020200040u, 60421),
    Magic(0x008080807e808000u, 0x0000820020004020u, 86402),
    Magic(0x0001017e01010100u, 0x00fffd1800300030u, 50245),
    Magic(0x0002027c02020200u, 0x007fff7fbfd40020u, 76622),
    Magic(0x0004047a04040400u, 0x003fffbd00180018u, 84676),
    Magic(0x0008087608080800u, 0x001fffde80180018u, 78757),
    Magic(0x0010106e10101000u, 0x000fffe0bfe80018u, 37346),
    Magic(0x0020205e20202000u, 0x0001000080202001u, 370),
    Magic(0x0040403e40404000u, 0x0003fffbff980180u, 42182),
    Magic(0x0080807e80808000u, 0x0001fffdff9000e0u, 45385),
    Magic(0x00017e0101010100u, 0x00fffefeebffd800u, 61659),
    Magic(0x00027c0202020200u, 0x007ffff7ffc01400u, 12790),
    Magic(0x00047a0404040400u, 0x003fffbfe4ffe800u, 16762),
    Magic(0x0008760808080800u, 0x001ffff01fc03000u, 0),
    Magic(0x00106e1010101000u, 0x000fffe7f8bfe800u, 38380),
    Magic(0x00205e2020202000u, 0x0007ffdfdf3ff808u, 11098),
    Magic(0x00403e4040404000u, 0x0003fff85fffa804u, 21803),
    Magic(0x00807e8080808000u, 0x0001fffd75ffa802u, 39189),
    Magic(0x007e010101010100u, 0x00ffffd7ffebffd8u, 58628),
    Magic(0x007c020202020200u, 0x007fff75ff7fbfd8u, 44116),
    Magic(0x007a040404040400u, 0x003fff863fbf7fd8u, 78357),
    Magic(0x0076080808080800u, 0x001fffbfdfd7ffd8u, 44481),
    Magic(0x006e101010101000u, 0x000ffff810280028u, 64134),
    Magic(0x005e202020202000u, 0x0007ffd7f7feffd8u, 41759),
    Magic(0x003e404040404000u, 0x0003fffc0c480048u, 1394),
    Magic(0x007e808080808000u, 0x0001ffffafd7ffd8u, 40910),
    Magic(0x7e01010101010100u, 0x00ffffe4ffdfa3bau, 66516),
    Magic(0x7c02020202020200u, 0x007fffef7ff3d3dau, 3897),
    Magic(0x7a04040404040400u, 0x003fffbfdfeff7fau, 3930),
    Magic(0x7608080808080800u, 0x001fffeff7fbfc22u, 72934),
    Magic(0x6e10101010101000u, 0x0000020408001001u, 72662),
    Magic(0x5e20202020202000u, 0x0007fffeffff77fdu, 56325),
    Magic(0x3e40404040404000u, 0x0003ffffbf7dfeecu, 66501),
    Magic(0x7e80808080808000u, 0x0001ffff9dffa333u, 14826)
)

val bishopMagic = arrayOf<Magic>(
    Magic(0x0040201008040200u, 0x007fbfbfbfbfbfffu, 5378),
    Magic(0x0000402010080400u, 0x0000a060401007fcu, 4093),
    Magic(0x0000004020100a00u, 0x0001004008020000u, 4314),
    Magic(0x0000000040221400u, 0x0000806004000000u, 6587),
    Magic(0x0000000002442800u, 0x0000100400000000u, 6491),
    Magic(0x0000000204085000u, 0x000021c100b20000u, 6330),
    Magic(0x0000020408102000u, 0x0000040041008000u, 5609),
    Magic(0x0002040810204000u, 0x00000fb0203fff80u, 22236),
    Magic(0x0020100804020000u, 0x0000040100401004u, 6106),
    Magic(0x0040201008040000u, 0x0000020080200802u, 5625),
    Magic(0x00004020100a0000u, 0x0000004010202000u, 16785),
    Magic(0x0000004022140000u, 0x0000008060040000u, 16817),
    Magic(0x0000000244280000u, 0x0000004402000000u, 6842),
    Magic(0x0000020408500000u, 0x0000000801008000u, 7003),
    Magic(0x0002040810200000u, 0x000007efe0bfff80u, 4197),
    Magic(0x0004081020400000u, 0x0000000820820020u, 7356),
    Magic(0x0010080402000200u, 0x0000400080808080u, 4602),
    Magic(0x0020100804000400u, 0x00021f0100400808u, 4538),
    Magic(0x004020100a000a00u, 0x00018000c06f3fffu, 29531),
    Magic(0x0000402214001400u, 0x0000258200801000u, 45393),
    Magic(0x0000024428002800u, 0x0000240080840000u, 12420),
    Magic(0x0002040850005000u, 0x000018000c03fff8u, 15763),
    Magic(0x0004081020002000u, 0x00000a5840208020u, 5050),
    Magic(0x0008102040004000u, 0x0000020008208020u, 4346),
    Magic(0x0008040200020400u, 0x0000804000810100u, 6074),
    Magic(0x0010080400040800u, 0x0001011900802008u, 7866),
    Magic(0x0020100a000a1000u, 0x0000804000810100u, 32139),
    Magic(0x0040221400142200u, 0x000100403c0403ffu, 57673),
    Magic(0x0002442800284400u, 0x00078402a8802000u, 55365),
    Magic(0x0004085000500800u, 0x0000101000804400u, 15818),
    Magic(0x0008102000201000u, 0x0000080800104100u, 5562),
    Magic(0x0010204000402000u, 0x00004004c0082008u, 6390),
    Magic(0x0004020002040800u, 0x0001010120008020u, 7930),
    Magic(0x0008040004081000u, 0x000080809a004010u, 13329),
    Magic(0x00100a000a102000u, 0x0007fefe08810010u, 7170),
    Magic(0x0022140014224000u, 0x0003ff0f833fc080u, 27267),
    Magic(0x0044280028440200u, 0x007fe08019003042u, 53787),
    Magic(0x0008500050080400u, 0x003fffefea003000u, 5097),
    Magic(0x0010200020100800u, 0x0000101010002080u, 6643),
    Magic(0x0020400040201000u, 0x0000802005080804u, 6138),
    Magic(0x0002000204081000u, 0x0000808080a80040u, 7418),
    Magic(0x0004000408102000u, 0x0000104100200040u, 7898),
    Magic(0x000a000a10204000u, 0x0003ffdf7f833fc0u, 42012),
    Magic(0x0014001422400000u, 0x0000008840450020u, 57350),
    Magic(0x0028002844020000u, 0x00007ffc80180030u, 22813),
    Magic(0x0050005008040200u, 0x007fffdd80140028u, 56693),
    Magic(0x0020002010080400u, 0x00020080200a0004u, 5818),
    Magic(0x0040004020100800u, 0x0000101010100020u, 7098),
    Magic(0x0000020408102000u, 0x0007ffdfc1805000u, 4451),
    Magic(0x0000040810204000u, 0x0003ffefe0c02200u, 4709),
    Magic(0x00000a1020400000u, 0x0000000820806000u, 4794),
    Magic(0x0000142240000000u, 0x0000000008403000u, 13364),
    Magic(0x0000284402000000u, 0x0000000100202000u, 4570),
    Magic(0x0000500804020000u, 0x0000004040802000u, 4282),
    Magic(0x0000201008040200u, 0x0004010040100400u, 14964),
    Magic(0x0000402010080400u, 0x00006020601803f4u, 4026),
    Magic(0x0002040810204000u, 0x0003ffdfdfc28048u, 4826),
    Magic(0x0004081020400000u, 0x0000000820820020u, 7354),
    Magic(0x000a102040000000u, 0x0000000008208060u, 4848),
    Magic(0x0014224000000000u, 0x0000000000808020u, 15946),
    Magic(0x0028440200000000u, 0x0000000001002020u, 14932),
    Magic(0x0050080402000000u, 0x0000000401002008u, 16588),
    Magic(0x0020100804020000u, 0x0000004040404040u, 6905),
    Magic(0x0040201008040200u, 0x007fff9fdf7ff813u, 16076)
)

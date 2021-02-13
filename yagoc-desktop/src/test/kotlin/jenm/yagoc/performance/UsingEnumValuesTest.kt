package jenm.yagoc.performance

class UsingEnumValuesTest {
//
//    val counter = AtomicInteger()
//
//    @Test
//    @Timeout(4)
//    fun `square board impl via index`() {
//        val squareBoard = SquareBoard()
//        IntRange(0, 10000000).forEach {
//            val x = squareBoard[Squares.values()[it % 63].ordinal]
//            val y = squareBoard[Squares.values()[it % 63].ordinal]
//            val z = squareBoard[Squares.values()[it % 63].ordinal]
//            squareBoard[Squares.values()[it % 63].ordinal] = none
//        }
//    }
//
//    @Test
//    @Timeout(3)
//    fun `square board impl via square using values`() {
//        val squareBoard = SquareBoard()
//        IntRange(0, 10000000).forEach {
//            val x = squareBoard[Squares.values()[it % 63]]
//            val y = squareBoard[Squares.values()[it % 63]]
//            val z = squareBoard[Squares.values()[it % 63]]
//            squareBoard[Squares.values()[it % 63]] = none
//        }
//    }
//
//    @Test
//    @Timeout(3)
//    fun `square board impl via square again using values`() {
//        val squareBoard = SquareBoard()
//        IntRange(0, 10000000).forEach {
//            val x = squareBoard[Squares.values()[it % 63]]
//            val y = squareBoard[Squares.values()[it % 63]]
//            val z = squareBoard[Squares.values()[it % 63]]
//            squareBoard[Squares.values()[it % 63]] = none
//        }
//    }
//
//    @Test
//    @Timeout(2)
//    fun `square board impl via squareboard again but using valueOf instead of values`() {
//        val squareBoard = SquareBoard()
//        IntRange(0, 10000000).forEach {
//            val x = squareBoard[Squares.valueOf(it % 63)]
//            val y = squareBoard[Squares.valueOf(it % 63)]
//            val z = squareBoard[Squares.valueOf(it % 63)]
//            squareBoard[Squares.values()[it % 63]] = none
//        }
//    }
//
//    @Test
//    @Timeout(2)
//    fun `square board impl via array and valueOf`() {
//        val squareBoard = Array(64) { none }
//        IntRange(0, 10000000).forEach {
//            val x = squareBoard[Squares.valueOf(it % 63).ordinal]
//            val y = squareBoard[Squares.valueOf(it % 63).ordinal]
//            val z = squareBoard[Squares.valueOf(it % 63).ordinal]
//            squareBoard[Squares.values()[it % 63].ordinal] = none
//        }
//    }
}
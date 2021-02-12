package yagoc.board

const val a8 = 0

@JvmField
val a8Square = Square(a8)
const val b8 = 1

@JvmField
val b8Square = Square(b8)
const val c8 = 2

@JvmField
val c8Square = Square(c8)
const val d8 = 3

@JvmField
val d8Square = Square(d8)
const val e8 = 4

@JvmField
val e8Square = Square(e8)
const val f8 = 5

@JvmField
val f8Square = Square(f8)
const val g8 = 6

@JvmField
val g8Square = Square(g8)
const val h8 = 7

@JvmField
val h8Square = Square(h8)
const val a7 = 8

@JvmField
val a7Square = Square(a7)
const val b7 = 9

@JvmField
val b7Square = Square(b7)
const val c7 = 10

@JvmField
val c7Square = Square(c7)
const val d7 = 11

@JvmField
val d7Square = Square(d7)
const val e7 = 12

@JvmField
val e7Square = Square(e7)
const val f7 = 13

@JvmField
val f7Square = Square(f7)
const val g7 = 14

@JvmField
val g7Square = Square(g7)
const val h7 = 15

@JvmField
val h7Square = Square(h7)
const val a6 = 16

@JvmField
val a6Square = Square(a6)
const val b6 = 17

@JvmField
val b6Square = Square(b6)
const val c6 = 18

@JvmField
val c6Square = Square(c6)
const val d6 = 19

@JvmField
val d6Square = Square(d6)
const val e6 = 20

@JvmField
val e6Square = Square(e6)
const val f6 = 21

@JvmField
val f6Square = Square(f6)
const val g6 = 22

@JvmField
val g6Square = Square(g6)
const val h6 = 23

@JvmField
val h6Square = Square(h6)
const val a5 = 24

@JvmField
val a5Square = Square(a5)
const val b5 = 25

@JvmField
val b5Square = Square(b5)
const val c5 = 26

@JvmField
val c5Square = Square(c5)
const val d5 = 27

@JvmField
val d5Square = Square(d5)
const val e5 = 28

@JvmField
val e5Square = Square(e5)
const val f5 = 29

@JvmField
val f5Square = Square(f5)
const val g5 = 30

@JvmField
val g5Square = Square(g5)
const val h5 = 31

@JvmField
val h5Square = Square(h5)
const val a4 = 32

@JvmField
val a4Square = Square(a4)
const val b4 = 33

@JvmField
val b4Square = Square(b4)
const val c4 = 34

@JvmField
val c4Square = Square(c4)
const val d4 = 35

@JvmField
val d4Square = Square(d4)
const val e4 = 36

@JvmField
val e4Square = Square(e4)
const val f4 = 37

@JvmField
val f4Square = Square(f4)
const val g4 = 38

@JvmField
val g4Square = Square(g4)
const val h4 = 39

@JvmField
val h4Square = Square(h4)
const val a3 = 40

@JvmField
val a3Square = Square(a3)
const val b3 = 41

@JvmField
val b3Square = Square(b3)
const val c3 = 42

@JvmField
val c3Square = Square(c3)
const val d3 = 43

@JvmField
val d3Square = Square(d3)
const val e3 = 44

@JvmField
val e3Square = Square(e3)
const val f3 = 45

@JvmField
val f3Square = Square(f3)
const val g3 = 46

@JvmField
val g3Square = Square(g3)
const val h3 = 47

@JvmField
val h3Square = Square(h3)
const val a2 = 48

@JvmField
val a2Square = Square(a2)
const val b2 = 49

@JvmField
val b2Square = Square(b2)
const val c2 = 50

@JvmField
val c2Square = Square(c2)
const val d2 = 51

@JvmField
val d2Square = Square(d2)
const val e2 = 52

@JvmField
val e2Square = Square(e2)
const val f2 = 53

@JvmField
val f2Square = Square(f2)
const val g2 = 54

@JvmField
val g2Square = Square(g2)
const val h2 = 55

@JvmField
val h2Square = Square(h2)
const val a1 = 56

@JvmField
val a1Square = Square(a1)
const val b1 = 57

@JvmField
val b1Square = Square(b1)
const val c1 = 58

@JvmField
val c1Square = Square(c1)
const val d1 = 59

@JvmField
val d1Square = Square(d1)
const val e1 = 60

@JvmField
val e1Square = Square(e1)
const val f1 = 61

@JvmField
val f1Square = Square(f1)
const val g1 = 62

@JvmField
val g1Square = Square(g1)
const val h1 = 63

@JvmField
val h1Square = Square(h1)

@JvmField
val allSquares = arrayOf(
    a8Square, b8Square, c8Square, d8Square, e8Square, f8Square, g8Square, h8Square,
    a7Square, b7Square, c7Square, d7Square, e7Square, f7Square, g7Square, h7Square,
    a6Square, b6Square, c6Square, d6Square, e6Square, f6Square, g6Square, h6Square,
    a5Square, b5Square, c5Square, d5Square, e5Square, f5Square, g5Square, h5Square,
    a4Square, b4Square, c4Square, d4Square, e4Square, f4Square, g4Square, h4Square,
    a3Square, b3Square, c3Square, d3Square, e3Square, f3Square, g3Square, h3Square,
    a2Square, b2Square, c2Square, d2Square, e2Square, f2Square, g2Square, h2Square,
    a1Square, b1Square, c1Square, d1Square, e1Square, f1Square, g1Square, h1Square
)

@JvmField
val invalidSquare = Square(-1)

fun square(rank: Int, file: Int): Square {
    return if (rank in 0..7 && file in 0..7) {
        allSquares[rank * 8 + file]
    } else {
        invalidSquare
    }
}

//fun main() {
//    /**
//     * Generate
//     *   const val Squares
//     */
//    Squares.values().forEach {
//        println("const val ${it.name} = ${it.ordinal}")
//        println("@JvmField\nval ${it.name}Square = Square(${it.name})")
//    }
//
//    print("@JvmField\nval allSquares = arrayOf(")
//    print(Squares.values().joinToString { square -> square.name + "Square" })
//    print(")")
//}
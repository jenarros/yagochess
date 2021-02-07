package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.util.stream.Collectors
import java.util.stream.Stream

@JvmField
var blackRook: Piece = Rook(PieceColor.blackSet)

@JvmField
var blackKnight: Piece = Knight(PieceColor.blackSet)

@JvmField
var blackBishop: Piece = Bishop(PieceColor.blackSet)

@JvmField
var blackQueen: Piece = Queen(PieceColor.blackSet)

@JvmField
var blackKing: Piece = King(PieceColor.blackSet)

@JvmField
var blackPawn: Piece = Pawn(PieceColor.blackSet)

@JvmField
var whiteRook: Piece = Rook(PieceColor.whiteSet)

@JvmField
var whiteKnight: Piece = Knight(PieceColor.whiteSet)

@JvmField
var whiteBishop: Piece = Bishop(PieceColor.whiteSet)

@JvmField
var whiteQueen: Piece = Queen(PieceColor.whiteSet)

@JvmField
var whiteKing: Piece = King(PieceColor.whiteSet)

@JvmField
var whitePawn: Piece = Pawn(PieceColor.whiteSet)

@JvmField
var none: Piece = object : Piece(PieceType.none, PieceColor.none) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        throw RuntimeException("Should not happen")
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        throw RuntimeException("Should not happen")
    }
}

var all: Collection<Piece> = Stream.of(
    blackRook,
    blackKnight,
    blackBishop,
    blackQueen,
    blackKing,
    blackPawn,
    whiteRook,
    whiteKnight,
    whiteBishop,
    whiteQueen,
    whiteKing,
    whitePawn,
    none
).collect(Collectors.toSet())

object Pieces {
    fun parse(c: Char): Piece {
        return when (c) {
            'p' -> whitePawn
            'r' -> whiteRook
            'n' -> whiteKnight
            'b' -> whiteBishop
            'q' -> whiteQueen
            'k' -> whiteKing
            'P' -> blackPawn
            'R' -> blackRook
            'N' -> blackKnight
            'B' -> blackBishop
            'Q' -> blackQueen
            'K' -> blackKing
            else -> none
        }
    }
}
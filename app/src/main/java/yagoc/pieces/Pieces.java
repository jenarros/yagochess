package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.Square;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pieces {
    public static Piece blackRook = new Rook(PieceColor.blackSet);
    public static Piece blackKnight = new Knight(PieceColor.blackSet);
    public static Piece blackBishop = new Bishop(PieceColor.blackSet);
    public static Piece blackQueen = new Queen(PieceColor.blackSet);
    public static Piece blackKing = new King(PieceColor.blackSet);
    public static Piece blackPawn = new Pawn(PieceColor.blackSet);

    public static Piece whiteRook = new Rook(PieceColor.whiteSet);
    public static Piece whiteKnight = new Knight(PieceColor.whiteSet);
    public static Piece whiteBishop = new Bishop(PieceColor.whiteSet);
    public static Piece whiteQueen = new Queen(PieceColor.whiteSet);
    public static Piece whiteKing = new King(PieceColor.whiteSet);
    public static Piece whitePawn = new Pawn(PieceColor.whiteSet);

    public static Piece none = new Piece(null, null) {
        @Override
        public boolean isValidForPiece(Board board, Move move) {
            return false;
        }

        @Override
        public Stream<Move> generateMovesForPiece(Board board, Square from) {
            return Stream.empty();
        }
    };

    public static Collection<Piece> all = Stream.of(
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
            none)
            .collect(Collectors.toSet());

    public static Piece parse(char c) {
        switch (c) {
            case 'P':
                return whitePawn;
            case 'R':
                return whiteRook;
            case 'N':
                return whiteKnight;
            case 'B':
                return whiteBishop;
            case 'Q':
                return whiteQueen;
            case 'K':
                return whiteKing;
            default:
                return none;
        }
    }
}

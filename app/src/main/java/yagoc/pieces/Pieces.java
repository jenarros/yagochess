package yagoc.pieces;

import yagoc.SetType;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pieces {
    public static Piece blackRook = new Rook(SetType.blackSet);
    public static Piece blackKnight = new Knight(SetType.blackSet);
    public static Piece blackBishop = new Bishop(SetType.blackSet);
    public static Piece blackQueen = new Queen(SetType.blackSet);
    public static Piece blackKing = new King(SetType.blackSet);
    public static Piece blackPawn = new Pawn(SetType.blackSet);

    public static Piece whiteRook = new Rook(SetType.whiteSet);
    public static Piece whiteKnight = new Knight(SetType.whiteSet);
    public static Piece whiteBishop = new Bishop(SetType.whiteSet);
    public static Piece whiteQueen = new Queen(SetType.whiteSet);
    public static Piece whiteKing = new King(SetType.whiteSet);
    public static Piece whitePawn = new Pawn(SetType.whiteSet);

    public static Piece none = new Piece(null, null);

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
            .collect(Collectors.toList());
}

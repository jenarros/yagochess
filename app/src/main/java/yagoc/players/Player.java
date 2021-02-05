package yagoc.players;

import yagoc.board.Board;
import yagoc.board.Move;
import yagoc.pieces.PieceColor;

public interface Player {
    Move move(Board board);

    PlayerType type();

    PieceColor pieceColor();

    String name();

    default boolean isUser() {
        return type() == PlayerType.user;
    }

    default boolean isComputer() {
        return type() == PlayerType.computer;
    }
}
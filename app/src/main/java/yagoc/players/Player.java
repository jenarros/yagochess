package yagoc.players;

import yagoc.board.BoardReader;
import yagoc.board.Move;
import yagoc.pieces.PieceColor;

public interface Player {
    Move move(BoardReader board);

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
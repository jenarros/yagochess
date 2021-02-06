package yagoc.players;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.pieces.PieceColor;

public interface Player {
    Move move(BoardView board);

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
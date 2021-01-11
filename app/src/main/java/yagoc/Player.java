package yagoc;

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
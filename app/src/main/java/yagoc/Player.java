package yagoc;

interface Player {
    Move move(Board board);

    PlayerType type();

    SetType setType();

    String name();

    default boolean isUser() {
        return type() == PlayerType.user;
    }

    default boolean isComputer() {
        return type() == PlayerType.computer;
    }
}
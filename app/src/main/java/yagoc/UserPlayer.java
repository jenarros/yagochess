package yagoc;

import java.io.Serializable;

class UserPlayer implements Player, Serializable {
    private final String name;
    private final PieceColor set;

    UserPlayer(String name, PieceColor set) {
        this.name = name;
        this.set = set;
    }

    public PieceColor pieceColor() {
        return set;
    }

    @Override
    public Move move(Board board) {
        throw new RuntimeException(name + " cannot move without user input.");
    }

    @Override
    public PlayerType type() {
        return PlayerType.user;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return set + "\t" + name() + "\t" + type();
    }
}

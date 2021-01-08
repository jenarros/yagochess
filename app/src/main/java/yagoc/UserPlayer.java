package yagoc;

class UserPlayer implements Player {
    private final String name;
    private final SetType set;

    UserPlayer(String name, SetType set) {
        this.name = name;
        this.set = set;
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

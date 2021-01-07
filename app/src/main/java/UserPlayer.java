
public class UserPlayer implements Player {
    private final String name;
    private final int set;

    public UserPlayer(String name, int set) {
        this.name = name;
        this.set = set;
    }

    @Override
    public Move move(Board board) {
        throw new RuntimeException("Player " + name + " cannot move without user input.");
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
        return " \t" + set + "\t" + name() + "\t" + type();
    }
}

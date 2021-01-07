
public class UserPlayer implements Player {
    private final String name;
    private final int set;
    private final String type;

    public UserPlayer(String name, int set, String type, Logger logger) {
        this.name = name;
        this.set = set;
        this.type = type;
    }

    @Override
    public Move move(Board board) {
        throw new RuntimeException("??");
    }

    @Override
    public String type() {
        return type;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return " \t" + set + "\t" + name() + "\t" + type();
    }
}

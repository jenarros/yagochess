public interface Player {
    Move move(Board board);

    String type();

    String name();
}
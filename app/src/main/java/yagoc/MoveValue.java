package yagoc;

class MoveValue {
    final int value;
    final Move move;

    MoveValue(int value) {
        this(null, value);
    }

    MoveValue(Move move, int value) {
        this.move = move;
        this.value = value;
    }
}

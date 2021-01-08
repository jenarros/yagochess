
class Move {
    Piece piece;
    Square from;
    Square to;

    Move(Piece piece, Square from, Square to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    public String toString() {
        return "Piece " + piece + " from [ " + from.x + " , " + from.y + " ] " + "to [ " + to.x + " , " + to.y + " ] ";
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    int rankDistance() {
        return (to.x - from.x) * (piece.set == SetType.whiteSet ? -1 : 1);
    }

    int fileDistanceAbs() {
        return Math.abs((to.y - from.y) * (piece.set == SetType.whiteSet ? 1 : -1));
    }

    int rankDistanceAbs() {
        return Math.abs(rankDistance());
    }

    boolean hasSameFile() {
        return to.y == from.y;
    }

    public boolean hasSameRank() {
        return from.x == to.x;
    }
}

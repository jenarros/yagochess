package yagoc;

import static yagoc.BoardController.FILE_NAMES;
import static yagoc.BoardController.RANK_NAMES;

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
        return piece + " from " + RANK_NAMES[from.rank] + FILE_NAMES[from.file] + " to " + RANK_NAMES[to.rank] + FILE_NAMES[to.file] + " ";
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    int rankDistance() {
        return (to.rank - from.rank) * (piece.set == SetType.whiteSet ? -1 : 1);
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    int fileDistance() {
        return (to.file - from.file) * (piece.set == SetType.whiteSet ? 1 : -1);
    }

    int fileDistanceAbs() {
        return Math.abs(fileDistance());
    }

    int rankDistanceAbs() {
        return Math.abs(rankDistance());
    }

    boolean hasSameFile() {
        return to.file == from.file;
    }

    public boolean hasSameRank() {
        return from.rank == to.rank;
    }

    public boolean movesRight() {
        return fileDistance() > 0;
    }

    public boolean movesLeft() {
        return fileDistance() < 0;
    }
}

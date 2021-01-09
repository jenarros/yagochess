package yagoc;

import static yagoc.BoardController.fileNames;
import static yagoc.BoardController.rankNames;

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
        return piece + " from " + rankNames[from.rank] + fileNames[from.file] + " to " + rankNames[to.rank] + fileNames[to.file] + " ";
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    int rankDistance() {
        return (to.rank - from.rank) * (piece.set == SetType.whiteSet ? -1 : 1);
    }

    int fileDistanceAbs() {
        return Math.abs((to.file - from.file) * (piece.set == SetType.whiteSet ? 1 : -1));
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
}

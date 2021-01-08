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
        return piece + " from " + rankNames[from.x] + fileNames[from.y] + " to " + rankNames[to.x] + fileNames[to.y] + " ";
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

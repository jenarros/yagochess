package yagoc;

import static yagoc.BoardController.FILE_NAMES;
import static yagoc.BoardController.RANK_NAMES;
import static yagoc.PieceType.king;

class Move {
    final Piece fromPiece;
    final Square from;
    final Square to;

    Move(Piece fromPiece, Square from, Square to) {
        this.fromPiece = fromPiece;
        this.from = from;
        this.to = to;
        if (!this.from.exists() || !this.to.exists()) {
            throw new IllegalArgumentException("This move contains non-existing squares: " + from + " " + to);
        }
    }

    public String toString() {
        return fromPiece + " from " + RANK_NAMES[from.rank] + FILE_NAMES[from.file] + " to " + RANK_NAMES[to.rank] + FILE_NAMES[to.file] + " ";
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    int rankDistance() {
        return (to.rank - from.rank) * (fromPiece.set == SetType.whiteSet ? -1 : 1);
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    int fileDistance() {
        return (to.file - from.file) * (fromPiece.set == SetType.whiteSet ? 1 : -1);
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

    public boolean isCastling() {
        return fromPiece.type == king && fileDistanceAbs() == 2 && rankDistance() == 0;
    }

    public boolean isCastlingQueenside() {
        return isCastling() && this.to.file < this.from.file;
    }
}

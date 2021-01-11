package yagoc;

import yagoc.pieces.Piece;

import static yagoc.BoardController.FILE_NAMES;
import static yagoc.BoardController.RANK_NAMES;
import static yagoc.pieces.PieceType.King;
import static yagoc.pieces.Pieces.none;

public class Move {
    private final Piece fromPiece;
    private final Square from;
    private final Square to;

    public Move(Piece fromPiece, Square from, Square to) {
        this.fromPiece = fromPiece;
        this.from = from;
        this.to = to;

        if (fromPiece == none) {
            throw new IllegalArgumentException("Cannot move nothing");
        }

        if (!this.from.exists() || !this.to.exists()) {
            throw new IllegalArgumentException("This move contains non-existing squares: " + from + " " + to);
        }
    }

    public String toString() {
        return fromPiece + " " + RANK_NAMES[from.getRank()] + FILE_NAMES[from.getFile()] + " " + RANK_NAMES[to.getRank()] + FILE_NAMES[to.getFile()];
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    public int rankDistance() {
        return (to.getRank() - from.getRank()) * (fromPiece.setType() == SetType.whiteSet ? -1 : 1);
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    public int fileDistance() {
        return (to.getFile() - from.getFile()) * (fromPiece.setType() == SetType.whiteSet ? 1 : -1);
    }

    public int fileDistanceAbs() {
        return Math.abs(fileDistance());
    }

    public int rankDistanceAbs() {
        return Math.abs(rankDistance());
    }

    public boolean hasSameFile() {
        return to.getFile() == from.getFile();
    }

    public boolean hasSameRank() {
        return from.getRank() == to.getRank();
    }

    public boolean isCastling() {
        return fromPiece.pieceType() == King && fileDistanceAbs() == 2 && rankDistance() == 0;
    }

    public boolean isCastlingQueenside() {
        return isCastling() && this.to.getFile() < this.from.getFile();
    }

    public Piece fromPiece() {
        return fromPiece;
    }

    public Square from() {
        return from;
    }

    public Square to() {
        return to;
    }
}

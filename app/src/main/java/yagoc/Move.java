package yagoc;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;

import static yagoc.pieces.PieceType.King;
import static yagoc.pieces.Pieces.none;

public class Move {
    public static final String[] FILE_NAMES = {"a", "b", "c", "d", "e", "f", "g", "h"};
    public static final String[] RANK_NAMES = {"8", "7", "6", "5", "4", "3", "2", "1"};

    private final Piece fromPiece;
    private final Square from;
    private final Square to;

    public Move(Piece fromPiece, Square from, Square to) {
        this.fromPiece = fromPiece;
        this.from = from;
        this.to = to;

        if (fromPiece.equals(none)) {
            throw new IllegalArgumentException("Cannot move nothing");
        }

        if (!this.from.exists() || !this.to.exists()) {
            throw new IllegalArgumentException("This move contains non-existing squares: " + from + " " + to);
        }
    }

    public String toString() {
        return fromPiece + " " + RANK_NAMES[from.rank()] + FILE_NAMES[from.file()] + " " + RANK_NAMES[to.rank()] + FILE_NAMES[to.file()];
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    public int rankDistance() {
        return (to.rank() - from.rank()) * (fromPiece.color() == PieceColor.whiteSet ? -1 : 1);
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    public int fileDistance() {
        return (to.file() - from.file()) * (fromPiece.color() == PieceColor.whiteSet ? 1 : -1);
    }

    public Square enPassantSquare() {
        return this.to().previousRank(this.fromPiece().color());
    }

    public int fileDistanceAbs() {
        return Math.abs(fileDistance());
    }

    public int rankDistanceAbs() {
        return Math.abs(rankDistance());
    }

    public boolean hasSameFile() {
        return to.file() == from.file();
    }

    public boolean hasSameRank() {
        return from.rank() == to.rank();
    }

    public boolean isCastling() {
        return fromPiece.pieceType() == King && fileDistanceAbs() == 2 && rankDistance() == 0;
    }

    public boolean isCastlingQueenside() {
        return isCastling() && this.to.file() < this.from.file();
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

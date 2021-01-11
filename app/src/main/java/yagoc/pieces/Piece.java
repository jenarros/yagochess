package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

abstract public class Piece {
    private final PieceType pieceType;
    private final SetType setType;

    public Piece(PieceType pieceType, SetType setType) {
        this.pieceType = pieceType;
        this.setType = setType;
    }

    public PieceType pieceType() {
        return pieceType;
    }

    public SetType setType() {
        return setType;
    }

    public Piece switchTo(PieceType type) {
        return Pieces.all.stream()
                .filter((piece) -> type == piece.pieceType && piece.setType == this.setType)
                .findFirst().orElseThrow();
    }

    @Override
    public String toString() {
        if (pieceType == PieceType.Knight) {
            return "N";
        } else {
            return pieceType.name().substring(0, 1);
        }
    }

    public abstract boolean isCorrectMove(Board board, Move move);
}

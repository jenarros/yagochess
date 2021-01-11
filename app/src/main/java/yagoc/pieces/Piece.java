package yagoc.pieces;

import yagoc.SetType;

public class Piece {
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
}

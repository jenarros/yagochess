package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.Square;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

abstract public class Piece implements Serializable {
    private final PieceType pieceType;
    private final PieceColor color;

    public Piece(PieceType pieceType, PieceColor color) {
        this.pieceType = pieceType;
        this.color = color;
    }

    public PieceType pieceType() {
        return pieceType;
    }

    public PieceColor color() {
        return color;
    }

    public Piece switchTo(PieceType type) {
        return Pieces.all.stream()
                .filter((piece) -> type == piece.pieceType && piece.color == this.color)
                .findFirst().orElseThrow();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return pieceType == piece.pieceType &&
                color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, color);
    }

    @Override
    public String toString() {
        if (pieceType == null) {
            return "";
        } else if (pieceType == PieceType.Knight) {
            return "N";
        } else {
            return pieceType.name().substring(0, 1);
        }
    }

    /**
     * Can safely assume that general rules have been validated such as:
     * - the piece is not moving to a square with a piece of the same color
     */
    protected abstract boolean isValidForPiece(Board board, Move move);

    public final boolean isCorrectMove(Board board, Move move) {
        if (board.pieceAt(move.from()).color() == board.pieceAt(move.to()).color()) {
            return false;
        }
        return this.isValidForPiece(board, move);
    }

    /**
     * These are all potential moves pre-validation, the resulting moves need to be validated.
     */
    protected abstract Stream<Move> generateMovesForPiece(Board board, Square from);

    public final Stream<Move> generateMoves(Board board, Square from) {
        return generateMovesForPiece(board, from).filter((move -> isCorrectMove(board, move)));
    }
}

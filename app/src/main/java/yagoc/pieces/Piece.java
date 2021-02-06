package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import static yagoc.pieces.PieceColor.blackSet;
import static yagoc.pieces.PieceColor.whiteSet;
import static yagoc.pieces.Pieces.none;

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
                .filter((piece) -> type.equals(piece.pieceType) && piece.color == this.color)
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
    protected abstract boolean isValidForPiece(BoardView board, Move move);

    public final boolean isCorrectMove(BoardView board, Move move) {
        if (board.pieceAt(move.from()).color().equals(board.pieceAt(move.to()).color())) {
            return false;
        }
        return this.isValidForPiece(board, move);
    }

    /**
     * These are all potential moves pre-validation, the resulting moves need to be validated.
     */
    protected abstract Stream<Move> generateMovesForPiece(BoardView board, Square from);

    public final Stream<Move> generateMoves(BoardView board, Square from) {
        return generateMovesForPiece(board, from).filter((move -> isCorrectMove(board, move)));
    }

    public boolean notOfSameColor(PieceColor pieceColor) {
        return this != none && this.color != pieceColor;
    }

    public char toUniqueChar() {
        if (blackSet.equals(color())) {
            return toString().charAt(0);
        } else if (whiteSet.equals(color())) {
            return toString().toLowerCase().charAt(0);
        }
        return '-';
    }
}

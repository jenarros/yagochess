package yagoc.pieces;

import yagoc.board.Board;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

import static yagoc.pieces.Bishop.generateMovesForBishop;
import static yagoc.pieces.Bishop.isCorrectMoveForBishop;
import static yagoc.pieces.Rook.generateMovesForRook;
import static yagoc.pieces.Rook.isCorrectMoveForRook;

public class Queen extends Piece {
    public Queen(PieceColor pieceColor) {
        super(PieceType.Queen, pieceColor);
    }

    @Override
    public boolean isValidForPiece(Board board, Move move) {
        return (isCorrectMoveForBishop(board, move) || isCorrectMoveForRook(board, move));
    }

    @Override
    public Stream<Move> generateMovesForPiece(Board board, Square from) {
        return Stream.concat(generateMovesForBishop(board, from), generateMovesForRook(board, from));
    }
}

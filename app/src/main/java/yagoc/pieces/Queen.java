package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

import static yagoc.pieces.Bishop.isCorrectMoveForBishop;
import static yagoc.pieces.Rook.isCorrectMoveForRook;

public class Queen extends Piece {
    public Queen(SetType setType) {
        super(PieceType.Queen, setType);
    }

    @Override
    public boolean isCorrectMove(Board board, Move move) {
        return (isCorrectMoveForBishop(board, move) || isCorrectMoveForRook(board, move));
    }
}

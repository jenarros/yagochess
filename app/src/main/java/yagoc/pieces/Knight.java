package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

public class Knight extends Piece {
    public Knight(SetType setType) {
        super(PieceType.Knight, setType);
    }

    @Override
    public boolean isCorrectMove(Board board, Move move) {
        if (move.rankDistanceAbs() == 2 && move.fileDistanceAbs() == 1) {
            return true;
        } else {
            return move.fileDistanceAbs() == 2 && move.rankDistanceAbs() == 1;
        }
    }
}

package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

public class Rook extends Piece {
    public Rook(SetType setType) {
        super(PieceType.Rook, setType);
    }

    public static boolean isCorrectMoveForRook(Board board, Move move) {
        if (move.hasSameRank()) {
            //movimiento horizontal
            int mi = Math.min(move.from().getFile(), move.to().getFile()) + 1;
            int ma = Math.max(move.from().getFile(), move.to().getFile());
            for (; mi < ma; mi++) {
                if (board.pieceAt(move.from().getRank(), mi) != Pieces.none)
                    return false;
            }
            return true;
        } else if (move.hasSameFile()) {
            //movimiento vertical
            int mi = Math.min(move.from().getRank(), move.to().getRank()) + 1;
            int ma = Math.max(move.from().getRank(), move.to().getRank());
            for (; mi < ma; mi++) {
                if (board.pieceAt(mi, move.from().getFile()) != Pieces.none)
                    return false;
            }
            return true;
        } else
            return false;
    }

    @Override
    public boolean isCorrectMove(Board board, Move move) {
        return isCorrectMoveForRook(board, move);
    }
}

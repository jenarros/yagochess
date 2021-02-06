package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

public class Rook extends Piece {
    public Rook(PieceColor pieceColor) {
        super(PieceType.Rook, pieceColor);
    }

    static boolean isCorrectMoveForRook(BoardView board, Move move) {
        if (move.hasSameRank()) {
            //movimiento horizontal
            int mi = Math.min(move.from().file(), move.to().file()) + 1;
            int ma = Math.max(move.from().file(), move.to().file());
            for (; mi < ma; mi++) {
                if (!board.pieceAt(move.from().rank(), mi).equals(Pieces.none))
                    return false;
            }
            return true;
        } else if (move.hasSameFile()) {
            //movimiento vertical
            int mi = Math.min(move.from().rank(), move.to().rank()) + 1;
            int ma = Math.max(move.from().rank(), move.to().rank());
            for (; mi < ma; mi++) {
                if (!board.pieceAt(mi, move.from().file()).equals(Pieces.none))
                    return false;
            }
            return true;
        } else
            return false;
    }

    static Stream<Move> generateMovesForRook(BoardView board, Square from) {
        Piece piece = board.pieceAt(from);
        return from.straightSquares().stream()
                .map((to) -> new Move(piece, from, to));
    }

    @Override
    public boolean isValidForPiece(BoardView board, Move move) {
        return isCorrectMoveForRook(board, move);
    }

    @Override
    public Stream<Move> generateMovesForPiece(BoardView board, Square from) {
        return generateMovesForRook(board, from);
    }
}

package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

public class Pawn extends Piece {
    public Pawn(SetType setType) {
        super(PieceType.Pawn, setType);
    }

    @Override
    public boolean isCorrectMove(Board board, Move move) {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false;
        }

        // straight ahead
        if (move.hasSameFile() && board.pieceAt(move.to()) == Pieces.none) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if (move.rankDistance() == 2 && board.pieceAt(move.to().previousRank(move.fromPiece().setType())) == Pieces.none &&
                    ((move.from().getRank() == 6 && board.currentPlayer() == board.whitePlayer()) || (move.from().getRank() == 1 && board.currentPlayer() == board.blackPlayer())))
                return true;

            if (move.rankDistance() == 1)
                return true;
        }

        // diagonal
        if (move.fileDistanceAbs() == 1 && move.rankDistance() == 1) {
            // capture
            if (board.isPieceOfOppositePlayer(board.pieceAt(move.to()))) {
                return true;
            }

            // en passant
            return board.pieceAt(move.to()) == Pieces.none
                    && board.enPassant(move.to().getFile()) == board.moveCounter() - 1
                    && move.from().getRank() == ((board.currentPlayer() == board.whitePlayer()) ? 3 : 4);
        }
        return false;
    }
}

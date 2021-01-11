package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;
import yagoc.Square;

public class King extends Piece {
    public King(SetType setType) {
        super(PieceType.King, setType);
    }

    public boolean isCorrectMove(Board board, Move move) {
        return (move.fileDistanceAbs() <= 1 && move.rankDistanceAbs() <= 1) || isCorrectCastling(board, move);
    }

    boolean isCorrectCastling(Board board, Move move) {
        if (((move.from().getRank() == 7 && move.fromPiece() == Pieces.whiteKing && !board.hasWhiteKingMoved()) ||
                (move.from().getRank() == 0 && move.fromPiece() == Pieces.blackKing && !board.hasBlackKingMoved())) &&
                move.hasSameRank() &&
                board.moveDoesNotCreateCheck(move)) {

            if (move.to().getFile() == 2 && board.pieceAt(move.from().getRank(), 0) == move.fromPiece().switchTo(PieceType.Rook)) {
                //blancas
                if (move.fromPiece().setType() == SetType.whiteSet && !board.hasWhiteLeftRookMoved() &&
                        board.pieceAt(7, 1) == Pieces.none && board.pieceAt(7, 2) == Pieces.none &&
                        board.pieceAt(7, 3) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 3)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 2))) {
                    return true;
                }
                //negras
                return move.fromPiece().setType() == SetType.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.pieceAt(0, 1) == Pieces.none && board.pieceAt(0, 2) == Pieces.none &&
                        board.pieceAt(0, 3) == Pieces.none && board.pieceAt(0, 4) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 3)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 2));
            } else if (move.to().getFile() == 6 && board.pieceAt(move.from().getRank(), 7) == move.fromPiece().switchTo(PieceType.Rook)) { //torre derecha
                //blancas
                if (move.fromPiece().setType() == SetType.whiteSet && !board.hasWhiteRightRookMoved() &&
                        board.pieceAt(7, 5) == Pieces.none && board.pieceAt(7, 6) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 6)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 5))) {
                    return true;
                }
                //negras
                return move.fromPiece().setType() == SetType.blackSet && !board.hasBlackRightRookMoved() &&
                        board.pieceAt(0, 5) == Pieces.none && board.pieceAt(0, 6) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 6)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 5));
            }
        }
        return false;
    }
}

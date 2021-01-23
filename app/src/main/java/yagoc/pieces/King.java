package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.Square;

import java.util.stream.Stream;

public class King extends Piece {
    public King(PieceColor pieceColor) {
        super(PieceType.King, pieceColor);
    }

    public boolean isValidForPiece(Board board, Move move) {
        return (move.fileDistanceAbs() <= 1 && move.rankDistanceAbs() <= 1) || isCorrectCastling(board, move);
    }

    @Override
    public Stream<Move> generateMovesForPiece(Board board, Square from) {
        Piece piece = board.pieceAt(from);
        return Stream.of(
                from.nextRank(piece.color()),
                from.nextRank(piece.color()).previousFile(piece.color()),
                from.nextRank(piece.color()).nextFile(piece.color()),
                from.previousRank(piece.color()),
                from.previousRank(piece.color()).nextFile(piece.color()),
                from.previousRank(piece.color()).previousFile(piece.color()),
                from.nextFile(piece.color()),
                from.previousFile(piece.color())
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to));
    }

    private boolean isCorrectCastling(Board board, Move move) {
        if (((move.from().rank() == 7 && move.fromPiece() == Pieces.whiteKing && !board.hasWhiteKingMoved()) ||
                (move.from().rank() == 0 && move.fromPiece() == Pieces.blackKing && !board.hasBlackKingMoved())) &&
                move.hasSameRank() && !board.isInCheck() &&
                board.moveDoesNotCreateCheck(move)) {

            if (move.to().file() == 2 && board.pieceAt(move.from().rank(), 0) == move.fromPiece().switchTo(PieceType.Rook)) {
                //blancas
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteLeftRookMoved() &&
                        board.pieceAt(7, 1) == Pieces.none && board.pieceAt(7, 2) == Pieces.none &&
                        board.pieceAt(7, 3) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 3)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 2))) {
                    return true;
                }
                //negras
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.pieceAt(0, 1) == Pieces.none && board.pieceAt(0, 2) == Pieces.none &&
                        board.pieceAt(0, 3) == Pieces.none && board.pieceAt(0, 4) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 3)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 2));
            } else if (move.to().file() == 6 && board.pieceAt(move.from().rank(), 7) == move.fromPiece().switchTo(PieceType.Rook)) { //torre derecha
                //blancas
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteRightRookMoved() &&
                        board.pieceAt(7, 5) == Pieces.none && board.pieceAt(7, 6) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 6)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(7, 5))) {
                    return true;
                }
                //negras
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackRightRookMoved() &&
                        board.pieceAt(0, 5) == Pieces.none && board.pieceAt(0, 6) == Pieces.none &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 6)) &&
                        board.moveDoesNotCreateCheck(move.from(), new Square(0, 5));
            }
        }
        return false;
    }
}

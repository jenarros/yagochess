package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

import static yagoc.board.BoardRules.isInCheck;
import static yagoc.board.BoardRules.moveDoesNotCreateCheck;
import static yagoc.board.SquaresKt.*;

public class King extends Piece {
    public King(PieceColor pieceColor) {
        super(PieceType.King, pieceColor);
    }

    public boolean isValidForPiece(BoardView board, Move move) {
        return (move.fileDistanceAbs() <= 1 && move.rankDistanceAbs() <= 1) || isCorrectCastling(board, move);
    }

    @Override
    public Stream<Move> generateMovesForPiece(BoardView board, Square from) {
        Piece piece = board.pieceAt(from);
        return Stream.of(
                from.next2File(piece.color()),
                from.previous2File(piece.color()),
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

    private boolean isCorrectCastling(BoardView board, Move move) {
        if (((move.from().rank() == 7 && move.fromPiece().equals(Pieces.whiteKing) && !board.hasWhiteKingMoved()) ||
                (move.from().rank() == 0 && move.fromPiece().equals(Pieces.blackKing) && !board.hasBlackKingMoved())) &&
                move.hasSameRank() && !isInCheck(board, move.fromPiece().color())) {

            if (move.to().file() == 2 && board.pieceAt(new Square(move.from().rank(), 0)).equals(move.fromPiece().switchTo(PieceType.Rook))) { // queenside
                // white set
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteLeftRookMoved() &&
                        board.noneAt(b1Square) && board.noneAt(c1Square) &&
                        board.noneAt(d1Square) &&
                        moveDoesNotCreateCheck(board, move.from(), d1Square) &&
                        moveDoesNotCreateCheck(board, move.from(), c1Square)) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.noneAt(b8Square) && board.noneAt(c8Square) &&
                        board.noneAt(d8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), d8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), c8Square);
            } else if (move.to().file() == 6 && board.pieceAt(new Square(move.from().rank(), 7)).equals(move.fromPiece().switchTo(PieceType.Rook))) { // kingside
                // white set
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteRightRookMoved() &&
                        board.noneAt(f1Square) && board.noneAt(g1Square) &&
                        moveDoesNotCreateCheck(board, move.from(), g1Square) &&
                        moveDoesNotCreateCheck(board, move.from(), f1Square)) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackRightRookMoved() &&
                        board.noneAt(f8Square) && board.noneAt(g8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), g8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), f8Square);
            }
        }
        return false;
    }
}

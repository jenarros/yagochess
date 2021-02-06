package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;
import yagoc.board.Squares;

import java.util.stream.Stream;

import static yagoc.board.BoardRules.isInCheck;
import static yagoc.board.BoardRules.moveDoesNotCreateCheck;
import static yagoc.board.Squares.b8;
import static yagoc.board.Squares.c8;
import static yagoc.board.Squares.d8;
import static yagoc.board.Squares.f1;
import static yagoc.board.Squares.f8;
import static yagoc.board.Squares.g1;

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
                        board.noneAt(Squares.b1) && board.noneAt(Squares.c1) &&
                        board.noneAt(Squares.d1) &&
                        moveDoesNotCreateCheck(board, move.from(), Squares.d1.legacySquare()) &&
                        moveDoesNotCreateCheck(board, move.from(), Squares.c1.legacySquare())) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.noneAt(b8) && board.noneAt(c8) &&
                        board.noneAt(Squares.d8) &&
                        moveDoesNotCreateCheck(board, move.from(), d8.legacySquare()) &&
                        moveDoesNotCreateCheck(board, move.from(), c8.legacySquare());
            } else if (move.to().file() == 6 && board.pieceAt(new Square(move.from().rank(), 7)).equals(move.fromPiece().switchTo(PieceType.Rook))) { // kingside
                // white set
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteRightRookMoved() &&
                        board.noneAt(Squares.f1) && board.noneAt(g1) &&
                        moveDoesNotCreateCheck(board, move.from(), g1.legacySquare()) &&
                        moveDoesNotCreateCheck(board, move.from(), f1.legacySquare())) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackRightRookMoved() &&
                        board.noneAt(f8) && board.noneAt(Squares.g8) &&
                        moveDoesNotCreateCheck(board, move.from(), Squares.g8.legacySquare()) &&
                        moveDoesNotCreateCheck(board, move.from(), f8.legacySquare());
            }
        }
        return false;
    }
}

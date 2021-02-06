package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

import static yagoc.board.BoardRules.isInCheck;
import static yagoc.board.BoardRules.moveDoesNotCreateCheck;

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

            if (move.to().file() == 2 && board.pieceAt(move.from().rank(), 0).equals(move.fromPiece().switchTo(PieceType.Rook))) { // queenside
                // white set
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteLeftRookMoved() &&
                        board.pieceAt(7, 1).equals(Pieces.none) && board.pieceAt(7, 2).equals(Pieces.none) &&
                        board.pieceAt(7, 3).equals(Pieces.none) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(7, 3)) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(7, 2))) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.pieceAt(0, 1).equals(Pieces.none) && board.pieceAt(0, 2).equals(Pieces.none) &&
                        board.pieceAt(0, 3).equals(Pieces.none) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(0, 3)) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(0, 2));
            } else if (move.to().file() == 6 && board.pieceAt(move.from().rank(), 7).equals(move.fromPiece().switchTo(PieceType.Rook))) { // kingside
                // white set
                if (move.fromPiece().color() == PieceColor.whiteSet && !board.hasWhiteRightRookMoved() &&
                        board.pieceAt(7, 5).equals(Pieces.none) && board.pieceAt(7, 6).equals(Pieces.none) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(7, 6)) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(7, 5))) {
                    return true;
                }
                // black set
                return move.fromPiece().color() == PieceColor.blackSet && !board.hasBlackRightRookMoved() &&
                        board.pieceAt(0, 5).equals(Pieces.none) && board.pieceAt(0, 6).equals(Pieces.none) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(0, 6)) &&
                        moveDoesNotCreateCheck(board, move.from(), new Square(0, 5));
            }
        }
        return false;
    }
}

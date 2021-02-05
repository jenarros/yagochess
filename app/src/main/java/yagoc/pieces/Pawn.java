package yagoc.pieces;

import yagoc.board.Board;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

import static yagoc.pieces.PieceColor.blackSet;
import static yagoc.pieces.PieceColor.whiteSet;

public class Pawn extends Piece {
    public Pawn(PieceColor pieceColor) {
        super(PieceType.Pawn, pieceColor);
    }

    @Override
    public boolean isValidForPiece(Board board, Move move) {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false;
        }

        // straight ahead
        if (move.hasSameFile() && board.pieceAt(move.to()).equals(Pieces.none)) {
            // if we move two squares, we should start from the initial position and next rank should be empty
            if (move.rankDistance() == 2 && board.pieceAt(move.to().previousRank(move.fromPiece().color())).equals(Pieces.none) &&
                    (move.from().rank() == 6 && move.fromPiece().color().equals(whiteSet) || (move.from().rank() == 1 && move.fromPiece().color().equals(blackSet))))
                return true;

            if (move.rankDistance() == 1)
                return true;
        }

        // diagonal
        if (move.fileDistanceAbs() == 1 && move.rankDistance() == 1) {
            // capture
            if (board.pieceAt(move.to()).notOfSameColor(move.fromPiece().color())) {
                return true;
            }

            // en passant
            return board.pieceAt(move.to()).equals(Pieces.none)
                    && board.enPassant(move.to().file()) == board.moveCounter() - 1
                    && move.from().rank() == ((board.currentPlayer().equals(board.whitePlayer())) ? 3 : 4);
        }
        return false;
    }

    @Override
    public Stream<Move> generateMovesForPiece(Board board, Square from) {
        Piece piece = board.pieceAt(from);

        return Stream.of(
                from.nextRankPreviousFile(piece.color()), // left
                from.nextRank(piece.color()),             // ahead
                from.next2Rank(piece.color()),            // ahead 2
                from.nextRankNextFile(piece.color())      // right
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to));
    }
}

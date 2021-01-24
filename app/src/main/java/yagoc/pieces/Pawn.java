package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.Square;

import java.util.stream.Stream;

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
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if (move.rankDistance() == 2 && board.pieceAt(move.to().previousRank(move.fromPiece().color())).equals(Pieces.none) &&
                    ((move.from().rank() == 6 && board.currentPlayer().equals(board.whitePlayer())) || (move.from().rank() == 1 && board.currentPlayer().equals(board.blackPlayer()))))
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
                from.nextRankPreviousFile(board.currentPlayer().pieceColor()), // left
                from.nextRank(board.currentPlayer().pieceColor()),             // ahead
                from.next2Rank(board.currentPlayer().pieceColor()),            // ahead 2
                from.nextRankNextFile(board.currentPlayer().pieceColor())      // right
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to));
    }
}

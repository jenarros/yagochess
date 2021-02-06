package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

public class Knight extends Piece {
    public Knight(PieceColor pieceColor) {
        super(PieceType.Knight, pieceColor);
    }

    @Override
    public boolean isValidForPiece(BoardView board, Move move) {
        if (move.rankDistanceAbs() == 2 && move.fileDistanceAbs() == 1) {
            return true;
        } else {
            return move.fileDistanceAbs() == 2 && move.rankDistanceAbs() == 1;
        }
    }

    @Override
    public Stream<Move> generateMovesForPiece(BoardView board, Square from) {
        Piece piece = board.pieceAt(from);
        return Stream.of(
                from.next2Rank(piece.color()).nextFile(piece.color()),
                from.next2Rank(piece.color()).previousFile(piece.color()),
                from.previous2Rank(piece.color()).nextFile(piece.color()),
                from.previous2Rank(piece.color()).previousFile(piece.color()),
                from.next2File(piece.color()).nextRank(piece.color()),
                from.next2File(piece.color()).previousRank(piece.color()),
                from.previous2File(piece.color()).nextRank(piece.color()),
                from.previous2File(piece.color()).previousRank(piece.color())
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to));
    }
}

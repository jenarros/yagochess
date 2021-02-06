package yagoc.pieces;

import yagoc.board.BoardView;
import yagoc.board.Move;
import yagoc.board.Square;

import java.util.stream.Stream;

public class Bishop extends Piece {
    public Bishop(PieceColor pieceColor) {
        super(PieceType.Bishop, pieceColor);
    }

    static Stream<Move> generateMovesForBishop(BoardView board, Square from) {
        Piece piece = board.pieceAt(from);

        return from.diagonalSquares().stream().map((to) -> new Move(piece, from, to));
    }

    static boolean isCorrectMoveForBishop(BoardView board, Move move) {
        if (move.rankDistanceAbs() == move.fileDistanceAbs()) {
            //vamos a recorrer el movimiento de izquierda a derecha
            int ma = Math.max(move.from().file(), move.to().file()); //y final
            int rank, file, direction;

            //calculamos la casilla de inicio
            if (move.from().file() < move.to().file()) {
                rank = move.from().rank();
                file = move.from().file();
            } else {
                rank = move.to().rank();
                file = move.to().file();
            }

            // calculamos el desplazamiento
            if (rank == Math.min(move.from().rank(), move.to().rank())) {
                // down
                direction = 1;
            } else {
                // up
                direction = -1;
            }

            // go through the squares
            for (file++, rank += direction; file < ma; ) {
                if (board.someAt(new Square(rank, file)))
                    return false;
                rank += direction;
                file++;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValidForPiece(BoardView board, Move move) {
        return isCorrectMoveForBishop(board, move);
    }

    @Override
    public Stream<Move> generateMovesForPiece(BoardView board, Square from) {
        return generateMovesForBishop(board, from);
    }
}

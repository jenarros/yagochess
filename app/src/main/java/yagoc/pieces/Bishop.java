package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.Square;

import java.util.stream.Stream;

public class Bishop extends Piece {
    public Bishop(PieceColor pieceColor) {
        super(PieceType.Bishop, pieceColor);
    }

    static Stream<Move> generateMovesForBishop(Board board, Square from) {
        Piece piece = board.pieceAt(from);

        return from.diagonalSquares()
                .stream().map((to) -> new Move(piece, from, to));
    }

    static boolean isCorrectMoveForBishop(Board board, Move move) {
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

            //calculamos el desplazamiento
            if (rank == Math.min(move.from().rank(), move.to().rank()))                 //hacia abajo
                direction = 1;
            else //hacia arriba
                direction = -1;

            //recorremos el movimiento
            for (file++, rank += direction; file < ma; ) {
                if (board.pieceAt(rank, file) != Pieces.none)
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
    public boolean isValidForPiece(Board board, Move move) {
        return isCorrectMoveForBishop(board, move);
    }

    @Override
    public Stream<Move> generateMovesForPiece(Board board, Square from) {
        return generateMovesForBishop(board, from);
    }
}

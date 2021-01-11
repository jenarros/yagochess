package yagoc.pieces;

import yagoc.Board;
import yagoc.Move;
import yagoc.SetType;

public class Bishop extends Piece {
    public Bishop(SetType setType) {
        super(PieceType.Bishop, setType);
    }

    public static boolean isCorrectMoveForBishop(Board board, Move move) {
        if (move.rankDistanceAbs() == move.fileDistanceAbs()) {
            //vamos a recorrer el movimiento de izquierda a derecha
            int ma = Math.max(move.from().getFile(), move.to().getFile()); //y final
            int rank, file, direction;

            //calculamos la casilla de inicio
            if (move.from().getFile() < move.to().getFile()) {
                rank = move.from().getRank();
                file = move.from().getFile();
            } else {
                rank = move.to().getRank();
                file = move.to().getFile();
            }

            //calculamos el desplazamiento
            if (rank == Math.min(move.from().getRank(), move.to().getRank()))                 //hacia abajo
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
    public boolean isCorrectMove(Board board, Move move) {
        return isCorrectMoveForBishop(board, move);
    }
}

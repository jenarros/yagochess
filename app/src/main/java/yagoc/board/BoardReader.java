package yagoc.board;

import yagoc.pieces.Piece;
import yagoc.players.Player;

import java.util.concurrent.Callable;

public interface BoardReader {
    Piece pieceAt(Square square);

    Piece pieceAt(int rank, int file);

    boolean noneAt(Square square);

    int enPassant(int file);

    Player currentPlayer();

    boolean hasWhiteLeftRookMoved();

    boolean hasWhiteRightRookMoved();

    boolean hasWhiteKingMoved();

    boolean hasBlackLeftRookMoved();

    boolean hasBlackRightRookMoved();

    boolean hasBlackKingMoved();

    int drawCounter();

    int moveCounter();

    <T> T playAndUndo(Move move, Callable<T> callable);
}

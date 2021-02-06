package yagoc.board;

import yagoc.pieces.Piece;
import yagoc.players.Player;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface BoardView extends Serializable {
    Piece pieceAt(Square square);

    Piece pieceAt(int rank, int file);

    boolean noneAt(Square square);

    int enPassant(int file);

    Player currentPlayer();

    Player oppositePlayer();

    boolean isPieceOfCurrentPlayer(Piece piece);

    boolean hasWhiteLeftRookMoved();

    boolean hasWhiteRightRookMoved();

    boolean hasWhiteKingMoved();

    boolean hasBlackLeftRookMoved();

    boolean hasBlackRightRookMoved();

    boolean hasBlackKingMoved();

    int drawCounter();

    int moveCounter();

    <T> T playAndUndo(Move move, Callable<T> callable);

    BoardView playAndUndo(Square from, Square to);

    String toPrettyString();
}

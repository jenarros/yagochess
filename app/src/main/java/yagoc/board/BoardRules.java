package yagoc.board;

import yagoc.pieces.PieceColor;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yagoc.Yagoc.logger;
import static yagoc.pieces.PieceType.King;
import static yagoc.pieces.Pieces.none;

public class BoardRules {
    public static boolean isCorrectMove(BoardReader board, Square from, Square to) {
        return isCorrectMove(board, new Move(board.pieceAt(from), from, to));
    }

    public static boolean isCorrectMove(BoardReader board, Move move) {
        return move.fromPiece().isCorrectMove(board, move);
    }

    static boolean cannotMoveWithoutBeingCheck(BoardReader board) {
        return generateMoves(board).stream().noneMatch((move) -> moveDoesNotCreateCheck(board, move));
    }

    public static boolean isInCheck(BoardReader board, PieceColor color) {
        Square kingSquare = Square.allSquares.stream()
                .filter((square) -> board.pieceAt(square).pieceType() == King && board.pieceAt(square).color() == color)
                .findAny().orElseThrow(() -> new RuntimeException("Could not find " + color + " king!"));

        return Square.allSquares.stream()
                .anyMatch((from) -> !board.pieceAt(from).equals(none) && !board.pieceAt(from).color().equals(color) && isCorrectMove(board, from, kingSquare));
    }

    public static boolean isCheckmate(BoardReader board) {
        if (isInCheck(board, board.currentPlayer().pieceColor())) {
            return cannotMoveWithoutBeingCheck(board);
        } else {
            return false;
        }
    }

    public static boolean isADraw(BoardReader board) {
        if (!isInCheck(board, board.currentPlayer().pieceColor()) && cannotMoveWithoutBeingCheck(board)) {
            return true;
        }

        return board.drawCounter() == 50;
    }

    public static boolean moveDoesNotCreateCheck(BoardReader board, Move move) {
        return !board.playAndUndo(move, () -> isInCheck(board, move.fromPiece().color()));
    }

    public static boolean moveDoesNotCreateCheck(BoardReader board, Square from, Square to) {
        return moveDoesNotCreateCheck(board, new Move(board.pieceAt(from), from, to));
    }

    public static boolean noMoreMovesAllowed(BoardReader board) {
        if (isCheckmate(board)) {
            logger.info("checkmate winner is " + board.oppositePlayer().name());
            return true;
        } else if (isADraw(board)) {
            logger.info("draw");
            return true;
        } else
            return false;
    }

    public static Stream<Move> generateMoves(BoardReader board, Square from) {
        return board.pieceAt(from).generateMoves(board, from);
    }

    public static Stream<Move> generateMoves(BoardReader board, Square from, Predicate<Move> predicate) {
        return generateMoves(board, from).filter(predicate);
    }

    public static Collection<Move> generateMoves(BoardReader board) {
        return Square.allSquares.stream().flatMap((from) -> {
            if (board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
                return generateMoves(board, from, (move) -> moveDoesNotCreateCheck(board, move));
            }
            return Stream.empty();
        }).collect(Collectors.toList());
    }
}

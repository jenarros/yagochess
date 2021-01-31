package yagoc;

import yagoc.pieces.PieceColor;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yagoc.Yagoc.logger;
import static yagoc.pieces.PieceType.King;
import static yagoc.pieces.Pieces.none;

public class BoardRules {
    static boolean isCorrectMove(Board board, Square from, Square to) {
        return isCorrectMove(board, new Move(board.pieceAt(from), from, to));
    }

    static boolean isCorrectMove(Board board, Move move) {
        return move.fromPiece().isCorrectMove(board, move);
    }

    static boolean cannotMoveWithoutBeingCheck(Board board) {
        return generateMoves(board).stream().noneMatch((move) -> moveDoesNotCreateCheck(board, move));
    }

    public static <T> T playAndUndo(Board board, Move move, Callable<T> callable) {
        MoveLog moveLog = board.play(move);
        try {
            T moveValue = callable.call();
            board.undo(moveLog);
            return moveValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isInCheck(Board board, PieceColor color) {
        Square kingSquare = Square.allSquares.stream()
                .filter((square) -> board.pieceAt(square).pieceType() == King && board.pieceAt(square).color() == color)
                .findAny().orElseThrow(() -> new RuntimeException("Could not find " + color + " king!"));

        return Square.allSquares.stream()
                .anyMatch((from) -> !board.pieceAt(from).equals(none) && !board.pieceAt(from).color().equals(color) && isCorrectMove(board, from, kingSquare));
    }

    public static boolean isCheckmate(Board board) {
        if (isInCheck(board, board.currentPlayer.pieceColor())) {
            return cannotMoveWithoutBeingCheck(board);
        } else {
            return false;
        }
    }

    public static boolean isADraw(Board board) {
        if (!isInCheck(board, board.currentPlayer.pieceColor()) && cannotMoveWithoutBeingCheck(board)) {
            return true;
        }

        return board.drawCounter == 50;
    }

    public static boolean moveDoesNotCreateCheck(Board board, Move move) {
        return !playAndUndo(board, move, () -> isInCheck(board, move.fromPiece().color()));
    }

    public static boolean moveDoesNotCreateCheck(Board board, Square from, Square to) {
        return moveDoesNotCreateCheck(board, new Move(board.pieceAt(from), from, to));
    }

    public static boolean noMoreMovesAllowed(Board board) {
        if (isCheckmate(board)) {
            logger.info("checkmate winner is " + board.oppositePlayer().name());
            return true;
        } else if (isADraw(board)) {
            logger.info("draw");
            return true;
        } else
            return false;
    }

    public static Stream<Move> generateMoves(Board board, Square from) {
        return board.pieceAt(from).generateMoves(board, from);
    }

    public static Stream<Move> generateMoves(Board board, Square from, Predicate<Move> predicate) {
        return generateMoves(board, from).filter(predicate);
    }

    public static Collection<Move> generateMoves(Board board) {
        return Square.allSquares.stream().flatMap((from) -> {
            if (board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
                return generateMoves(board, from, (move) -> moveDoesNotCreateCheck(board, move));
            }
            return Stream.empty();
        }).collect(Collectors.toList());
    }
}

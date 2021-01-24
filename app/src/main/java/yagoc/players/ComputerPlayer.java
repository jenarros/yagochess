package yagoc.players;

import yagoc.Board;
import yagoc.Move;
import yagoc.MoveLog;
import yagoc.pieces.PieceColor;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;

import static yagoc.Yagoc.logger;

public class ComputerPlayer implements Player, Serializable {
    final protected PieceColor pieceColor;
    final private String name;
    final private int level;
    int processedMoves;
    private final PlayerStrategy strategy;

    public ComputerPlayer(String name, PieceColor pieceColor, int level, PlayerStrategy strategy) {
        this.name = name;
        this.pieceColor = pieceColor;
        this.level = level;
        this.strategy = strategy;
    }

    public PieceColor pieceColor() {
        return pieceColor;
    }

    @Override
    public PlayerType type() {
        return PlayerType.computer;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return pieceColor + "\t" + type() + "\t" + level;
    }

    public Move move(Board board) {
        processedMoves = 0;
        long start = System.currentTimeMillis();
        MoveValue moveValue = alphaBeta(level, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
        long elapsed = System.currentTimeMillis() - start + 1;
        logger.info("alpha-beta: processed = " + processedMoves + " moves in " + elapsed + "ms " + processedMoves / elapsed + " moves/ms,  minimax = " + moveValue.value);

        return moveValue.move;
    }

    MoveValue alphaBeta(int depth, Board board, int alfa, int beta) {
        if (depth == 0) {
            return leafMoveValue(board);
        } else if ((level - depth) % 2 == 0) { // maximizing player = current player (as depth = level)
            return alphaBetaMax(depth, board, alfa, beta);
        } else {
            return alphaBetaMin(depth, board, alfa, beta);
        }
    }

    public static MoveValue playAndUndo(Board board, Move move, Callable<MoveValue> callable) {
        MoveLog moveLog = board.play(move);
        try {
            MoveValue moveValue = callable.call();
            board.undo(moveLog);
            return moveValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MoveValue alphaBetaMin(int depth, Board board, int alpha, int beta) {
        Collection<Move> moves = board.generateMoves();

        // checkmate
        if (moves.isEmpty()) {
            return new MoveValue(Integer.MAX_VALUE - (level - depth + 1));
        }

        MoveValue betaMoveValue;
        MoveValue moveValue = new MoveValue(beta);

        for (Move move : moves) {
            processedMoves++;

            // beta = min[beta, AlphaBeta(N_k,alpha,beta)]
            final int v = moveValue.value;
            betaMoveValue = playAndUndo(board, move, () -> alphaBeta(depth - 1, board, alpha, v));

            if (betaMoveValue.value < moveValue.value) {
                moveValue = new MoveValue(move, betaMoveValue.value); // better
            }

            // beta cutoff
            if (alpha >= moveValue.value) {
                return new MoveValue(moveValue.move, alpha);
            }
        }

        // if MIN is going to lose in 2 o more moves, we let it play
        if (moveValue.move == null) {
            moveValue = new MoveValue(moves.stream().findFirst().orElseThrow(), moveValue.value);
        }

        return moveValue;
    }

    protected MoveValue leafMoveValue(Board board) {
        return new MoveValue(strategy.apply(board, pieceColor));
    }

    private MoveValue alphaBetaMax(int depth, Board board, int alpha, int beta) {
        Collection<Move> moves = board.generateMoves();

        // checkmate
        if (moves.isEmpty()) {
            return new MoveValue(Integer.MIN_VALUE + (level - depth + 1));
        }

        MoveValue alphaMoveValue;
        MoveValue moveValue = new MoveValue(alpha);

        for (Move move : moves) {
            processedMoves++;

            // alpha = max[alpha, AlphaBeta(N_k,alpha,beta)
            final int v = moveValue.value;
            alphaMoveValue = playAndUndo(board, move, () -> alphaBeta(depth - 1, board, v, beta));

            if (alphaMoveValue.value > moveValue.value) {
                moveValue = new MoveValue(move, alphaMoveValue.value); // better
            }

            // alpha cutoff
            if (moveValue.value >= beta) {
                return new MoveValue(moveValue.move, beta);
            }
        }

        // if MAX is going to lose in 2 o more moves, we let it play
        if (moveValue.move == null) {
            moveValue = new MoveValue(moves.stream().findFirst().orElseThrow(), moveValue.value);
        }

        return moveValue;
    }
}

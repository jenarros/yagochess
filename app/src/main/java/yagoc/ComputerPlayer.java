package yagoc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.function.BiFunction;

class ComputerPlayer implements Player, Serializable {
    final protected Logger logger;
    final protected SetType set;
    final private String name;
    final private int level;
    int processedMoves;
    private final BiFunction<Board, SetType, Integer> strategy;

    ComputerPlayer(String name, SetType set, Logger logger, int level, BiFunction<Board, SetType, Integer> strategy) {
        this.name = name;
        this.set = set;
        this.logger = logger;
        this.level = level;
        this.strategy = strategy;
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
        return set + "\t" + name() + "\t" + type() + "\t" + level;
    }

    public Move move(Board board) {
        processedMoves = 0;
        MoveValue moveValue = alphaBeta(level, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
        logger.info("alpha-beta: moves processed = " + processedMoves + ",  minimax = " + moveValue.value);

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

    private MoveValue alphaBetaMin(int depth, Board board, int alpha, int beta) {
        LinkedList<Move> moves = new LinkedList<>(board.generateMoves());

        // checkmate
        if (moves.size() == 0) {
            return new MoveValue(Integer.MAX_VALUE - (level - depth + 1));
        }

        MoveValue betaMoveValue;
        Move move;
        MoveResult moveResult;
        MoveValue moveValue = new MoveValue(beta);

        do {
            processedMoves++;
            move = moves.removeFirst();
            moveResult = board.play(move);

            // beta = min[beta, AlphaBeta(N_k,alpha,beta)]
            betaMoveValue = alphaBeta(depth - 1, board, alpha, moveValue.value);
            board.undo(moveResult);

            if (betaMoveValue.value < moveValue.value) {
                moveValue = new MoveValue(move, betaMoveValue.value); // better
            }

            // beta cutoff
            if (alpha >= moveValue.value) {
                return new MoveValue(moveValue.move, alpha);
            }
        } while (moves.size() != 0);

        //si pierde MIN en 2 o más movimientos dejamos que juegue hasta entonces
        if (moveValue.move == null) {
            moveValue = new MoveValue(move, moveValue.value);
        }

        return moveValue;
    }

    private MoveValue alphaBetaMax(int depth, Board board, int alpha, int beta) {
        LinkedList<Move> moves = new LinkedList<>(board.generateMoves());

        // checkmate
        if (moves.size() == 0) {
            return new MoveValue(Integer.MIN_VALUE + (level - depth + 1));
        }

        MoveValue alphaMoveValue;
        Move move;
        MoveResult moveResult;
        MoveValue moveValue = new MoveValue(alpha);

        do {
            processedMoves++;
            move = moves.removeFirst();
            moveResult = board.play(move);

            // alpha = max[alpha, AlphaBeta(N_k,alpha,beta)
            alphaMoveValue = alphaBeta(depth - 1, board, moveValue.value, beta);
            board.undo(moveResult);

            if (alphaMoveValue.value > moveValue.value) {
                moveValue = new MoveValue(move, alphaMoveValue.value); // better
            }

            // alpha cutoff
            if (moveValue.value >= beta) {
                return new MoveValue(moveValue.move, beta);
            }
        } while (moves.size() != 0);

        //si pierde MAX en 2 o más movimientos dejamos que juegue hasta entonces
        if (moveValue.move == null) {
            moveValue = new MoveValue(move, moveValue.value);
        }

        return moveValue;
    }

    protected MoveValue leafMoveValue(Board board) {
        return new MoveValue(strategy.apply(board, set));
    }
}

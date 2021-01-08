import java.io.Serializable;
import java.util.LinkedList;
import java.util.function.BiFunction;

class ComputerPlayer implements Player, Serializable {
    final protected Logger logger;
    final protected SetType set;
    final private String name;
    final private int level;
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
        return " \t" + set + "\t" + name() + "\t" + type() + "\t" + level;
    }

    public Move move(Board board) {
        MoveValue moveValue = alfaBeta(level, board, Integer.MIN_VALUE, Integer.MAX_VALUE);
        logger.info("Alfa-Beta: Moves processed = " + moveValue.processedMoves + ",  minimax = " + moveValue.value);

        return moveValue.move;
    }

    MoveValue alfaBeta(int depth, Board board, int alfa, int beta) {
        LinkedList<Move> moves = new LinkedList<>();
        //si es un nodo hoja
        if (depth == 0) {
            return leafMoveValue(board);
        } else if ((level - depth) % 2 == 0) {//si es nodo MAX
            //--moveValue.v = alpha
            MoveValue moveValue = new MoveValue();
            MoveValue alphaMoveValue;
            Move move = null;
            MoveResult moveResult;
            moveValue.value = alfa;

            moves.addAll(board.generateMoves());
            //MEJORA DEL ALGORITMO
            if (moves.size() == 0) {
                moveValue.value = Integer.MIN_VALUE + (level - depth + 1);
                return moveValue;
            }

            //Para k = 1 hasta b hacer
            while (moves.size() != 0) {
                moveValue.processedMoves++;
                move = moves.removeFirst();
                moveResult = board.play(move);//----------realizo la jugada

                //alfa = max[alfa, AlfaBeta(N_k,alfa,beta)
                alphaMoveValue = alfaBeta(depth - 1, board, moveValue.value, beta);
                moveValue.processedMoves += alphaMoveValue.processedMoves;
                if (alphaMoveValue.value > moveValue.value) {
                    moveValue.move = move;//mejor jugada
                    moveValue.value = alphaMoveValue.value;//mejor valor
                }

                board.undo(moveResult);//--------------deshago la jugada

                //si alfa >= beta entonces devolver beta -PODA-
                if (moveValue.value >= beta) {
                    moveValue.value = beta;
                    return moveValue;
                }
            }

            //si pierde MAX en 2 o más movimientos dejamos que juegue hasta entonces
            if (moveValue.move == null) {
                //camino.add(moveValue.move);
                moveValue.move = move;
            }
            //si k = b entonces devolver alfa
            return moveValue;
        } else {//si es nodo MIN
            MoveValue b;
            Move j = null;
            MoveResult m;
            MoveValue moveValue = new MoveValue();
            moveValue.value = beta;

            moves.addAll(board.generateMoves());
            //MEJORA DEL ALGORITMO
            if (moves.size() == 0) {
                moveValue.value = Integer.MAX_VALUE - (level - depth + 1);
                return moveValue;
            }

            //Para k = 1 hasta b hacer
            while (moves.size() != 0) {
                moveValue.processedMoves++;
                j = moves.removeFirst();
                m = board.play(j);//--------realizo la jugada

                //beta = min[beta, AlfaBeta(N_k,alfa,beta)]
                b = alfaBeta(depth - 1, board, alfa, moveValue.value);
                moveValue.processedMoves += b.processedMoves;
                if (b.value < moveValue.value) {
                    moveValue.move = j;//mejor jugada
                    moveValue.value = b.value;//mejor valor
                }

                board.undo(m);//--------------deshago la jugada

                //si alfa >= beta entonces devolver alfa -PODA-
                if (alfa >= moveValue.value) {
                    moveValue.value = alfa;
                    return moveValue;
                }
            }

            //si pierde MIN en 2 o más movimientos dejamos que juegue hasta entonces
            if (moveValue.move == null) {
                moveValue.move = j;
            }

            //si k = b entonces devolver beta
            return moveValue;
        }
    }

    protected MoveValue leafMoveValue(Board board) {
        MoveValue mv = new MoveValue();
        mv.value = strategy.apply(board, set);
        return mv;
    }
}

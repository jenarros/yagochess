import java.io.Serializable;
import java.util.LinkedList;

public class ComputerPlayer implements Player, Serializable {
    final protected Logger logger;
    final private int set;
    final private String name;
    final private String type; //"m" (machine) | "u" (user)
    final private int level;

    ComputerPlayer(String name, int set, String type, Logger logger, int level) {
        this.name = name;
        this.set = set;
        this.type = type;
        this.logger = logger;
        this.level = level;
    }

    @Override
    public String type() {
        return type;
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
        logger.info("/********************** Alfa-Beta *********************/");
        logger.info("Moved processed = " + moveValue.processedMoves + "  minimax = " + moveValue.value);

        return moveValue.move;
    }

    public MoveValue alfaBeta(int depth, Board board, int alfa, int beta) {
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

            board.generateMoves(moves);
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

            board.generateMoves(moves);
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

    protected MoveValue leafMoveValue(Board t) {
        MoveValue mv = new MoveValue();
        mv.value = F1(t);
        return mv;
    }

    public int F1(Board board) {
        int f1 = 0, f2 = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isPieceOurs(board, i, j)) {
                    switch (Math.abs(board.tab[i][j])) {
                        case 1://cuanto más adelante mejor
                            f1 += 100;
                            if (board.tab[i][j] < 0)
                                f1 += i * 20;
                            else
                                f1 += (7 - i) * 20;
                            //Un peón cubierto vale más
                            if (i + board.turn < 8 && i + board.turn > 0 && j - 1 > 0 && j + 1 < 8
                                    && (board.tab[i + board.turn][j - 1] == board.tab[i][j]
                                    || board.tab[i + board.turn][j + 1] == board.tab[i][j]))
                                f1 += 30;
                            break;
                        case 2://cuanto más al centro del tablero mejor
                            f1 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                            if (board.tab[i][j] < 0)
                                f1 += Math.abs(3.5 - i) * 10;
                            else
                                f1 += Math.abs(3.5 - i) * 10;
                            break;
                        case 3:
                            f1 += 300 + board.Movimientos(i, j) * 10;
                            break;
                        case 4:
                            f1 += 500;
                            break;
                        case 5://cuanto más al centro mejor
                            f1 += 940 + (3.5 - Math.abs(3.5 - j)) * 20;
                            if (board.tab[i][j] < 0)
                                f1 += Math.abs(3.5 - i) * 10;
                            else
                                f1 += Math.abs(3.5 - i) * 10;
                            break;
                        case 6:
                            //el rey siempre está así que no lo evaluamos
                            break;
                        default:
                            logger.warn("ERROR GRAVE");
                    }
                } else if (isPieceTheirs(board, i, j)) {
                    switch (Math.abs(board.tab[i][j])) {
                        case 1://cuanto más adelante mejor
                            f2 += 100;
                            if (board.tab[i][j] < 0)
                                f2 += i * 30;
                            else
                                f2 += (7 - i) * 30;
                            if (i + board.turn < 8 && i + board.turn > 0 && j - 1 > 0 && j + 1 < 8
                                    && (board.tab[i + board.turn][j - 1] == board.tab[i][j]
                                    || board.tab[i + board.turn][j + 1] == board.tab[i][j]))
                                f2 += 20;
                            break;
                        case 2:
                            f2 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                            break;
                        case 3:
                            f2 += 330 + board.Movimientos(i, j) * 10;
                            break;
                        case 4:
                            f2 += 500;
                            break;
                        case 5:
                            f2 += 1000;
                            if (board.tab[i][j] < 0)
                                f2 += Math.abs(3.5 - i) * 10;
                            else
                                f2 += Math.abs(3.5 - i) * 10;
                            break;
                        case 6:
                            //el rey siempre está así que no lo evaluamos
                            break;
                        default:
                            logger.warn("ERROR GRAVE");
                    }
                }
            }
        }
        return f1 - f2;
    }

    boolean isPieceOurs(Board board, int i, int j) {
        return board.tab[i][j] * set > 0;
    }

    boolean isPieceTheirs(Board board, int i, int j) {
        return board.tab[i][j] * set < 0;
    }
}

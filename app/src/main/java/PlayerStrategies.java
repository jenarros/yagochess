import java.util.function.BiFunction;

public class PlayerStrategies {
    public static BiFunction<Board, Integer, Integer> F1 = (board, set) -> {

        int f1 = 0, f2 = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isPieceOurs(board, set, i, j)) {
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
                        default:
                            break;
                    }
                } else if (isPieceTheirs(board, set, i, j)) {
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
                        default:
                            break;
                    }
                }
            }
        }
        return f1 - f2;
    };
    public static BiFunction<Board, Integer, Integer> F2 = (board, set) -> {
        int f1 = 0, f2 = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isPieceOurs(board, set, i, j)) {
                    switch (Math.abs(board.tab[i][j])) {
                        case 1://cuanto más adelante mejor
                            f1 += 100;
                            if (board.tab[i][j] < 0)
                                f1 += i * 20;
                            else
                                f1 += (7 - i) * 20;
                            break;
                        case 2://cuanto más al centro del tablero mejor
                            f1 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                            if (board.tab[i][j] < 0)
                                f1 += Math.abs(3.5 - i) * 10;
                            else
                                f1 += Math.abs(3.5 - i) * 10;
                            break;
                        case 3:
                            f1 += 330 + board.Movimientos(i, j) * 10;
                            break;
                        case 4:
                            f1 += 500;
                            if (board.tab[i][j] < 0)
                                f1 += Math.abs(3.5 - i) * 15;
                            else
                                f1 += Math.abs(3.5 - i) * 15;
                            break;
                        case 5://cuanto más al centro del tablero mejor
                            f1 += 940 + (3.5 - Math.abs(3.5 - j)) * 20;
                            if (board.tab[i][j] < 0)
                                f1 += Math.abs(3.5 - i) * 10;
                            else
                                f1 += Math.abs(3.5 - i) * 10;
                            break;
                        default:
                    }
                } else if (isPieceTheirs(board, set, i, j)) {
                    switch (Math.abs(board.tab[i][j])) {
                        case 1: //cuanto más adelante mejor
                            f2 += 100;
                            if (board.tab[i][j] < 0)
                                f2 += i * 30;
                            else
                                f2 += (7 - i) * 30;
                            break;
                        case 2:
                            f2 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                            break;
                        case 3:
                            f2 += 330 + board.Movimientos(i, j) * 10;
                            if (board.tab[i][j] < 0)
                                f2 += Math.abs(3.5 - i) * 10;
                            else
                                f2 += Math.abs(3.5 - i) * 10;
                            break;
                        case 4:
                            f2 += 500;
                            break;
                        case 5:
                            f2 += 940 + (3.5 - Math.abs(3.5 - j)) * 10;
                            if (board.tab[i][j] < 0)
                                f2 += Math.abs(3.5 - i) * 10;
                            else
                                f2 += Math.abs(3.5 - i) * 10;
                            break;
                        default:
                    }
                }
            }
        }
        return f1 - f2;
    };

    public static boolean isPieceOurs(Board board, int set, int i, int j) {
        return board.tab[i][j] * set > 0;
    }

    public static boolean isPieceTheirs(Board board, int set, int i, int j) {
        return board.tab[i][j] * set < 0;
    }
}

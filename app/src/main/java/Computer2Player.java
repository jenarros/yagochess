public class Computer2Player extends ComputerPlayer {
    public Computer2Player(String name, int set, String type, Logger logger, int level) {
        super(name, set, type, logger, level);
    }

    protected MoveValue leafMoveValue(Board t) {
        MoveValue mv = new MoveValue();
        mv.value = F2(t);

        return mv;
    }

    private int F2(Board board) {
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
                        case 6:
                            //el rey siempre está así que no lo evaluamos
                            break;
                        default:
                            logger.warn("ERROR GRAVE");
                    }
                } else if (isPieceTheirs(board, i, j)) {
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
}

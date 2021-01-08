import java.util.function.BiFunction;

class PlayerStrategies {
    static BiFunction<Board, SetType, Integer> F1 = (board, set) -> {
        return (Integer) Square.allSquares.stream().map((square) -> {
            int acc = 0;
            final Piece piece = board.get(square);

            if (piece == Piece.none) {
                // ignore empty squares
            } else if (isPieceOurs(board, set, square)) {
                switch (piece.type) {
                    case pawn: // further ahead is better
                        acc += 100;
                        if (piece.set == SetType.blackSet)
                            acc += square.x * 20;
                        else
                            acc += (7 - square.x) * 20;

                        //Un peón cubierto vale más
                        if (square.nextRank(set).exists() && square.y - 1 > 0 && square.y + 1 < 8
                                && (board.get(square.nextRankPreviousFile(set)) == piece || board.get(square.nextRankPreviousFile(set)) == piece))
                            acc += 30;
                        break;
                    case knight://cuanto más al centro del tablero mejor
                        acc += 300 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        if (piece.set == SetType.blackSet)
                            acc += Math.abs(3.5 - square.x) * 10;
                        else
                            acc += Math.abs(3.5 - square.x) * 10;
                        break;
                    case bishop:
                        acc += 300 + board.possibleMoves(square) * 10;
                        break;
                    case rook:
                        acc += 500;
                        break;
                    case queen://cuanto más al centro mejor
                        acc += 940 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        if (piece.set == SetType.blackSet)
                            acc += Math.abs(3.5 - square.x) * 10;
                        else
                            acc += Math.abs(3.5 - square.x) * 10;
                        break;
                    default:
                        break;
                }
            } else if (isPieceTheirs(board, set, square)) {
                switch (piece.type) {
                    case pawn: // further ahead is better
                        acc -= 100;
                        if (piece.set == SetType.blackSet)
                            acc -= square.x * 30;
                        else
                            acc -= (7 - square.x) * 30;
                        if (square.nextRank(set).exists() && square.y - 1 > 0 && square.y + 1 < 8
                                && (board.get(square.nextRankPreviousFile(set)) == piece || board.get(square.nextRankPreviousFile(set)) == piece))
                            acc -= 20;
                        break;
                    case knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        break;
                    case bishop:
                        acc -= 330 + board.possibleMoves(square) * 10;
                        break;
                    case rook:
                        acc -= 500;
                        break;
                    case queen:
                        acc -= 1000;
                        acc -= Math.abs(3.5 - square.x) * 10;
                        break;
                    default:
                        break;
                }
            }
            return acc;
        }).mapToInt(Integer::intValue).sum();
    };

    static BiFunction<Board, SetType, Integer> F2 = (board, set) -> {
        return (Integer) Square.allSquares.stream().map((square) -> {
            int acc = 0;
            final Piece piece = board.get(square);

            if (isPieceOurs(board, set, square)) {
                switch (piece.type) {
                    case pawn://cuanto más adelante mejor
                        acc += 100;
                        if (piece.set == SetType.blackSet)
                            acc += square.x * 20;
                        else
                            acc += (7 - square.x) * 20;
                        break;
                    case knight://cuanto más al centro del tablero mejor
                        acc += 300 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        if (piece.set == SetType.blackSet)
                            acc += Math.abs(3.5 - square.x) * 10;
                        else
                            acc += Math.abs(3.5 - square.x) * 10;
                        break;
                    case bishop:
                        acc += 330 + board.possibleMoves(square) * 10;
                        break;
                    case rook:
                        acc += 500;
                        if (piece.set == SetType.blackSet)
                            acc += Math.abs(3.5 - square.x) * 15;
                        else
                            acc += Math.abs(3.5 - square.x) * 15;
                        break;
                    case queen://cuanto más al centro del tablero mejor
                        acc += 940 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        if (piece.set == SetType.blackSet)
                            acc += Math.abs(3.5 - square.x) * 10;
                        else
                            acc += Math.abs(3.5 - square.x) * 10;
                        break;
                    default:
                }
            } else if (isPieceTheirs(board, set, square)) {
                switch (piece.type) {
                    case pawn:
                        acc -= 100;
                        if (piece.set == SetType.blackSet)
                            acc -= square.x * 30;
                        else
                            acc -= (7 - square.x) * 30;
                        break;
                    case knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.y)) * 20;
                        break;
                    case bishop:
                        acc -= 330 + board.possibleMoves(square) * 10;
                        if (piece.set == SetType.blackSet)
                            acc -= Math.abs(3.5 - square.x) * 10;
                        else
                            acc -= Math.abs(3.5 - square.x) * 10;
                        break;
                    case rook:
                        acc -= 500;
                        break;
                    case king:
                        acc -= 940 + (3.5 - Math.abs(3.5 - square.y)) * 10;
                        if (piece.set == SetType.blackSet)
                            acc -= Math.abs(3.5 - square.x) * 10;
                        else
                            acc -= Math.abs(3.5 - square.x) * 10;
                        break;
                    default:
                }
            }
            return acc;
        }).mapToInt(Integer::intValue).sum();
    };

    static boolean isPieceOurs(Board board, SetType set, Square square) {
        return board.get(square).set == set;
    }

    static boolean isPieceTheirs(Board board, SetType set, Square square) {
        return board.get(square).set != set;
    }
}

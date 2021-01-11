package yagoc;

import yagoc.pieces.Piece;
import yagoc.pieces.Pieces;

import java.io.Serializable;
import java.util.function.BiFunction;

public class PlayerStrategy implements Serializable {
    static PlayerStrategy F1 = new PlayerStrategy((board, set) -> {
        return Square.allSquares.stream().map((square) -> {
            int acc = 0;
            final Piece piece = board.pieceAt(square);

            if (piece == Pieces.none) {
                // ignore empty squares
            } else if (isPieceOurs(board, set, square)) {
                switch (piece.pieceType()) {
                    case pawn: // further ahead is better
                        acc += 100;
                        if (piece.setType() == SetType.blackSet)
                            acc += square.rank * 20;
                        else
                            acc += (7 - square.rank) * 20;

                        //Un peón cubierto vale más
                        if (square.nextRank(set).exists() && square.file - 1 > 0 && square.file + 1 < 8
                                && (board.pieceAt(square.nextRankPreviousFile(set)) == piece || board.pieceAt(square.nextRankPreviousFile(set)) == piece))
                            acc += 30;
                        break;
                    case knight://cuanto más al centro del tablero mejor
                        acc += 300 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        if (piece.setType() == SetType.blackSet)
                            acc += Math.abs(3.5 - square.rank) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank) * 10;
                        break;
                    case bishop:
                        acc += 300 + board.generateMoves(square).size() * 10;
                        break;
                    case rook:
                        acc += 500;
                        break;
                    case queen://cuanto más al centro mejor
                        acc += 940 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        if (piece.setType() == SetType.blackSet)
                            acc += Math.abs(3.5 - square.rank) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank) * 10;
                        break;
                    default:
                        break;
                }
            } else if (isPieceTheirs(board, set, square)) {
                switch (piece.pieceType()) {
                    case pawn: // further ahead is better
                        acc -= 100;
                        if (piece.setType() == SetType.blackSet)
                            acc -= square.rank * 30;
                        else
                            acc -= (7 - square.rank) * 30;
                        if (square.nextRank(set).exists() && square.file - 1 > 0 && square.file + 1 < 8
                                && (board.pieceAt(square.nextRankPreviousFile(set)) == piece || board.pieceAt(square.nextRankPreviousFile(set)) == piece))
                            acc -= 20;
                        break;
                    case knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        break;
                    case bishop:
                        acc -= 330 + board.generateMoves(square).size() * 10;
                        break;
                    case rook:
                        acc -= 500;
                        break;
                    case queen:
                        acc -= 1000;
                        acc -= Math.abs(3.5 - square.rank) * 10;
                        break;
                    default:
                        break;
                }
            }
            return acc;
        }).mapToInt(Integer::intValue).sum();
    });
    static PlayerStrategy F2 = new PlayerStrategy((board, set) -> {
        return Square.allSquares.stream().map((square) -> {
            int acc = 0;
            final Piece piece = board.pieceAt(square);

            if (piece == Pieces.none) {
                // ignore empty squares
            } else if (isPieceOurs(board, set, square)) {
                switch (piece.pieceType()) {
                    case pawn://cuanto más adelante mejor
                        acc += 100;
                        if (piece.setType() == SetType.blackSet)
                            acc += square.rank * 20;
                        else
                            acc += (7 - square.rank) * 20;
                        break;
                    case knight://cuanto más al centro del tablero mejor
                        acc += 300 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        if (piece.setType() == SetType.blackSet)
                            acc += Math.abs(3.5 - square.rank) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank) * 10;
                        break;
                    case bishop:
                        acc += 330 + board.generateMoves(square).size() * 10;
                        break;
                    case rook:
                        acc += 500;
                        if (piece.setType() == SetType.blackSet)
                            acc += Math.abs(3.5 - square.rank) * 15;
                        else
                            acc += Math.abs(3.5 - square.rank) * 15;
                        break;
                    case queen://cuanto más al centro del tablero mejor
                        acc += 940 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        if (piece.setType() == SetType.blackSet)
                            acc += Math.abs(3.5 - square.rank) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank) * 10;
                        break;
                    default:
                }
            } else if (isPieceTheirs(board, set, square)) {
                switch (piece.pieceType()) {
                    case pawn:
                        acc -= 100;
                        if (piece.setType() == SetType.blackSet)
                            acc -= square.rank * 30;
                        else
                            acc -= (7 - square.rank) * 30;
                        break;
                    case knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.file)) * 20;
                        break;
                    case bishop:
                        acc -= 330 + board.generateMoves(square).size() * 10;
                        if (piece.setType() == SetType.blackSet)
                            acc -= Math.abs(3.5 - square.rank) * 10;
                        else
                            acc -= Math.abs(3.5 - square.rank) * 10;
                        break;
                    case rook:
                        acc -= 500;
                        break;
                    case king:
                        acc -= 940 + (3.5 - Math.abs(3.5 - square.file)) * 10;
                        if (piece.setType() == SetType.blackSet)
                            acc -= Math.abs(3.5 - square.rank) * 10;
                        else
                            acc -= Math.abs(3.5 - square.rank) * 10;
                        break;
                    default:
                }
            }
            return acc;
        }).mapToInt(Integer::intValue).sum();
    });
    private final SerializableBiFunction strategy;

    public PlayerStrategy(SerializableBiFunction strategy) {
        this.strategy = strategy;
    }

    public Integer apply(Board board, SetType setType) {
        return strategy.apply(board, setType);
    }

    interface SerializableBiFunction extends BiFunction<Board, SetType, Integer>, Serializable {
    }

    static boolean isPieceOurs(Board board, SetType set, Square square) {
        return board.pieceAt(square).setType() == set;
    }

    static boolean isPieceTheirs(Board board, SetType set, Square square) {
        return board.pieceAt(square).setType() != set;
    }
}

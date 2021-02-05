package yagoc.players;

import yagoc.board.Board;
import yagoc.board.Square;
import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;

import java.io.Serializable;

import static yagoc.board.BoardRules.generateMoves;

public class PlayerStrategy implements Serializable {
    public static PlayerStrategy F1 = new PlayerStrategy((board, color) -> {
        return Square.allSquares.stream().map((square) -> {
            int acc = 0;

            if (board.noneAt(square)) {
                // ignore empty squares
            } else if (isPieceOurs(board, color, square)) {
                final Piece piece = board.pieceAt(square);

                switch (piece.pieceType()) {
                    case Pawn: // further ahead is better
                        acc += 100;
                        if (piece.color() == PieceColor.blackSet)
                            acc += square.rank() * 20;
                        else
                            acc += (7 - square.rank()) * 20;

                        // Un peón cubierto vale más
                        if (square.nextRank(color).exists() && square.file() - 1 > 0 && square.file() + 1 < 8
                                && (board.pieceAt(square.nextRankPreviousFile(color)).equals(piece) || board.pieceAt(square.nextRankPreviousFile(color)).equals(piece)))
                            acc += 30;
                        break;
                    case Knight: // middle of the board is better
                        acc += 300 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        if (piece.color() == PieceColor.blackSet)
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        break;
                    case Bishop:
                        acc += 300 + generateMoves(board, square).count() * 10;
                        break;
                    case Rook:
                        acc += 500;
                        break;
                    case Queen: // middle of the board is better
                        acc += 940 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        if (piece.color() == PieceColor.blackSet)
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        break;
                    default:
                        break;
                }
            } else if (isPieceTheirs(board, color, square)) {
                final Piece piece = board.pieceAt(square);

                switch (piece.pieceType()) {
                    case Pawn: // further ahead is better
                        acc -= 100;
                        if (piece.color() == PieceColor.blackSet)
                            acc -= square.rank() * 30;
                        else
                            acc -= (7 - square.rank()) * 30;
                        if (square.nextRank(color).exists() && square.file() - 1 > 0 && square.file() + 1 < 8
                                && (board.pieceAt(square.nextRankPreviousFile(color)).equals(piece) || board.pieceAt(square.nextRankPreviousFile(color)).equals(piece)))
                            acc -= 20;
                        break;
                    case Knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        break;
                    case Bishop:
                        acc -= 330 + generateMoves(board, square).count() * 10;
                        break;
                    case Rook:
                        acc -= 500;
                        break;
                    case Queen:
                        acc -= 1000;
                        acc -= Math.abs(3.5 - square.rank()) * 10;
                        break;
                    default:
                        break;
                }
            }
            return acc;
        }).mapToInt(Integer::intValue).sum();
    });

    public static PlayerStrategy F2 = new PlayerStrategy((board, set) -> {
        return Square.allSquares.stream().map((square) -> {
            int acc = 0;

            if (board.noneAt(square)) {
                // ignore empty squares
            } else if (isPieceOurs(board, set, square)) {
                final Piece piece = board.pieceAt(square);
                switch (piece.pieceType()) {
                    case Pawn: // further ahead is better
                        acc += 100;
                        if (piece.color() == PieceColor.blackSet)
                            acc += square.rank() * 20;
                        else
                            acc += (7 - square.rank()) * 20;
                        break;
                    case Knight: // middle of the board is better
                        acc += 300 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        if (piece.color() == PieceColor.blackSet)
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        break;
                    case Bishop:
                        acc += 330 + generateMoves(board, square).count() * 10;
                        break;
                    case Rook:
                        acc += Math.abs(3.5 - square.rank()) * 15 + 500;
                        break;
                    case Queen: // middle of the board is better
                        acc += 940 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        if (piece.color() == PieceColor.blackSet)
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc += Math.abs(3.5 - square.rank()) * 10;
                        break;
                    default:
                }
            } else if (isPieceTheirs(board, set, square)) {
                final Piece piece = board.pieceAt(square);

                switch (piece.pieceType()) {
                    case Pawn:
                        acc -= 100;
                        if (piece.color() == PieceColor.blackSet)
                            acc -= square.rank() * 30;
                        else
                            acc -= (7 - square.rank()) * 30;
                        break;
                    case Knight:
                        acc -= 300 + (3.5 - Math.abs(3.5 - square.file())) * 20;
                        break;
                    case Bishop:
                        acc -= 330 + generateMoves(board, square).count() * 10;
                        if (piece.color() == PieceColor.blackSet)
                            acc -= Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc -= Math.abs(3.5 - square.rank()) * 10;
                        break;
                    case Rook:
                        acc -= 500;
                        break;
                    case King:
                        acc -= 940 + (3.5 - Math.abs(3.5 - square.file())) * 10;
                        if (piece.color() == PieceColor.blackSet)
                            acc -= Math.abs(3.5 - square.rank()) * 10;
                        else
                            acc -= Math.abs(3.5 - square.rank()) * 10;
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

    static boolean isPieceOurs(Board board, PieceColor set, Square square) {
        return board.pieceAt(square).color() == set;
    }

    static boolean isPieceTheirs(Board board, PieceColor color, Square square) {
        return board.pieceAt(square).color() != color;
    }

    public Integer apply(Board board, PieceColor pieceColor) {
        return strategy.apply(board, pieceColor);
    }
}

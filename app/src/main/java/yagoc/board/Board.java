package yagoc.board;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;

import java.util.concurrent.Callable;

import static yagoc.board.Square.castlingKingsideBlackFrom;
import static yagoc.board.Square.castlingKingsideBlackTo;
import static yagoc.board.Square.castlingKingsideWhiteFrom;
import static yagoc.board.Square.castlingKingsideWhiteTo;
import static yagoc.board.Square.castlingQueensideBlackFrom;
import static yagoc.board.Square.castlingQueensideBlackTo;
import static yagoc.board.Square.castlingQueensideWhiteFrom;
import static yagoc.board.Square.castlingQueensideWhiteTo;
import static yagoc.pieces.PieceType.Pawn;
import static yagoc.pieces.Pieces.none;

public class Board extends BoardState {
    public Board() {
        reset();
    }

    public Board(BoardState board) {
        resetWith(board);
    }

    public static Board parseBoard(String stringBoard) {
        String cleanStringBoard = stringBoard.replaceAll(" |\t|\n", "");
        Board board = new Board();
        Square.allSquares.forEach((square) -> {
            board.pieceAt(square, Pieces.parse(cleanStringBoard.charAt(square.arrayPosition())));
        });
        return board;
    }

    public <T> T playAndUndo(Move move, Callable<T> callable) {
        play(move);
        try {
            T moveValue = callable.call();
            undo();
            return moveValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Board playAndUndo(Square from, Square to) {
        play(from, to);
        undo();

        return this;
    }

    public Board play(Square from, Square to) {
        return play(new Move(pieceAt(from), from, to));
    }

    private void updateMovedPieces(Move move) {
        if (move.fromPiece().equals(Pieces.whiteKing))
            whiteKingMoved = true;
        else if (move.fromPiece().equals(Pieces.whiteRook) && move.from().file() == 0)
            whiteLeftRookMoved = true;
        else if (move.fromPiece().equals(Pieces.whiteRook) && move.from().file() == 7)
            whiteRightRookMoved = true;

        if (move.fromPiece().equals(Pieces.blackKing))
            blackKingMoved = true;
        else if (move.fromPiece().equals(Pieces.blackRook) && move.from().file() == 0)
            blackLeftRookMoved = true;
        else if (move.fromPiece().equals(Pieces.blackRook) && move.from().file() == 7)
            blackRightRookMoved = true;
    }

    void undo() {
        MoveLog moveLog = moves.pop();

        if (moveLog.type == MoveType.normal) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);

        } else if (moveLog.type == MoveType.enPassant) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);
            pieceAt(moveLog.move.enPassantSquare(), moveLog.enPassantPiece);

        } else if (moveLog.type == MoveType.castling) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);
            pieceAt(moveLog.castlingExtraMove.from(), moveLog.castlingExtraMove.fromPiece());
            pieceAt(moveLog.castlingExtraMove.to(), none);
        }
        whiteLeftRookMoved = moveLog.whiteLeftRookMoved;
        whiteRightRookMoved = moveLog.whiteRightRookMoved;
        whiteKingMoved = moveLog.whiteKingMoved;
        blackLeftRookMoved = moveLog.blackLeftRookMoved;
        blackRightRookMoved = moveLog.blackRightRookMoved;
        blackKingMoved = moveLog.blackKingMoved;
        enPassant[moveLog.move.to().file()] = moveLog.enPassant;
        drawCounter = moveLog.drawCounter;
        moveCounter = moveLog.moveCounter;

        togglePlayer();
    }

    MoveLog playCastlingExtraMove(Move move) {
        final Move castlingExtraMove;

        if (move.isCastlingQueenside()) {
            if (move.fromPiece().color() == PieceColor.whiteSet) {
                castlingExtraMove = new Move(Pieces.whiteRook, castlingQueensideWhiteFrom, castlingQueensideWhiteTo);
                pieceAt(castlingQueensideWhiteFrom, none);
                pieceAt(castlingQueensideWhiteTo, Pieces.whiteRook);
            } else {
                castlingExtraMove = new Move(Pieces.blackRook, castlingQueensideBlackFrom, castlingQueensideBlackTo);
                pieceAt(castlingQueensideBlackFrom, none);
                pieceAt(castlingQueensideBlackTo, Pieces.blackRook);
            }

        } else if (move.fromPiece().color() == PieceColor.whiteSet) {
            castlingExtraMove = new Move(Pieces.whiteRook, castlingKingsideWhiteFrom, castlingKingsideWhiteTo);
            pieceAt(castlingKingsideWhiteFrom, none);
            pieceAt(castlingKingsideWhiteTo, Pieces.whiteRook);
        } else {
            castlingExtraMove = new Move(Pieces.blackRook, castlingKingsideBlackFrom, castlingKingsideBlackTo);
            pieceAt(castlingKingsideBlackFrom, none);
            pieceAt(castlingKingsideBlackTo, Pieces.blackRook);
        }
        return MoveLog.castling(this, move, pieceAt(move.to()), castlingExtraMove);
    }

    public Board play(Move move) {
        final MoveLog moveLog;

        if (move.isCastling()) {
            moveLog = playCastlingExtraMove(move);
            drawCounter++;
        } else {
            // if pawn advances 2 squares, it is possible that next move is a en passant capture
            if (move.fromPiece().pieceType() == Pawn && move.rankDistanceAbs() == 2) {
                enPassant[move.to().file()] = moveCounter;
            }
            // en passant capture
            if (move.fromPiece().pieceType() == Pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && enPassant[move.to().file()] == moveCounter - 1) {
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveLog = MoveLog.enPassant(this, move, pieceAt(move.to()));
                pieceAt(moveLog.move.enPassantSquare(), none);
            } else {
                moveLog = MoveLog.normalMove(this, move, pieceAt(move.to()));
            }
            if (!pieceAt(move.to()).equals(none) || move.fromPiece().pieceType() == Pawn) {
                // draw counter restarts when we capture a piece or move a pawn
                drawCounter = 0;
            } else {
                drawCounter++;
            }
        }

        updateMovedPieces(move);

        pieceAt(move.from(), none);
        pieceAt(move.to(), move.fromPiece());
        togglePlayer();
        moveCounter++;
        moves.add(moveLog);

        return this;
    }

    public String toPrettyString() {
        StringBuilder buffer = new StringBuilder();
        Square.allSquares.forEach((square) -> {
            Piece piece = pieceAt(square);
            buffer.append(piece.toUniqueChar());
            if (square.file() % 8 == 7 && square.rank() < 7) buffer.append("\n");
        });

        return buffer.toString();
    }
}

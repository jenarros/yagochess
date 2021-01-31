package yagoc;

import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;

import java.util.concurrent.Callable;

import static yagoc.Square.castlingKingsideBlackFrom;
import static yagoc.Square.castlingKingsideBlackTo;
import static yagoc.Square.castlingKingsideWhiteFrom;
import static yagoc.Square.castlingKingsideWhiteTo;
import static yagoc.Square.castlingQueensideBlackFrom;
import static yagoc.Square.castlingQueensideBlackTo;
import static yagoc.Square.castlingQueensideWhiteFrom;
import static yagoc.Square.castlingQueensideWhiteTo;
import static yagoc.pieces.PieceType.Pawn;
import static yagoc.pieces.Pieces.none;

// 1 pawn
// 2 knight
// 3 bishop
// 4 rook
// 5 queen
// 6 king
/*
0        -4, -2, -3, -5, -6, -3, -2, -4,
1        -1, -1, -1, -1, -1, -1, -1, -1,
2         0,  0,  0,  0,  0,  0,  0,  0,
3         0,  0,  0,  0,  0,  0,  0,  0,
4         0,  0,  0,  0,  0,  0,  0,  0,
5         0,  0,  0,  0,  0,  0,  0,  0,
6         1,  1,  1,  1,  1,  1,  1,  1,
7         4,  2,  3,  5,  6,  3,  2,  4
*/
public class Board extends BoardState {
    public Board() {
        reset();
    }

    public Board(BoardState board) {
        resetWith(board);
    }

    @Override
    public Board clone() {
        return new Board(this);
    }

    public static <T> T playAndUndo(Board board, Move move, Callable<T> callable) {
        MoveLog moveLog = board.play(move);
        try {
            T moveValue = callable.call();
            board.undo(moveLog);
            return moveValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MoveLog play(Move move) {
        MoveLog moveLog = new MoveLog(this, move, pieceAt(move.to()));

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

        if (move.isCastling()) {
            drawCounter++;
            moveLog.type = MoveType.castling;
            playCastlingExtraMove(moveLog);
        } else {
            if (!pieceAt(move.to()).equals(none) || move.fromPiece().pieceType() == Pawn) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else {
                drawCounter++;
            }
            //si peon avanza dos activar posible captura al paso
            if (move.fromPiece().pieceType() == Pawn && move.rankDistanceAbs() == 2) {
                enPassant[move.to().file()] = moveCounter;
            }
            //realizar captura al paso
            if (move.fromPiece().pieceType() == Pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && enPassant[move.to().file()] == moveCounter - 1) {
                moveLog.type = MoveType.enPassant;
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveLog.enPassantSquare = move.to().previousRank(move.fromPiece().color());
                moveLog.enPassantPiece = pieceAt(moveLog.enPassantSquare);
                pieceAt(moveLog.enPassantSquare, none);
            } else {
                moveLog.type = MoveType.normal;
            }
        }

        pieceAt(move.from(), none);
        pieceAt(move.to(), move.fromPiece());
        togglePlayer();
        moveCounter++;
        return moveLog;
    }

    void undo(MoveLog moveLog) {
        if (moveLog.type == MoveType.normal) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);

        } else if (moveLog.type == MoveType.enPassant) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);
            pieceAt(moveLog.enPassantSquare, moveLog.enPassantPiece);

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

    void playCastlingExtraMove(MoveLog moveLog) {
        if (moveLog.move.isCastlingQueenside()) {
            if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
                moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingQueensideWhiteFrom, castlingQueensideWhiteTo);
                pieceAt(castlingQueensideWhiteFrom, none);
                pieceAt(castlingQueensideWhiteTo, Pieces.whiteRook);
            } else {
                moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingQueensideBlackFrom, castlingQueensideBlackTo);
                pieceAt(castlingQueensideBlackFrom, none);
                pieceAt(castlingQueensideBlackTo, Pieces.blackRook);
            }

        } else if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
            moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingKingsideWhiteFrom, castlingKingsideWhiteTo);
            pieceAt(castlingKingsideWhiteFrom, none);
            pieceAt(castlingKingsideWhiteTo, Pieces.whiteRook);
        } else {
            moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingKingsideBlackFrom, castlingKingsideBlackTo);
            pieceAt(castlingKingsideBlackFrom, none);
            pieceAt(castlingKingsideBlackTo, Pieces.blackRook);
        }
    }
}

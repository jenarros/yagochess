package yagoc.board;

import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;

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

    public void play(Move move) {
        final MoveLog moveLog;

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
            moveLog = playCastlingExtraMove(move);
        } else {
            if (!pieceAt(move.to()).equals(none) || move.fromPiece().pieceType() == Pawn) {
                // draw counter restarts when we capture a piece or move a pawn
                drawCounter = 0;
            } else {
                drawCounter++;
            }
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
        }

        pieceAt(move.from(), none);
        pieceAt(move.to(), move.fromPiece());
        togglePlayer();
        moveCounter++;
        moves.add(moveLog);
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
}

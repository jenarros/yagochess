package yagoc;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;
import yagoc.players.ComputerPlayer;
import yagoc.players.Player;
import yagoc.players.PlayerStrategy;
import yagoc.players.UserPlayer;

import java.io.Serializable;
import java.util.Arrays;

import static yagoc.pieces.Pieces.none;

public class BoardState implements Cloneable, Serializable {
    protected Piece[][] squares;
    protected int[] enPassant;
    protected Player currentPlayer;
    protected boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
    protected boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
    protected int drawCounter;
    protected int moveCounter;
    protected Player blackPlayer, whitePlayer;

    public void resetWith(BoardState board) {
        squares = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            squares[i] = board.squares[i].clone();
        }
        enPassant = board.enPassant.clone();
        currentPlayer = board.currentPlayer;
        whiteLeftRookMoved = board.whiteLeftRookMoved;
        whiteRightRookMoved = board.whiteRightRookMoved;
        whiteKingMoved = board.whiteKingMoved;
        blackLeftRookMoved = board.blackLeftRookMoved;
        blackRightRookMoved = board.blackRightRookMoved;
        blackKingMoved = board.blackKingMoved;
        drawCounter = board.drawCounter;
        moveCounter = board.moveCounter;
        blackPlayer = board.blackPlayer;
        whitePlayer = board.whitePlayer;
    }

    public void reset() {
        squares = newTable();
        enPassant = new int[8];

        for (int k = 0; k < 8; k++) {
            enPassant[k] = -5; //movimiento absurdo
        }

        blackPlayer = new ComputerPlayer("computer 1", PieceColor.blackSet, 3, PlayerStrategy.F1);
        whitePlayer = new UserPlayer("user 1", PieceColor.whiteSet);
        currentPlayer = whitePlayer;

        squares[0][0] = Pieces.blackRook;
        squares[0][1] = Pieces.blackKnight;
        squares[0][2] = Pieces.blackBishop;
        squares[0][3] = Pieces.blackQueen;
        squares[0][4] = Pieces.blackKing;
        squares[0][5] = Pieces.blackBishop;
        squares[0][6] = Pieces.blackKnight;
        squares[0][7] = Pieces.blackRook;
        for (int file = 0; file < 8; file++) {
            squares[1][file] = Pieces.blackPawn;
        }

        squares[7][0] = Pieces.whiteRook;
        squares[7][1] = Pieces.whiteKnight;
        squares[7][2] = Pieces.whiteBishop;
        squares[7][3] = Pieces.whiteQueen;
        squares[7][4] = Pieces.whiteKing;
        squares[7][5] = Pieces.whiteBishop;
        squares[7][6] = Pieces.whiteKnight;
        squares[7][7] = Pieces.whiteRook;
        for (int file = 0; file < 8; file++) {
            squares[6][file] = Pieces.whitePawn;
        }
    }

    private Piece[][] newTable() {
        Piece[][] pieces = new Piece[8][8];

        for (Piece[] piece : pieces) {
            Arrays.fill(piece, none);
        }

        return pieces;
    }
}

package yagoc.board;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;
import yagoc.players.ComputerPlayer;
import yagoc.players.Player;
import yagoc.players.PlayerStrategy;
import yagoc.players.UserPlayer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

import static yagoc.pieces.Pieces.none;

public class BoardState implements Serializable {
    protected final Stack<MoveLog> moves = new Stack<>();
    protected final Piece[][] squares = new Piece[8][8];
    protected final int[] enPassant = new int[8];
    private Player currentPlayer;
    protected boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
    protected boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
    protected int drawCounter;
    protected int moveCounter;
    private Player blackPlayer, whitePlayer;

    public void resetWith(BoardState board) {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board.squares[i], 0, squares[i], 0, squares[i].length);
        }
        System.arraycopy(board.enPassant, 0, this.enPassant, 0, this.enPassant.length);
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
        moves.removeAllElements();
        moves.addAll(board.moves);
    }

    public void reset() {
        Arrays.fill(squares[2], none);
        Arrays.fill(squares[3], none);
        Arrays.fill(squares[4], none);
        Arrays.fill(squares[5], none);

        Arrays.fill(enPassant, -5);

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

        Arrays.fill(squares[1], Pieces.blackPawn);

        squares[7][0] = Pieces.whiteRook;
        squares[7][1] = Pieces.whiteKnight;
        squares[7][2] = Pieces.whiteBishop;
        squares[7][3] = Pieces.whiteQueen;
        squares[7][4] = Pieces.whiteKing;
        squares[7][5] = Pieces.whiteBishop;
        squares[7][6] = Pieces.whiteKnight;
        squares[7][7] = Pieces.whiteRook;

        Arrays.fill(squares[6], Pieces.whitePawn);
    }

    public int enPassant(int file) {
        return enPassant[file];
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public boolean hasWhiteLeftRookMoved() {
        return whiteLeftRookMoved;
    }

    public boolean hasWhiteRightRookMoved() {
        return whiteRightRookMoved;
    }

    public boolean hasWhiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean hasBlackLeftRookMoved() {
        return blackLeftRookMoved;
    }

    public boolean hasBlackRightRookMoved() {
        return blackRightRookMoved;
    }

    public boolean hasBlackKingMoved() {
        return blackKingMoved;
    }

    public int drawCounter() {
        return drawCounter;
    }

    public int moveCounter() {
        return moveCounter;
    }

    public Player blackPlayer() {
        return blackPlayer;
    }

    public Player whitePlayer() {
        return whitePlayer;
    }

    public void whitePlayer(Player player) {
        if (currentPlayer.equals(whitePlayer)) {
            currentPlayer = player;
        }
        whitePlayer = player;
    }

    public boolean isPieceOfOppositePlayer(Piece piece) {
        return piece.notOfSameColor(currentPlayer.pieceColor());
    }

    public boolean isPieceOfCurrentPlayer(Piece piece) {
        return piece.color() == currentPlayer.pieceColor();
    }

    protected void togglePlayer() {
        if (currentPlayer.equals(blackPlayer)) {
            currentPlayer = whitePlayer;
        } else {
            currentPlayer = blackPlayer;
        }
    }

    public void blackPlayer(Player player) {
        if (currentPlayer.equals(blackPlayer)) {
            currentPlayer = player;
        }
        blackPlayer = player;
    }

    public Piece pieceAt(int rank, int file) {
        Square square = new Square(rank, file);
        if (!square.exists()) {
            throw new IllegalArgumentException("Square " + square + " does not exist.");
        }
        return pieceAt(square);
    }

    public boolean noneAt(Square square) {
        return pieceAt(square).equals(none);
    }

    public Piece pieceAt(Square square) {
        return squares[square.rank()][square.file()];
    }

    public void pieceAt(Square square, Piece newPiece) {
        squares[square.rank()][square.file()] = newPiece;
    }

    public Player oppositePlayer() {
        if (currentPlayer.equals(whitePlayer)) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }
}

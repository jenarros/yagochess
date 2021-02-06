package yagoc.board;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;
import yagoc.players.ComputerPlayer;
import yagoc.players.Player;
import yagoc.players.PlayerStrategy;
import yagoc.players.UserPlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.Callable;

import static yagoc.pieces.PieceType.Pawn;
import static yagoc.pieces.Pieces.none;

public class Board implements BoardView {
    private final Stack<MoveLog> moves = new Stack<>();
    private final SquareBoard squareBoard = new SquareBoard();
    private final int[] enPassant = new int[8];
    private boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
    private boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
    private int drawCounter;
    private int moveCounter;
    private Player currentPlayer;
    private Player blackPlayer, whitePlayer;

    public Board() {
        reset();
    }

    public Board(Board board) {
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

    public void resetWith(Board board) {
        System.arraycopy(board.squareBoard.getPieces(), 0, squareBoard.getPieces(), 0, squareBoard.getPieces().length);
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
        Arrays.fill(enPassant, -5);

        blackPlayer = new ComputerPlayer("computer 1", PieceColor.blackSet, 3, PlayerStrategy.F1);
        whitePlayer = new UserPlayer("user 1", PieceColor.whiteSet);
        currentPlayer = whitePlayer;
        squareBoard.reset();
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

    public boolean isPieceOfCurrentPlayer(Piece piece) {
        return piece.color() == currentPlayer.pieceColor();
    }

    public void togglePlayer() {
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

    @Override
    public Piece pieceAt(int rank, int file) {
        Square square = new Square(rank, file);
        if (!square.exists()) {
            throw new IllegalArgumentException("Square " + square + " does not exist.");
        }
        return pieceAt(square);
    }

    @Override
    public boolean noneAt(Square square) {
        return pieceAt(square).equals(none);
    }

    @Override
    public Piece pieceAt(Square square) {
        return squareBoard.get(square.arrayPosition());
    }

    public void pieceAt(Square square, Piece newPiece) {
        squareBoard.set(square.arrayPosition(), newPiece);
    }

    public Player oppositePlayer() {
        if (currentPlayer.equals(whitePlayer)) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board that = (Board) o;
        return whiteLeftRookMoved == that.whiteLeftRookMoved &&
                whiteRightRookMoved == that.whiteRightRookMoved &&
                whiteKingMoved == that.whiteKingMoved &&
                blackLeftRookMoved == that.blackLeftRookMoved &&
                blackRightRookMoved == that.blackRightRookMoved &&
                blackKingMoved == that.blackKingMoved &&
                drawCounter == that.drawCounter &&
                moveCounter == that.moveCounter &&
                moves.equals(that.moves) &&
                squareBoard.equals(that.squareBoard) &&
                Arrays.equals(enPassant, that.enPassant) &&
                currentPlayer.equals(that.currentPlayer) &&
                blackPlayer.equals(that.blackPlayer) &&
                whitePlayer.equals(that.whitePlayer);
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
                castlingExtraMove = new Move(Pieces.whiteRook, Squares.a1.legacySquare(), Squares.d1.legacySquare());
                pieceAt(Squares.a1.legacySquare(), none);
                pieceAt(Squares.d1.legacySquare(), Pieces.whiteRook);
            } else {
                castlingExtraMove = new Move(Pieces.blackRook, Squares.a8.legacySquare(), Squares.d8.legacySquare());
                pieceAt(Squares.a8.legacySquare(), none);
                pieceAt(Squares.d8.legacySquare(), Pieces.blackRook);
            }

        } else if (move.fromPiece().color() == PieceColor.whiteSet) {
            castlingExtraMove = new Move(Pieces.whiteRook, Squares.h1.legacySquare(), Squares.f1.legacySquare());
            pieceAt(Squares.h1.legacySquare(), none);
            pieceAt(Squares.f1.legacySquare(), Pieces.whiteRook);
        } else {
            castlingExtraMove = new Move(Pieces.blackRook, Squares.h8.legacySquare(), Squares.f8.legacySquare());
            pieceAt(Squares.h8.legacySquare(), none);
            pieceAt(Squares.f8.legacySquare(), Pieces.blackRook);
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

    @Override
    public int hashCode() {
        int result = Objects.hash(moves, currentPlayer, whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved, blackLeftRookMoved, blackRightRookMoved, blackKingMoved, drawCounter, moveCounter, blackPlayer, whitePlayer);
        result = 31 * result + squareBoard.hashCode();
        result = 31 * result + Arrays.hashCode(enPassant);
        return result;
    }
}

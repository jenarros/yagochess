package yagoc;

import yagoc.board.Board;
import yagoc.board.Move;
import yagoc.board.Square;
import yagoc.pieces.PieceColor;
import yagoc.players.ComputerPlayer;
import yagoc.players.PlayerStrategy;
import yagoc.players.UserPlayer;
import yagoc.ui.UserOptionDialog;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static yagoc.Yagoc.logger;
import static yagoc.board.BoardRules.isCorrectMove;
import static yagoc.board.BoardRules.moveDoesNotCreateCheck;
import static yagoc.board.BoardRules.noMoreMovesAllowed;
import static yagoc.pieces.PiecesKt.blackPawn;
import static yagoc.pieces.PiecesKt.blackQueen;
import static yagoc.pieces.PiecesKt.whitePawn;
import static yagoc.pieces.PiecesKt.whiteQueen;

public class Controller {
    private static final int COMPUTER_PAUSE_SECONDS = 1;
    private final ArrayList<Board> checkpoints = new ArrayList<>();
    private final Board board;
    private final UserOptionDialog userOptions;
    private boolean finished;

    public Controller(Board board, UserOptionDialog userOptions) {
        this.board = board;
        this.userOptions = userOptions;
    }

    public void resetBoard(Board board) {
        checkpoints.clear();
        this.board.resetWith(board);
        nextMove();
    }

    public void undo() {
        if (!checkpoints.isEmpty()) {
            this.board.resetWith(checkpoints.remove(checkpoints.size() - 1));
            this.finished = false;
        }
    }

    public void saveBoard(String absolutePath) throws IOException {
        try (FileOutputStream fileStream = new FileOutputStream(absolutePath);
             ObjectOutputStream stream = new ObjectOutputStream(fileStream)) {

            stream.writeObject(board);
        }
    }

    public void move(Square from, Square to) {
        Board copy = new Board(board);

        if (moveIfPossible(from, to)) {
            checkpoints.add(copy);
        }

        SwingUtilities.invokeLater(this::nextMove);
    }

    public boolean moveIfPossible(Square from, Square to) {
        if (finished || !board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
            return false;
        } else if (board.currentPlayer().isUser()) {

            Move move = new Move(board.pieceAt(from), from, to);
            if (!from.equals(to)
                    && isCorrectMove(board, move)
                    && moveDoesNotCreateCheck(board, move)) {

                board.play(move);

                ifPawnHasReachedFinalRankReplaceWithQueen(board, move);

                finished = noMoreMovesAllowed(board);

                logger.info(move.toString());

                return true;
            }
        }

        return false;
    }

    public void ifPawnHasReachedFinalRankReplaceWithQueen(Board board, Move move) {
        //TODO What if there is already a queen?
        if ((move.fromPiece().equals(blackPawn) && move.to().rank() == 7)) {
            board.pieceAt(move.to(), blackQueen);
        } else if (move.fromPiece().equals(whitePawn) && move.to().rank() == 0) {
            board.pieceAt(move.to(), whiteQueen);
        }
    }

    public void nextMove() {
        if (finished) return;

        if (board.currentPlayer().isComputer()) {
            Move move = board.currentPlayer().move(board);

            board.play(move);
            logger.info(move.toString());

            ifPawnHasReachedFinalRankReplaceWithQueen(board, move);

            finished = noMoreMovesAllowed(board);
        }

        if (board.currentPlayer().isComputer()) {
            breath();
            SwingUtilities.invokeLater(this::nextMove);
        }
    }

    private void breath() {
        try {
            TimeUnit.SECONDS.sleep(COMPUTER_PAUSE_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void newBoard() {
        board.reset();
    }

    public void configurePlayers() {
        try {
            int game = userOptions.getGameType();
            logger.info("Type " + game + " chosen");

            if (game == 1) {
                board.blackPlayer(new ComputerPlayer("computer", PieceColor.blackSet, userOptions.getLevel("computer", 1), PlayerStrategy.F1));
                board.whitePlayer(new UserPlayer("user", PieceColor.whiteSet));
            } else if (game == 2) {
                board.blackPlayer(new UserPlayer("user", PieceColor.blackSet));
                board.whitePlayer(new ComputerPlayer("computer", PieceColor.whiteSet, userOptions.getLevel("computer", 1), PlayerStrategy.F1));
            } else if (game == 3) {
                board.blackPlayer(new ComputerPlayer("computer 1", PieceColor.blackSet, userOptions.getLevel("computer 1", 1), PlayerStrategy.F1));
                board.whitePlayer(new ComputerPlayer("computer 2", PieceColor.whiteSet, userOptions.getLevel("computer 2", 1), PlayerStrategy.F2));
            } else {
                board.blackPlayer(new UserPlayer("user 1", PieceColor.blackSet));
                board.whitePlayer(new UserPlayer("user 2", PieceColor.whiteSet));
            }
            logger.info("name\ttype");
            logger.info(board.blackPlayer().toString());
            logger.info(board.whitePlayer().toString());

            if (board.currentPlayer().isComputer()) {
                SwingUtilities.invokeLater(this::nextMove);
            }
        } catch (Exception exc) {
            logger.info("Error: " + exc);
            System.exit(-1);
        }
    }
}

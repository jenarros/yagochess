package yagoc;

import yagoc.pieces.PieceColor;
import yagoc.players.ComputerPlayer;
import yagoc.players.Player;
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
        }
    }

    public void saveBoard(String absolutePath) throws IOException {
        try (FileOutputStream fileStream = new FileOutputStream(absolutePath);
             ObjectOutputStream stream = new ObjectOutputStream(fileStream)) {

            stream.writeObject(board);
        }
    }

    public void move(Square from, Square to) {
        Board copy = board.clone();

        if (moveIfPossible(from, to)) {
            checkpoints.add(copy);
        }

        SwingUtilities.invokeLater(this::nextMove);
    }

    public void movePlayer(Player player) {
        if (player.isComputer()) {
            Move move = player.move(board);

            board.play(move);
            logger.info(move.toString());

            board.ifPawnHasReachedFinalRankReplaceWithQueen(move);

            finished = board.noMoreMovesAllowed();
        }
    }

    public boolean moveIfPossible(Square from, Square to) {

        if (finished || !board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
            return false;
        } else if (board.currentPlayer().isUser()) {
            Move move = new Move(board.pieceAt(from), from, to);
            if (!from.equals(to)
                    && board.isCorrectMove(move)
                    && board.moveDoesNotCreateCheck(move)) {

                board.play(move);

                board.ifPawnHasReachedFinalRankReplaceWithQueen(move);

                finished = board.noMoreMovesAllowed();

                logger.info(move.toString());

                return true;
            }
        }

        return false;
    }

    public void nextMove() {
        if (finished) return;

        movePlayer(board.currentPlayer());

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
            // if the player 2 (whiteSet) is a computer then start automatically
            if (board.currentPlayer().isComputer()) {
                SwingUtilities.invokeLater(this::nextMove);
            }
        } catch (Exception exc) {
            logger.info("Error: " + exc);
            System.exit(-1);
        }
    }
}
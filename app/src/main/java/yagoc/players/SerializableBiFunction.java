package yagoc.players;

import yagoc.board.BoardView;
import yagoc.pieces.PieceColor;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface SerializableBiFunction extends BiFunction<BoardView, PieceColor, Integer>, Serializable {
}

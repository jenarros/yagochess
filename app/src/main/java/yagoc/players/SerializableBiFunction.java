package yagoc.players;

import yagoc.board.BoardReader;
import yagoc.pieces.PieceColor;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface SerializableBiFunction extends BiFunction<BoardReader, PieceColor, Integer>, Serializable {
}

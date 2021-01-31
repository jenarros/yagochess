package yagoc.players;

import yagoc.Board;
import yagoc.pieces.PieceColor;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface SerializableBiFunction extends BiFunction<Board, PieceColor, Integer>, Serializable {
}

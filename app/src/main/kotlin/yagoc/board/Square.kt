package yagoc.board;

import yagoc.pieces.PieceColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Square implements Serializable, Comparable<Square> {
    public static List<Square> allSquares = all();

    private final int rank;
    private final int file;

    private static List<Square> all() {
        List<Square> squares = new ArrayList<>();

        for (int rank = 0; rank <= 7; rank++) {
            for (int file = 0; file <= 7; file++) {
                squares.add(new Square(rank, file));
            }
        }

        return squares;
    }

    public Square nextRank(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank - 1, file);
        } else {
            return new Square(rank + 1, file);
        }
    }

    public Square next2Rank(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank - 2, file);
        } else {
            return new Square(rank + 2, file);
        }
    }

    public Square next2File(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank, file + 2);
        } else {
            return new Square(rank, file - 2);
        }
    }

    public Square previous2File(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank, file - 2);
        } else {
            return new Square(rank, file + 2);
        }
    }

    public Square nextRankNextFile(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank - 1, file + 1);
        } else {
            return new Square(rank + 1, file - 1);
        }
    }

    public Square nextRankPreviousFile(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank - 1, file - 1);
        } else {
            return new Square(rank + 1, file + 1);
        }
    }

    public Square previousFile(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank, file - 1);
        } else {
            return new Square(rank, file + 1);
        }
    }

    public Square nextFile(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank, file + 1);
        } else {
            return new Square(rank, file - 1);
        }
    }

    public Square previousRank(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank + 1, file);
        } else {
            return new Square(rank - 1, file);
        }
    }

    public Square previous2Rank(PieceColor pieceColor) {
        if (pieceColor == PieceColor.whiteSet) {
            return new Square(rank + 2, file);
        } else {
            return new Square(rank - 2, file);
        }
    }

    public boolean exists() {
        return this.rank <= 7 && this.rank >= 0 && this.file <= 7 && this.file >= 0;
    }

    public List<Square> diagonalSquares() {
        List<Square> acc = new ArrayList<>();
        for (int distance = 1; distance <= 7; distance++) {
            acc.add(new Square(rank + distance, file + distance));
            acc.add(new Square(rank + distance, file - distance));
            acc.add(new Square(rank - distance, file + distance));
            acc.add(new Square(rank - distance, file - distance));
        }
        return acc.stream().filter((Square::exists)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return rank == square.rank &&
                file == square.file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, file);
    }

    public List<Square> straightSquares() {
        List<Square> acc = new ArrayList<>();
        for (int distance = 1; distance <= 7; distance++) {
            acc.add(new Square(rank + distance, file));
            acc.add(new Square(rank - distance, file));
            acc.add(new Square(rank, file + distance));
            acc.add(new Square(rank, file - distance));
        }
        return acc.stream().filter((Square::exists)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "(" + Move.RANK_NAMES[rank] + ", " + Move.FILE_NAMES[file] + ')';
    }

    public int rank() {
        return rank;
    }

    public int file() {
        return file;
    }


    public Square(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

    /**
     * position of the square in a sequence from 0 to 63
     */
    public Integer arrayPosition() {
        return rank * 8 + file;
    }

    public Squares toSquares() {
        return Squares.Companion.valueOf(arrayPosition());
    }

    @Override
    public int compareTo(Square o) {
        return this.arrayPosition().compareTo(o.arrayPosition());
    }
}

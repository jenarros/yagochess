package yagoc;

import yagoc.pieces.PieceColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Square implements Comparable {
    public static List<Square> allSquares = all();

    private final int rank;
    private final int file;

    public Square(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

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
        return "(" + rank + ", " + file + ')';
    }

    public int rank() {
        return rank;
    }

    public int file() {
        return file;
    }


    static final Square castlingKingsideWhiteFrom = new Square(7, 7);
    static final Square castlingKingsideWhiteTo = new Square(7, 5);

    static final Square castlingQueensideWhiteFrom = new Square(7, 0);
    static final Square castlingQueensideWhiteTo = new Square(7, 3);

    static final Square castlingQueensideBlackFrom = new Square(0, 0);
    static final Square castlingQueensideBlackTo = new Square(0, 3);

    static final Square castlingKingsideBlackFrom = new Square(0, 7);

    static final Square castlingKingsideBlackTo = new Square(0, 5);

    public Integer arrayPosition() {
        return (this.file() * 8) + this.rank();
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Square)) return -1;

        Square other = ((Square) o);

        return this.arrayPosition().compareTo(other.arrayPosition());
    }
}

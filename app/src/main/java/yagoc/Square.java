package yagoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Square {
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

    Square nextRank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank - 1, file);
        } else {
            return new Square(rank + 1, file);
        }
    }

    Square next2Rank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank - 2, file);
        } else {
            return new Square(rank + 2, file);
        }
    }

    Square next2File(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank, file + 2);
        } else {
            return new Square(rank, file - 2);
        }
    }

    Square previous2File(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank, file - 2);
        } else {
            return new Square(rank, file + 2);
        }
    }

    Square nextRankNextFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank - 1, file + 1);
        } else {
            return new Square(rank + 1, file - 1);
        }
    }

    Square nextRankPreviousFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank - 1, file - 1);
        } else {
            return new Square(rank + 1, file + 1);
        }
    }

    Square previousFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank, file - 1);
        } else {
            return new Square(rank, file + 1);
        }
    }

    Square nextFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank, file + 1);
        } else {
            return new Square(rank, file - 1);
        }
    }

    public Square previousRank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(rank + 1, file);
        } else {
            return new Square(rank - 1, file);
        }
    }

    public Square previous2Rank(SetType set) {
        if (set == SetType.whiteSet) {
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

    static final Square castlingKingsideWhiteFrom = new Square(7, 7);
    static final Square castlingKingsideWhiteTo = new Square(7, 5);

    static final Square castlingQueensideWhiteFrom = new Square(7, 0);
    static final Square castlingQueensideWhiteTo = new Square(7, 3);

    static final Square castlingQueensideBlackFrom = new Square(0, 0);
    static final Square castlingQueensideBlackTo = new Square(0, 3);

    static final Square castlingKingsideBlackFrom = new Square(0, 7);
    static final Square castlingKingsideBlackTo = new Square(0, 5);

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }
}

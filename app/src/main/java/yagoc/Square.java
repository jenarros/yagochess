package yagoc;

import java.util.ArrayList;
import java.util.List;

public class Square {
    public static List<Square> allSquares = all();
    final int rank;
    final int file;

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

    public List<Square> otherSquaresInSameFile() {
        List<Square> acc = new ArrayList<>();
        for (int file = 0; file < 8; file++) {
            if (file != this.file) {
                acc.add(new Square(this.rank, file));
            }
        }
        return acc;
    }

    public List<Square> otherSquaresInSameRank() {
        List<Square> acc = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            if (rank != this.rank) {
                acc.add(new Square(rank, this.file));
            }
        }
        return acc;
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
        return acc;
    }

    public List<Square> straightSquares() {
        List<Square> acc = new ArrayList<>();
        for (int distance = 1; distance <= 7; distance++) {
            acc.add(new Square(rank + distance, file));
            acc.add(new Square(rank - distance, file));
            acc.add(new Square(rank, file + distance));
            acc.add(new Square(rank, file - distance));
        }
        return acc;
    }
}

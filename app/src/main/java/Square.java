import java.util.ArrayList;
import java.util.List;

public class Square {
    public static List<Square> allSquares = all();
    final int x;
    final int y;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
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
            return new Square(x - 1, y);
        } else {
            return new Square(x + 1, y);
        }
    }

    Square next2Rank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x - 2, y);
        } else {
            return new Square(x + 2, y);
        }
    }

    Square next2File(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x, y + 2);
        } else {
            return new Square(x, y - 2);
        }
    }

    Square previous2File(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x, y - 2);
        } else {
            return new Square(x, y + 2);
        }
    }

    Square nextRankNextFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x - 1, y + 1);
        } else {
            return new Square(x + 1, y - 1);
        }
    }

    Square nextRankPreviousFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x - 1, y - 1);
        } else {
            return new Square(x + 1, y + 1);
        }
    }

    Square previousFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x, y - 1);
        } else {
            return new Square(x, y + 1);
        }
    }

    Square nextFile(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x, y + 1);
        } else {
            return new Square(x, y - 1);
        }
    }

    public Square previousRank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x + 1, y);
        } else {
            return new Square(x - 1, y);
        }
    }

    public Square previous2Rank(SetType set) {
        if (set == SetType.whiteSet) {
            return new Square(x + 2, y);
        } else {
            return new Square(x - 2, y);
        }
    }

    public List<Square> otherSquaresInSameFile() {
        List<Square> acc = new ArrayList<>();
        for (int file = 0; file < 8; file++) {
            if (file != this.y) {
                acc.add(new Square(this.x, file));
            }
        }
        return acc;
    }

    public List<Square> otherSquaresInSameRank() {
        List<Square> acc = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            if (rank != this.x) {
                acc.add(new Square(rank, this.y));
            }
        }
        return acc;
    }

    public boolean exists() {
        return this.x <= 7 && this.x >= 0 && this.y <= 7 && this.y >= 0;
    }

    public List<Square> diagonalSquares() {
        List<Square> acc = new ArrayList<>();
        for (int distance = 1; distance <= 7; distance++) {
            acc.add(new Square(x + distance, y + distance));
            acc.add(new Square(x + distance, y - distance));
            acc.add(new Square(x - distance, y + distance));
            acc.add(new Square(x - distance, y - distance));
        }
        return acc;
    }

    public List<Square> straightSquares() {
        List<Square> acc = new ArrayList<>();
        for (int distance = 1; distance <= 7; distance++) {
            acc.add(new Square(x + distance, y));
            acc.add(new Square(x - distance, y));
            acc.add(new Square(x, y + distance));
            acc.add(new Square(x, y - distance));
        }
        return acc;
    }
}

package yagoc;

public enum SetType {
    blackSet, whiteSet;

    public SetType next() {
        if (this == blackSet) {
            return whiteSet;
        } else {
            return blackSet;
        }
    }

    public SetType previous() {
        return next();
    }
}

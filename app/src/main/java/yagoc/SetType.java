package yagoc;

enum SetType {
    blackSet, whiteSet;

    SetType next() {
        if (this == blackSet) {
            return whiteSet;
        } else {
            return blackSet;
        }
    }

    SetType previous() {
        return next();
    }
}

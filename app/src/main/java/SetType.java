enum SetType {
    blackSet, whiteSet;

    SetType next() {
        if (this == blackSet) {
            return whiteSet;
        } else {
            return blackSet;
        }
    }
}

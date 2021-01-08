import java.util.Arrays;

enum Piece {
    blackRook(PieceType.rook, SetType.blackSet),
    blackKnight(PieceType.knight, SetType.blackSet),
    blackBishop(PieceType.bishop, SetType.blackSet),
    blackQueen(PieceType.queen, SetType.blackSet),
    blackKing(PieceType.king, SetType.blackSet),
    blackPawn(PieceType.pawn, SetType.blackSet),

    whiteRook(PieceType.rook, SetType.whiteSet),
    whiteKnight(PieceType.knight, SetType.whiteSet),
    whiteBishop(PieceType.bishop, SetType.whiteSet),
    whiteQueen(PieceType.queen, SetType.whiteSet),
    whiteKing(PieceType.king, SetType.whiteSet),
    whitePawn(PieceType.pawn, SetType.whiteSet),

    none(null, null);

    final PieceType type;
    final SetType set;

    Piece(PieceType type, SetType set) {
        this.type = type;
        this.set = set;
    }

    Piece to(PieceType type) {
        return Arrays.stream(values())
                .filter((piece) -> type == piece.type && piece.set == this.set)
                .findFirst().orElseThrow();
    }
}

package yagoc.board

import yagoc.board.MoveLog.Companion.castling
import yagoc.board.MoveLog.Companion.enPassant
import yagoc.board.MoveLog.Companion.normalMove
import yagoc.pieces.*
import yagoc.players.ComputerPlayer
import yagoc.players.Player
import yagoc.players.PlayerStrategy
import yagoc.players.UserPlayer
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Consumer

class Board : BoardView {
    private val moves = Stack<MoveLog>()
    private val squareBoard = SquareBoard()
    private val enPassant = IntArray(8)
    private var whiteLeftRookMoved = false
    private var whiteRightRookMoved = false
    private var whiteKingMoved = false
    private var blackLeftRookMoved = false
    private var blackRightRookMoved = false
    private var blackKingMoved = false
    private var drawCounter = 0
    private var moveCounter = 0
    private var blackPlayer: Player = ComputerPlayer("computer 1", PieceColor.blackSet, 3, PlayerStrategy.F1)
    private var whitePlayer: Player = UserPlayer("user 1", PieceColor.whiteSet)
    private var currentPlayer: Player = whitePlayer

    constructor() {
        reset()
    }

    constructor(board: Board) {
        resetWith(board)
    }

    fun resetWith(board: Board) {
        System.arraycopy(board.squareBoard.pieces, 0, squareBoard.pieces, 0, squareBoard.pieces.size)
        System.arraycopy(board.enPassant, 0, enPassant, 0, enPassant.size)
        currentPlayer = board.currentPlayer
        whiteLeftRookMoved = board.whiteLeftRookMoved
        whiteRightRookMoved = board.whiteRightRookMoved
        whiteKingMoved = board.whiteKingMoved
        blackLeftRookMoved = board.blackLeftRookMoved
        blackRightRookMoved = board.blackRightRookMoved
        blackKingMoved = board.blackKingMoved
        drawCounter = board.drawCounter
        moveCounter = board.moveCounter
        blackPlayer = board.blackPlayer
        whitePlayer = board.whitePlayer
        moves.removeAllElements()
        moves.addAll(board.moves)
    }

    fun reset() {
        Arrays.fill(enPassant, -5)
        blackPlayer = ComputerPlayer("computer 1", PieceColor.blackSet, 3, PlayerStrategy.F1)
        whitePlayer = UserPlayer("user 1", PieceColor.whiteSet)
        currentPlayer = whitePlayer
        squareBoard.reset()
    }

    override fun enPassant(file: Int): Int {
        return enPassant[file]
    }

    override fun currentPlayer(): Player {
        return currentPlayer
    }

    override fun hasWhiteLeftRookMoved(): Boolean {
        return whiteLeftRookMoved
    }

    override fun hasWhiteRightRookMoved(): Boolean {
        return whiteRightRookMoved
    }

    override fun hasWhiteKingMoved(): Boolean {
        return whiteKingMoved
    }

    override fun hasBlackLeftRookMoved(): Boolean {
        return blackLeftRookMoved
    }

    override fun hasBlackRightRookMoved(): Boolean {
        return blackRightRookMoved
    }

    override fun hasBlackKingMoved(): Boolean {
        return blackKingMoved
    }

    override fun drawCounter(): Int {
        return drawCounter
    }

    override fun moveCounter(): Int {
        return moveCounter
    }

    fun blackPlayer(): Player {
        return blackPlayer
    }

    fun whitePlayer(): Player {
        return whitePlayer
    }

    fun whitePlayer(player: Player) {
        if (currentPlayer == whitePlayer) {
            currentPlayer = player
        }
        whitePlayer = player
    }

    override fun isPieceOfCurrentPlayer(piece: Piece): Boolean {
        return piece.color() == currentPlayer.pieceColor()
    }

    fun togglePlayer() {
        currentPlayer = if (currentPlayer == blackPlayer) {
            whitePlayer
        } else {
            blackPlayer
        }
    }

    fun blackPlayer(player: Player) {
        if (currentPlayer == blackPlayer) {
            currentPlayer = player
        }
        blackPlayer = player
    }

    override fun pieceAt(square: Square): Piece {
        return squareBoard[square.arrayPosition()]
    }

    fun pieceAt(square: Square, newPiece: Piece) {
        squareBoard[square.arrayPosition()] = newPiece
    }

    override fun oppositePlayer(): Player {
        return if (currentPlayer == whitePlayer) {
            blackPlayer
        } else {
            whitePlayer
        }
    }

    override fun toPrettyString(): String {
        val buffer = StringBuilder()
        Square.allSquares.forEach(Consumer { square: Square ->
            val piece = pieceAt(square)
            buffer.append(piece.toUniqueChar())
            if (square.file() % 8 == 7 && square.rank() < 7) buffer.append("\n")
        })
        return buffer.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Board
        return whiteLeftRookMoved == that.whiteLeftRookMoved && whiteRightRookMoved == that.whiteRightRookMoved && whiteKingMoved == that.whiteKingMoved && blackLeftRookMoved == that.blackLeftRookMoved && blackRightRookMoved == that.blackRightRookMoved && blackKingMoved == that.blackKingMoved && drawCounter == that.drawCounter && moveCounter == that.moveCounter && moves == that.moves &&
                squareBoard == that.squareBoard &&
                enPassant.contentEquals(that.enPassant) && currentPlayer == that.currentPlayer && blackPlayer == that.blackPlayer && whitePlayer == that.whitePlayer
    }

    override fun <T> playAndUndo(move: Move, callable: Callable<T>): T {
        play(move)
        return try {
            val moveValue = callable.call()
            undo()
            moveValue
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun playAndUndo(from: Square, to: Square): Board {
        play(from, to)
        undo()
        return this
    }

    fun play(from: Square, to: Square): Board {
        return play(Move(pieceAt(from), from, to))
    }

    private fun updateMovedPieces(move: Move) {
        if (move.fromPiece() == whiteKing) whiteKingMoved =
            true else if (move.fromPiece() == whiteRook && move.from().file() == 0) whiteLeftRookMoved =
            true else if (move.fromPiece() == whiteRook && move.from().file() == 7) whiteRightRookMoved = true
        if (move.fromPiece() == blackKing) blackKingMoved =
            true else if (move.fromPiece() == blackRook && move.from().file() == 0) blackLeftRookMoved =
            true else if (move.fromPiece() == blackRook && move.from().file() == 7) blackRightRookMoved = true
    }

    fun undo() {
        val moveLog = moves.pop()
        when {
            moveLog.type === MoveType.normal -> {
                pieceAt(moveLog.move.from(), moveLog.move.fromPiece())
                pieceAt(moveLog.move.to(), moveLog.toPiece)
            }
            moveLog.type === MoveType.enPassant -> {
                pieceAt(moveLog.move.from(), moveLog.move.fromPiece())
                pieceAt(moveLog.move.to(), moveLog.toPiece)
                moveLog.enPassantPiece?.let { pieceAt(moveLog.move.enPassantSquare(), it) }
            }
            moveLog.type === MoveType.castling -> {
                pieceAt(moveLog.move.from(), moveLog.move.fromPiece())
                pieceAt(moveLog.move.to(), moveLog.toPiece)
                moveLog.castlingExtraMove?.let {
                    pieceAt(moveLog.castlingExtraMove.from(), moveLog.castlingExtraMove.fromPiece())
                    pieceAt(moveLog.castlingExtraMove.to(), none)
                }
            }
        }
        whiteLeftRookMoved = moveLog.whiteLeftRookMoved
        whiteRightRookMoved = moveLog.whiteRightRookMoved
        whiteKingMoved = moveLog.whiteKingMoved
        blackLeftRookMoved = moveLog.blackLeftRookMoved
        blackRightRookMoved = moveLog.blackRightRookMoved
        blackKingMoved = moveLog.blackKingMoved
        enPassant[moveLog.move.to().file()] = moveLog.enPassant
        drawCounter = moveLog.drawCounter
        moveCounter = moveLog.moveCounter
        togglePlayer()
    }

    fun playCastlingExtraMove(move: Move): MoveLog {
        val castlingExtraMove: Move
        if (move.isCastlingQueenside) {
            if (move.fromPiece().color() == PieceColor.whiteSet) {
                castlingExtraMove = Move(whiteRook, a1Square, d1Square)
                pieceAt(a1Square, none)
                pieceAt(d1Square, whiteRook)
            } else {
                castlingExtraMove = Move(blackRook, a8Square, d8Square)
                pieceAt(a8Square, none)
                pieceAt(d8Square, blackRook)
            }
        } else if (move.fromPiece().color() == PieceColor.whiteSet) {
            castlingExtraMove = Move(whiteRook, h1Square, f1Square)
            pieceAt(h1Square, none)
            pieceAt(f1Square, whiteRook)
        } else {
            castlingExtraMove = Move(blackRook, h8Square, f8Square)
            pieceAt(h8Square, none)
            pieceAt(f8Square, blackRook)
        }
        return castling(this, move, pieceAt(move.to()), castlingExtraMove)
    }

    fun play(move: Move): Board {
        val moveLog: MoveLog
        if (move.isCastling) {
            moveLog = playCastlingExtraMove(move)
            drawCounter++
        } else {
            // if pawn advances 2 squares, it is possible that next move is a en passant capture
            if (move.fromPiece().pieceType() == PieceType.Pawn && move.rankDistanceAbs() == 2) {
                enPassant[move.to().file()] = moveCounter
            }
            // en passant capture
            if (move.fromPiece()
                    .pieceType() == PieceType.Pawn && move.rankDistance() == 1 && move.fileDistanceAbs() == 1 && enPassant[move.to()
                    .file()] == moveCounter - 1
            ) {
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveLog = enPassant(this, move, pieceAt(move.to()))
                pieceAt(moveLog.move.enPassantSquare(), none)
            } else {
                moveLog = normalMove(this, move, pieceAt(move.to()))
            }
            if (!noneAt(move.to()) || move.fromPiece().pieceType() == PieceType.Pawn) {
                // draw counter restarts when we capture a piece or move a pawn
                drawCounter = 0
            } else {
                drawCounter++
            }
        }
        updateMovedPieces(move)
        pieceAt(move.from(), none)
        pieceAt(move.to(), move.fromPiece())
        togglePlayer()
        moveCounter++
        moves.add(moveLog)
        return this
    }

    override fun hashCode(): Int {
        var result = Objects.hash(
            moves,
            currentPlayer,
            whiteLeftRookMoved,
            whiteRightRookMoved,
            whiteKingMoved,
            blackLeftRookMoved,
            blackRightRookMoved,
            blackKingMoved,
            drawCounter,
            moveCounter,
            blackPlayer,
            whitePlayer
        )
        result = 31 * result + squareBoard.hashCode()
        result = 31 * result + enPassant.contentHashCode()
        return result
    }

    companion object {
        fun parseBoard(stringBoard: String): Board {
            val cleanStringBoard = stringBoard.replace("[ \t\n]".toRegex(), "")
            val board = Board()
            Square.allSquares.forEach(Consumer { square: Square ->
                board.pieceAt(
                    square, Pieces.parse(
                        cleanStringBoard[square.arrayPosition()]
                    )
                )
            })
            return board
        }
    }
}
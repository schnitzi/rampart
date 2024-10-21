package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.Piece
import org.computronium.chess.core.PieceType
import kotlin.system.exitProcess

open class CaptureTransform(private val captureIndex : Int) : Transform {

    private var capturedPiece: Piece? = null

    private var halfMovesSinceCaptureOrPawnAdvance: Int = 0

    private var opponentCouldCastleKingSide: Boolean = false
    private var opponentCouldCastleQueenSide: Boolean = false

    override fun apply(boardState: BoardState) {

        val opponentSideData = boardState.sideData[1 - boardState.whoseTurn]
        opponentCouldCastleKingSide = opponentSideData.canKingSideCastle
        opponentCouldCastleQueenSide = opponentSideData.canQueenSideCastle

        capturedPiece = boardState[captureIndex]

        if (capturedPiece!!.type == PieceType.KING) {
            System.err.println("King is captured.  Something has gone awry.")
            exitProcess(-1)
        }

        // See if rook was captured, which should disable castling.
        if (opponentSideData.homeRankStart == captureIndex) {
            opponentSideData.canQueenSideCastle = false
        } else if (opponentSideData.homeRankStart + 7 == captureIndex) {
            opponentSideData.canKingSideCastle = false
        }

        boardState[captureIndex] = null

        halfMovesSinceCaptureOrPawnAdvance = boardState.halfMovesSinceCaptureOrPawnAdvance

        boardState.halfMovesSinceCaptureOrPawnAdvance = 0
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance = halfMovesSinceCaptureOrPawnAdvance

        boardState[captureIndex] = capturedPiece

        val opponentSideData = boardState.sideData[1 - boardState.whoseTurn]
        opponentSideData.canKingSideCastle = opponentCouldCastleKingSide
        opponentSideData.canQueenSideCastle = opponentCouldCastleQueenSide
    }
}

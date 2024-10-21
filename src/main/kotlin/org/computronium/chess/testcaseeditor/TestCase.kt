package org.computronium.chess.testcaseeditor

import com.google.gson.annotations.Expose
import org.computronium.chess.core.BoardState

class TestCase(@Expose val description: String?,
               @Expose val start: TestCasePosition,
               @Expose val expected: List<TestCasePosition>) {


    class TestCasePosition(description: String? = null, @Expose val move: String?, @Expose val fen: String, private var modified: Boolean = false) {

        @Expose
        var description: String? = description
            set(value) {
                modified = true
                field = value
            }

        @Transient
        private var state: BoardState? = null

        override fun toString(): String {
            return (if (move != null) "$move -> " else "") + fen
        }

        fun moveLabel(): String {
            return "After ${getBoardState().moveNumber}. ${if (getBoardState().whoseTurn == BoardState.WHITE) "... " else ""}${move ?: "??"}\n"
        }

        fun getBoardState(): BoardState {
            if (state == null) state = BoardState.fromFEN(fen)
            return state!!
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestCasePosition

            if (move != other.move) return false
            if (fen != other.fen) return false
            if (description != other.description) return false

            return true
        }

        override fun hashCode(): Int {
            var result = move?.hashCode() ?: 0
            result = 31 * result + fen.hashCode()
            result = 31 * result + (description?.hashCode() ?: 0)
            return result
        }
    }

    override fun toString(): String {
        return start.fen + (if (description == null) "" else " ($description)")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestCase

        if (description != other.description) return false
        if (start != other.start) return false
        if (expected != other.expected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + start.hashCode()
        result = 31 * result + expected.hashCode()
        return result
    }
}

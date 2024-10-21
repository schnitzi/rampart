package org.computronium.chess

import org.computronium.chess.core.GameRunner
import org.computronium.chess.testcaseeditor.TestCaseGroup
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * An example test class tests the built-in move generator against the full set of test files.
 */
class TestMoveGeneratorKotlin {

    @Test
    fun runTest() {

        val testFileDir = File("./src/main/resources/testcases")
        val testFiles = testFileDir.listFiles()
        if (testFiles == null) {
            Assert.fail("No test files found.")
            return
        }
        for (testFile in testFiles.filter { file -> file.name.endsWith(".json") }) {
            doTest(testFile)
        }

    }

    private fun doTest(testFile: File) {
        val testCaseGroup = TestCaseGroup.fromFile(testFile)

        println("Testing file $testFile (${testCaseGroup})")

        for (testCase in testCaseGroup.testCases) {

            println("  Testing case '${testCase}'")

            val expectedMoves = testCase.expected.associateBy({ it.move.toString() }, { it.fen })

            val runner = GameRunner.fromFEN(testCase.start.fen)
            val startPos = runner.generateSearchNode()
            val boardState = startPos.boardState
            val actualMoves = startPos.moves.associateBy({ it.toString() }, {

                // Do the move and save the resulting FEN.
                it.apply(boardState)
                val fen = boardState.toFEN()

                // Rollback the move we just applied.
                it.rollback(boardState)

                // Confirm that the rollback works by checking we still match the original FEN.
                Assert.assertEquals("Rollback failed", testCase.start.fen, boardState.toFEN())

                fen
            })

            if (expectedMoves != actualMoves) {

                if (expectedMoves.keys != actualMoves.keys) {
                    println("Moves: Expected but didn't find: ${expectedMoves.keys.filter { !actualMoves.keys.contains(it) }}")
                    println("Moves: Found but didn't expect: ${actualMoves.keys.filter { !expectedMoves.keys.contains(it) } }")
                }

                println("FENs: Expected but didn't find: ${expectedMoves.values.filter { !actualMoves.values.contains(it) }}")
                println("FENs: Found but didn't expect: ${actualMoves.values.filter { !expectedMoves.values.contains(it) }}")
            }

            Assert.assertEquals("Did not match expected board states", expectedMoves, actualMoves)
        }
    }
}

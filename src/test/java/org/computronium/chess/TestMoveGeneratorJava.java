package org.computronium.chess;

import org.computronium.chess.core.BoardState;
import org.computronium.chess.core.GameRunner;
import org.computronium.chess.core.SearchNode;
import org.computronium.chess.core.moves.AlgebraicMoveNameGenerator;
import org.computronium.chess.core.moves.Move;
import org.computronium.chess.testcaseeditor.TestCase;
import org.computronium.chess.testcaseeditor.TestCase.TestCasePosition;
import org.computronium.chess.testcaseeditor.TestCaseGroup;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class TestMoveGeneratorJava {
    @Test
    public void runTest() {
        File testFileDir = new File("./src/main/resources/testcases");
        File[] testFiles = testFileDir.listFiles();
        if (testFiles == null) {
            Assert.fail("No test files found.");
            return;
        }

        for (File testFile : testFiles) {
            if (testFile.getName().endsWith(".json")) {
                doTest(testFile);
            }
        }
    }

    private void doTest(File testFile) {
        TestCaseGroup testCaseGroup = TestCaseGroup.Companion.fromFile(testFile);

        System.out.println("Testing file " + testFile + " (" + testCaseGroup + ")");

        for (TestCase testCase : testCaseGroup.getTestCases()) {

            System.out.println("  Testing case '" + testCase + "'");

            final Map<String, String> expectedMoves = new HashMap<>();
            for (TestCasePosition position : testCase.getExpected()) {
                expectedMoves.put(position.getMove(), position.getFen());
            }

            GameRunner runner = GameRunner.Companion.fromFEN(testCase.getStart().getFen(), new AlgebraicMoveNameGenerator());
            SearchNode startPos = runner.generateSearchNode();
            final Map<String, String> actualMoves = new HashMap<>();
            for (Move move : startPos.getMoves()) {

                // Do the move and save the resulting FEN.
                BoardState boardState = startPos.getBoardState();
                move.apply(boardState);
                actualMoves.put(move.toString(), boardState.toFEN());

                // Rollback the move we just applied.
                move.rollback(boardState);

                // Confirm that the rollback works by checking we still match the original FEN.
                Assert.assertEquals("Rollback failed", testCase.getStart().getFen(), boardState.toFEN());
            }

            if (!expectedMoves.equals(actualMoves)) {
                if (expectedMoves.keySet() != actualMoves.keySet()) {
                    System.out.println("Moves: Expected but didn't find: " + expectedMoves.keySet().stream().filter(key -> !actualMoves.containsKey(key)));
                    System.out.println("Moves: Found but didn't expect: " + actualMoves.keySet().stream().filter(key -> !expectedMoves.containsKey(key)));
                }

                System.out.println("FENs: Expected but didn't find: " + expectedMoves.values().stream().filter(value -> !actualMoves.containsValue(value)));
                System.out.println("FENs: Found but didn't expect: " + actualMoves.values().stream().filter(value -> !expectedMoves.containsValue(value)));
            }

            Assert.assertEquals("Did not match expected board states", expectedMoves, actualMoves);
        }
    }
}

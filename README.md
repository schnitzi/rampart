**Rampart** is a set of extreme test data for testing your chess move generator.

># Dive right in!
>
> If you're here looking for the nasty data files containing [FENs](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation)
> to test your chess move generator with, they can be found [here](src/main/resources/testcases).
>
> I recommend you read the description below however and consider
> implementing the [three kinds of recommended tests](#Using-the-test-data-files-to-test-your-move-generator).
>
> There are example unit tests (which test the included move generator) using 
> these files are included for [Java](src/test/java/org/computronium/chess/TestMoveGeneratorJava.java) and [Kotlin](src/test/kotlin/org/computronium/chess/TestMoveGeneratorKotlin.kt).


# Overview

An important component of any chess engine is the move generator,
the algorithm that is capable of generating all the legal chess
moves that a player can make from a given position.  The algorithm
itself is reasonably straightforward, but there are a number of
subtleties that arise in extreme circumstances or because the rules
are sometimes poorly understood.

This project grew out of my failure to find a good set of test data
for a chess move generator I was writing.  I created some test data
by hand, but it was a laborious process, and I found myself repeatedly
wishing for something more complete and battle-tested.  So I decided
to make my own full data set, and the easiest way to do that was to
write a tool.

This project contains:

1.  An extensive test data set for stress testing your chess move generator.

2.  Example tests that use the test data files to test an included example move generator.

3.  A graphical tool for creating, editing, and viewing these test data files, which also uses the move generator to generate test cases.

This project does NOT contain:

1.  A chess program that you can play against.

## The test data sets

You can use the data set contained in this project to test your own
move generator, without ever bothering to compile and run the
test data editing tool or the included tests.  The test data files
contain a number of scenarios, including extreme ones, that your move
generator should be able to handle.

The test data files are located in [src/main/resources/testcases](src/main/resources/testcases).
The files are split by category -- castling, pawn moves, high piece numbers,
etc., each containing multiple test cases.  Each test case consists
of a starting board position, and the set of resulting board positions
that your move generator should generate.  Board positions are
specified in FEN (https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation)
format, and all the test cases represent valid board positions that
can be reached in a standard game of chess.

So to use these data files, you will need the ability to parse JSON
(there are a number of libraries that will help you do that, for
most modern languages), and the ability to convert a FEN into your
internal board representation (and vice versa, which I promise will
be handy for other purposes later).  As there is no particular order
to the resulting moves, it is recommended that you generate moves into
a Set class, so you can just check that `actual` equals `expected`.

There are test files for positions involving checkmates and stalemates.
Most move generators will and should find no legal moves for
these test cases, but depending on how you write your chess engine, your
move generator might never encounter this situation -- you can skip these
tests if so.  

As this project contains its own move generator, I've included
sample tests (in Java and Kotlin) that test it against the test
data files.  As the test data files were generated using this same
engine, the tests will of course pass!  But the tests demonstrate
how you can set up similar tests for your chess engine, as described
below.

## Using the test data files to test your move generator

### Type 1: Finding all moves

Your tests can run through the included data files, and for each
test case, compare the expected set of moves (contained in the data
file as FENs) against the set of moves that your move generator
generates.  Your move generator should generate the exact same set of
resulting boards in all cases -- no more, and no less.  If there is any
discrepancy, you probably have a bug in your move generator.  (Or,
possibly, there's a bug in the test data -- please let me know, so I can
fix it!)

### Type 2: Algebraic move names

Your move generator may or may not generate the actual move names
(such as "Qe4").  If it does, you can test your move name generation
using these same test data files -- all of them include the move
names (in algebraic format) that your move generator should
generate.  Note this is a weaker test than testing against the
resulting FEN positions, because the FEN positions include various
flags that more fully represent the board state.

Moves that result in a checkmate do in fact generate the standard
algebraic checkmate symbol `#` at the end of their move names, but
in many move generators this is extra baggage that is not worth
checking for at the move generation stage, so adjust your tests
accordingly.  There is no standard algebraic symbol for stalemate,
so you won't be able to test for that via the move name.

### Type 3: Move rollbacks

In addition to testing that your move generator produces the
correct set of moves going forward from a position, your chess
engine might also include an "undo" feature that rolls a move
backwards.  Very often, chess engines have bugs in their undo
features, where pieces get put back wrong, 'already castled' flags
don't get unset, etc.  You can test your undo feature easily enough
by performing the undo for each test case and seeing if you end up with
a FEN that matches the starting position.  The included sample tests
contain just such a rollback test for every test case.

# The graphical tool (that you can ignore)

The included test files are usable on their own for testing your move
generator.  These were generated using a small graphical editor, the
code for which is included in the project but entirely unnecessary for
your testing.  It can be used to build up your own test cases, either
by editing the existing test case files or creating new ones.

![FEN test file editor](doc/editor.png)

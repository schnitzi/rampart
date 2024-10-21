package org.computronium.chess.testcaseeditor

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.streams.toList

class TestCaseGroup(description: String?, testCases: List<TestCase> = listOf(), var modified: Boolean = false) {

    @Expose
    var description: String? = description
        set(value) {
            if (value != field) {
                modified = true
                field = value
            }
        }

    @Expose
    var testCases: List<TestCase> = testCases
        set(value) {
            modified = true
            field = value
        }

    operator fun get(index: Int) : TestCase {
        return testCases[index]
    }

    fun getSize() = testCases.size

    fun add(testCase: TestCase) {
        testCases = if (contains(testCase.start.fen)) {
            testCases.stream().map { if (it.start.fen == testCase.start.fen) testCase else it }.toList()
        } else {
            testCases + testCase
        }
    }

    fun expectedFENsAt(index: Int) : List<TestCase.TestCasePosition> {
        return testCases[index].expected
    }

    fun remove(index: Int) {
        val mutableTestCases = testCases.toMutableList()
        mutableTestCases.removeAt(index)
        testCases = mutableTestCases.toList()
    }

    fun contains(newFEN: String): Boolean {
        return testCases.stream().anyMatch { it.start.fen == newFEN }
    }

    override fun toString(): String {
        return description ?: "no description"
    }

    companion object {
        fun fromFile(file: File) : TestCaseGroup {
            val jsonString: String = file.readText(Charsets.UTF_8)
            val testCaseType = object : TypeToken<TestCaseGroup>() {}.type
            return Gson().fromJson(jsonString, testCaseType)
        }
    }
}

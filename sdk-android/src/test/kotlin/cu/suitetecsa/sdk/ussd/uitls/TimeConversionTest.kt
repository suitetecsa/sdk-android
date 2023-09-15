package cu.suitetecsa.sdk.ussd.uitls

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeConversionTest {

    @Test
    fun testToTimeStringWhenGivenKnownValueThenReturnCorrectTimeString() {
        val input: Long = 3661
        val expectedOutput = "01:01:01"
        val actualOutput = input.toTimeString()
        assertEquals("Expected and actual output are not the same", expectedOutput, actualOutput)
    }

    @Test
    fun testToTimeStringWhenGivenZeroThenReturnZeroTimeString() {
        val input: Long = 0
        val expectedOutput = "00:00:00"
        val actualOutput = input.toTimeString()
        assertEquals("Expected and actual output are not the same", expectedOutput, actualOutput)
    }

    @Test
    fun testToTimeStringWhenGivenLargeValueThenReturnCorrectTimeString() {
        val input: Long = 360000
        val expectedOutput = "100:00:00"
        val actualOutput = input.toTimeString()
        assertEquals("Expected and actual output are not the same", expectedOutput, actualOutput)
    }
}

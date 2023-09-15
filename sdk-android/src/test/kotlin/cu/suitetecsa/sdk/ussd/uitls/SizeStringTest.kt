package cu.suitetecsa.sdk.ussd.uitls

import org.junit.Assert.assertEquals
import org.junit.Test

class SizeStringTest {

    @Test
    fun testToSizeStringWhenInputIsBytesThenOutputIsInCorrectUnit() {
        val sizeInBytes = 1024.0
        val expectedOutput = "1,00 KB"
        val actualOutput = sizeInBytes.toSizeString()
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testToSizeStringWhenInputIsMaxSizeOfUnitThenOutputIsCorrect() {
        val sizeInBytes = 1024.0 * 1024.0 // 1 MB
        val expectedOutput = "1,00 MB"
        val actualOutput = sizeInBytes.toSizeString()
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun testToSizeStringWhenInputIsZeroThenOutputIsZeroBytes() {
        val sizeInBytes = 0.0
        val expectedOutput = "0,00 bytes"
        val actualOutput = sizeInBytes.toSizeString()
        assertEquals(expectedOutput, actualOutput)
    }
}

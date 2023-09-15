package cu.suitetecsa.sdk.ussd.uitls

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class StringExtensionTest {

    @Test
    fun testToBytesWhenSizeInKbThenReturnBytes() {
        val sizeInKb = "1024 KB"
        val expectedBytes = 1024.0 * 1024
        val actualBytes = sizeInKb.toBytes()
        assertEquals(expectedBytes, actualBytes, 0.0)
    }

    @Test
    fun testToBytesWhenSizeInMbThenReturnBytes() {
        val sizeInMb = "1 MB"
        val expectedBytes = 1024.0 * 1024
        val actualBytes = sizeInMb.toBytes()
        assertEquals(expectedBytes, actualBytes, 0.0)
    }

    @Test
    fun testToBytesWhenSizeInGbThenReturnBytes() {
        val sizeInGb = "1 GB"
        val expectedBytes = 1024.0 * 1024 * 1024
        val actualBytes = sizeInGb.toBytes()
        assertEquals(expectedBytes, actualBytes, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testToBytesWhenUnitInvalidThenThrowIllegalArgumentException() {
        val sizeWithInvalidUnit = "1024 TB"
        sizeWithInvalidUnit.toBytes()
    }

    @Test
    fun testToSecondsWhenOneHourThenReturns3600() {
        val time = "01:00:00"
        val expected = 3600L
        val actual = time.toSeconds()
        assertEquals(expected, actual)
    }

    @Test
    fun testToSecondsWhenOneMinuteThenReturns60() {
        val time = "00:01:00"
        val expected = 60L
        val actual = time.toSeconds()
        assertEquals(expected, actual)
    }

    @Test
    fun testToSecondsWhenOneSecondThenReturns1() {
        val time = "00:00:01"
        val expected = 1L
        val actual = time.toSeconds()
        assertEquals(expected, actual)
    }

    @Test
    fun testToSecondsWhenOneHourOneMinuteOneSecondThenReturns3661() {
        val time = "01:01:01"
        val expected = 3661L
        val actual = time.toSeconds()
        assertEquals(expected, actual)
    }

    @Test
    fun testToDateWhenStringIsInCorrectFormatThenReturnDate() {
        val dateString = "12/12/2020"
        val expectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)
        val actualDate = dateString.toDate()
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testToCubacelDateWhenStringIsInCorrectFormatThenReturnDate() {
        val dateString = "12-12-21"
        val expectedDate = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(dateString)
        val actualDate = dateString.toCubacelDate()
        assertEquals(expectedDate, actualDate)
    }
}

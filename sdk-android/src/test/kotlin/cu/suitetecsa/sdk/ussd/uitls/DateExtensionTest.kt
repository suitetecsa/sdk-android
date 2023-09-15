package cu.suitetecsa.sdk.ussd.uitls

import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class DateExtensionTest {
    @Test
    fun testDaysBetweenDatesWhenDateIsInFutureThenReturnCorrectDifference() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 5)
        val futureDate = calendar.time

        val expectedDifference = 5
        val actualDifference = futureDate.daysBetweenDates()

        Assert.assertEquals(expectedDifference, actualDifference)
    }

    @Test
    fun testDaysBetweenDatesWhenDateIsInPastThenReturnNegativeDifference() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val pastDate = calendar.time

        val expectedDifference = -4
        val actualDifference = pastDate.daysBetweenDates()

        Assert.assertEquals(expectedDifference, actualDifference)
    }
}

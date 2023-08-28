package cu.suitetecsa.sdk.ussd.uitls

import cu.suitetecsa.sdk.ussd.model.UssdResponse
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class UssdResponseExtensionTest {

    @Test
    fun testParseBonusBalanceWhenBonusCreditInMessageThenReturnsCorrectBonusCredit() {
        val ussdResponse = UssdResponse("$1662.06 vence 06-06-23.")
        val bonusBalance = ussdResponse.parseBonusBalance()

        assertEquals(1662.06f, bonusBalance.credit.credit)
        assertEquals("06-06-23", bonusBalance.credit.bonusCreditDueDate)
    }

    @Test
    fun testParseBonusBalanceWhenBonusDataInMessageThenReturnsCorrectBonusData() {
        val ussdResponse = UssdResponse("Datos: 27.63 GB vence 06-06-23.")
        val bonusBalance = ussdResponse.parseBonusBalance()

        assertEquals("27.63 GB".toBytes(), bonusBalance.data.bonusDataCount)
        assertEquals("06-06-23", bonusBalance.data.bonusDataDueDate)
    }

    @Test
    fun testParseBonusBalanceWhenBonusDataCuInMessageThenReturnsCorrectBonusDataCu() {
        val ussdResponse = UssdResponse( "Datos.cu 175 MB vence 06-06-23.")
        val bonusBalance = ussdResponse.parseBonusBalance()

        assertEquals("175 MB".toBytes(), bonusBalance.dataCu.bonusDataCuCount)
        assertEquals("06-06-23", bonusBalance.dataCu.bonusDataCuDueDate)
    }

    @Test
    fun testParseBonusBalanceWhenUnlimitedDataInMessageThenReturnsCorrectUnlimitedData() {
        val ussdResponse = UssdResponse("Datos: ilimitados vence 29-05-23.")
        val bonusBalance = ussdResponse.parseBonusBalance()

        assertEquals("29-05-23", bonusBalance.unlimitedData.bonusUnlimitedDataDueDate)
    }

    @Test
    fun testParseBonusBalanceWhenNoBonusInfoInMessageThenReturnsDefaultBonusBalance() {
        val ussdResponse = UssdResponse("No bonus information.")
        val bonusBalance = ussdResponse.parseBonusBalance()

        assertEquals(null, bonusBalance.credit.credit)
        assertEquals(null, bonusBalance.credit.bonusCreditDueDate)
        assertEquals(null, bonusBalance.data.bonusDataCount)
        assertEquals(null, bonusBalance.data.bonusDataDueDate)
        assertEquals(null, bonusBalance.dataCu.bonusDataCuCount)
        assertEquals(null, bonusBalance.dataCu.bonusDataCuDueDate)
        assertEquals(null, bonusBalance.unlimitedData.bonusUnlimitedDataDueDate)
    }

    @Test
    fun testParseMainBalanceWhenMessageContainsAllInfoThenReturnsCorrectMainBalance() {
        val message = "Saldo: 25.00 CUP. " +
                "Datos: 3.50 GB + 1.24 GB LTE. " +
                "Voz: 07:38:18. " +
                "SMS: 452. " +
                "Linea activa hasta 13-04-24 vence 13-05-24."
        val ussdResponse = UssdResponse(message)

        val mainBalance = ussdResponse.parseMainBalance()

        assertEquals(3.758096384E9, mainBalance.data.data)
        assertEquals(1.33143986176E9, mainBalance.data.dataLte)
        assertEquals(27_498L, mainBalance.voice.mainVoice)
        assertEquals(452, mainBalance.sms.mainSms)
        assertEquals(25.00f, mainBalance.credit)
        assertEquals("13-04-24", mainBalance.activeUntil)
        assertEquals("13-05-24", mainBalance.mainBalanceDueDate)
    }

    @Test
    fun testParseMainBalanceWhenMessageIsMissingInfoThenReturnsMainBalanceWithNulls() {
        val message =
            "Saldo: 25.00 CUP. Datos: 3.50 GB. Voz: 07:38:18. Linea activa hasta 13-04-24 vence 13-05-24."
        val ussdResponse = UssdResponse(message)

        val mainBalance = ussdResponse.parseMainBalance()

        assertEquals(3.758096384E9, mainBalance.data.data)
        Assert.assertNull(mainBalance.data.dataLte)
        assertEquals(27_498L, mainBalance.voice.mainVoice)
        Assert.assertNull(mainBalance.sms.mainSms)
        assertEquals(25.00f, mainBalance.credit)
        assertEquals("13-04-24", mainBalance.activeUntil)
        assertEquals("13-05-24", mainBalance.mainBalanceDueDate)
    }

    @Test
    fun testParseMainBalanceWhenMessageIsEmptyThenReturnsMainBalanceWithAllNulls() {
        val ussdResponse = UssdResponse("")

        val mainBalance = ussdResponse.parseMainBalance()

        Assert.assertNull(mainBalance.data.data)
        Assert.assertNull(mainBalance.data.dataLte)
        Assert.assertNull(mainBalance.voice.mainVoice)
        Assert.assertNull(mainBalance.sms.mainSms)
        Assert.assertNull(mainBalance.credit)
        Assert.assertNull(mainBalance.activeUntil)
        Assert.assertNull(mainBalance.mainBalanceDueDate)
    }

    @Test
    fun testParseMainDataWhenValidUssdResponseThenCorrectMainData() {
        val ussdResponse = UssdResponse("Tarifa: Activa. Paquetes: 2 GB + 1 GB LTE validos 30 dias.")
        val mainData = ussdResponse.parseMainData()

        assertEquals(true, mainData.usageBasedPricing)
        assertEquals(2.147483648E9, mainData.data)
        assertEquals(1.073741824E9, mainData.dataLte)
        assertEquals(30, mainData.remainingDays)
    }

    @Test
    fun testParseMainDataWhenInvalidUssdResponseThenNullMainData() {
        val ussdResponse = UssdResponse("Invalid USSD response message.")
        val mainData = ussdResponse.parseMainData()

        assertEquals(false, mainData.usageBasedPricing)
        Assert.assertNull(mainData.data)
        Assert.assertNull(mainData.dataLte)
        Assert.assertNull(mainData.remainingDays)
    }

    @Test
    fun testParseMainDataWhenNoMainDataInUssdResponseThenNullMainData() {
        val ussdResponse = UssdResponse("Tarifa: Activa.")
        val mainData = ussdResponse.parseMainData()

        assertEquals(true, mainData.usageBasedPricing)
        Assert.assertNull(mainData.data)
        Assert.assertNull(mainData.dataLte)
        Assert.assertNull(mainData.remainingDays)
    }

    @Test
    fun testParseMainDataWhenNoActiveMainDataThenRemainingDaysNull() {
        val ussdResponse = UssdResponse("Tarifa: No activa. " +
                "Mensajeria: 600 MB validos 30 dias. " +
                "Diaria: 200 MB no activos. " +
                "Paquetes: 4 GB + 3 GB LTE no activos.")
        val mainData = ussdResponse.parseMainData()

        assertEquals(false, mainData.usageBasedPricing)
        assertEquals(4.294967296E9, mainData.data)
        assertEquals(3.221225472E9, mainData.dataLte)
        Assert.assertNull(mainData.remainingDays)
    }

    @Test
    fun testParseMainSmsWhenMessageContainsSmsAndDaysThenReturnsCorrectValues() {
        val ussdResponse = UssdResponse("Usted dispone de 50 SMS validos por 10 dias")
        val mainSms = ussdResponse.parseMainSms()

        assertEquals(50, mainSms.mainSms)
        assertEquals(10, mainSms.remainingDays)
    }

    @Test
    fun testParseMainSmsWhenMessageDoesNotContainSmsAndDaysThenReturnsNullValues() {
        val ussdResponse = UssdResponse("Usted no dispone de SMS")
        val mainSms = ussdResponse.parseMainSms()

        Assert.assertNull(mainSms.mainSms)
        Assert.assertNull(mainSms.remainingDays)
    }

    @Test
    fun testParseMainSmsWhenNotActiveMessageThenRemainingDaysReturnsNullValue() {
        val ussdResponse = UssdResponse("Usted dispone de 50 SMS no activos")
        val mainSms = ussdResponse.parseMainSms()

        assertEquals(50, mainSms.mainSms)
        Assert.assertNull(mainSms.remainingDays)
    }

    @Test
    fun testParseMainVoiceWhenMessageMatchesPatternThenReturnsCorrectMainVoice() {
        val ussdResponse = UssdResponse("Usted dispone de 07:37:49 MIN validos por 5 dias")
        val mainVoice = ussdResponse.parseMainVoice()

        assertEquals(27_469L, mainVoice.mainVoice)
        assertEquals(5, mainVoice.remainingDays)
    }

    @Test
    fun testParseMainVoiceWhenMessageDoesNotMatchPatternThenReturnsNullMainVoice() {
        val ussdResponse = UssdResponse("Invalid message")
        val mainVoice = ussdResponse.parseMainVoice()

        Assert.assertNull(mainVoice.mainVoice)
        Assert.assertNull(mainVoice.remainingDays)
    }

    @Test
    fun testParseMainDataWhenNoActiveMainVoiceThenRemainingDaysNull() {
        val ussdResponse = UssdResponse("Usted dispone de 07:37:49 MIN no activos")
        val mainVoice = ussdResponse.parseMainVoice()

        assertEquals(27_469L, mainVoice.mainVoice)
        Assert.assertNull(mainVoice.remainingDays)
    }
}
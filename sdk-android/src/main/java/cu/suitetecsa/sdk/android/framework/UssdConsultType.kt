package cu.suitetecsa.sdk.android.framework

import android.net.Uri

/**
 * Represents different types of USSD consults.
 *
 * @property ussdCode The USSD code associated with the consult type.
 */
sealed class UssdConsultType(val ussdCode: String) {
    /**
     * Represents a consult for the principal balance.
     */
    data object PrincipalBalance : UssdConsultType("*222${Uri.parse("#")}")

    /**
     * Represents a consult for the data balance.
     */
    data object DataBalance : UssdConsultType("*222*328${Uri.parse("#")}")

    /**
     * Represents a consult for the voice balance.
     */
    data object VoiceBalance : UssdConsultType("*222*869${Uri.parse("#")}")

    /**
     * Represents a consult for the messages balance.
     */
    data object MessagesBalance : UssdConsultType("*222*767${Uri.parse("#")}")

    /**
     * Represents a consult for the bonus balance.
     */
    data object BonusBalance : UssdConsultType("*222*266${Uri.parse("#")}")
    data class Custom(val code: String) : UssdConsultType(code)
}

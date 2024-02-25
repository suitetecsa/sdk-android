package cu.suitetecsa.sdk.android.utils

private const val SHORT_NUMBER_LENGTH = 8
private const val START_INDEX = 4
private const val END_INDEX = 12

/**
 * Validates the complete format of the input text.
 *
 * This function checks if the input text matches the expected formats for phone numbers.
 * The formats include various prefixes and country codes specific to a region.
 *
 * @param text The input text to be validated.
 * @return The original text if it matches the expected format, null otherwise.
 */
fun validateFormat(text: String): String? {
    val completeRegex =
        """^(5\d{7}|05\d{7}|535\d{7}|\+535\d{7}|99535\d{7}99|\+5399535\d{7}|\*995\d{7}|\+\*995\d{7})$""".toRegex()
    return if (!text.matches(completeRegex)) null else text
}

/**
 * Extracts the short phone number from the input text.
 *
 * This function first validates the format of the input text. If the format is valid,
 * it then extracts the phone number based on specific criteria. The criteria involve
 * handling different prefixes and lengths to extract the core 8-digit phone number.
 *
 * @param text The input text containing the phone number.
 * @return The extracted 8-digit phone number if the format is valid, null otherwise.
 */
fun extractShortNumber(text: String): String? {
    val validatedText = validateFormat(text) ?: return null

    return when {
        validatedText.startsWith("5") && validatedText.length == SHORT_NUMBER_LENGTH -> validatedText
        validatedText.length > SHORT_NUMBER_LENGTH -> {
            when {
                validatedText.startsWith("99535") -> validatedText.substring(START_INDEX, END_INDEX)
                else -> validatedText.takeLast(SHORT_NUMBER_LENGTH)
            }
        }
        else -> null
    }
}

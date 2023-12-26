package cu.suitetecsa.sdk.android.framework

/**
 * Exception thrown when there is an error in processing a USSD request.
 *
 * @param message The error message associated with the exception.
 */
class UssdRequestException(message: String) : Exception(message)

package cu.suitetecsa.sdk.android.framework

/**
 * Interface for executing a USSD balance request.
 */
interface UssdBalanceRequestExecutor {
    /**
     * Executes a USSD balance request with the provided callback.
     *
     * @param callback The callback to be invoked with the result of the USSD balance request.
     */
    fun execute(callback: UssdBalanceRequestExecutorCallBack)
    fun execute(ussdCode: String, callback: UssdBalanceRequestExecutorCallBack)
}

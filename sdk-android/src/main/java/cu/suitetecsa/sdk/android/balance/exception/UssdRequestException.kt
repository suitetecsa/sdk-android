package cu.suitetecsa.sdk.android.balance.exception

class UssdRequestException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}

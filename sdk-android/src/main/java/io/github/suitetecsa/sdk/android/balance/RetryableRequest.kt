package io.github.suitetecsa.sdk.android.balance

import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest

data class RetryableRequest(
    val request: UssdRequest,
    val customCode: String? = null,
    var retryCount: Int = 0
)

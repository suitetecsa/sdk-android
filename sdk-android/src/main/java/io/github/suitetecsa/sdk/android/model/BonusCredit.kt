package io.github.suitetecsa.sdk.android.model

import java.util.Date

@JvmRecord
data class BonusCredit(
    val data: Float,
    val expires: Date
)

package cu.suitetecsa.sdk.android.kotlin

import cu.suitetecsa.sdk.android.utils.LongUtils

val Long.asDateString: String get() = LongUtils.toDateString(this)
val Long.asSizeString: String get() = LongUtils.toSizeString(this)
val Long.asTimeString: String get() = LongUtils.toTimeString(this)
val Long.asRemainingDays: Int get() = LongUtils.toRemainingDays(this)

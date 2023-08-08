package cu.suitetecsa.sdk.ussd.uitls

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun String.toDate(): Date? = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this)
internal fun String.toCubacelDate(): Date? = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(this)
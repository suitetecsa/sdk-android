package cu.suitetecsa.sdk.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import cu.suitetecsa.sdk.android.domain.model.SimCard

object Utils {
    private val simSlotName = listOf(
        "extra_asus_dial_use_dualsim",
        "com.android.phone.extra.slot",
        "slot",
        "simslot",
        "sim_slot",
        "subscription",
        "Subscription",
        "phone",
        "com.android.phone.DialingMode",
        "simSlot",
        "slot_id",
        "simId",
        "simnum",
        "phone_type",
        "slotId",
        "slotIdx"
    )

    fun makeCall(context: Context, phoneNumber: String, simCard: SimCard) {
        val uri = Uri.parse("tel:${phoneNumber}")
        val intent = Intent(Intent.ACTION_CALL, uri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", simCard.subscriptionId.toString())
        } else {
            intent.putExtra("con.android.phone.extra.PHONE_ACCOUNT_HANDLE", simCard.subscriptionId.toString())
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

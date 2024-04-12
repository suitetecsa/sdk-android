package io.github.suitetecsa.sdk.android

import android.content.Context
import android.os.Build
import io.github.suitetecsa.sdk.android.model.SimCard

/**
 * Interface for collecting SIM card information.
 */
interface SimCardCollector {
    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */
    fun collect(): List<SimCard>
    class Builder {
        fun build(context: Context): SimCardCollector =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                SimCardCollectorApi26(context) else SimCardCollectorLegacy(context)
    }
}

package cu.suitetecsa.sdk.android

import android.content.Context
import cu.suitetecsa.sdk.android.model.SimCard

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
        fun build(context: Context): SimCardCollector {
            return SimCardCollectorImpl(context)
        }
    }
}

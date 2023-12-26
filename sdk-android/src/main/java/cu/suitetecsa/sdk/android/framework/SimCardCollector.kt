package cu.suitetecsa.sdk.android.framework

import android.Manifest
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.domain.model.SimCard

/**
 * Interface for collecting SIM card information.
 */
interface SimCardCollector {
    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    fun collect(): List<SimCard>
}

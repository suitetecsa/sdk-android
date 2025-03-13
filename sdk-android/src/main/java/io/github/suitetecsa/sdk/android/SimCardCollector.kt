package io.github.suitetecsa.sdk.android

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import io.github.suitetecsa.sdk.android.model.SimCard

/**
 * Interface for collecting SIM card data.
 */
interface SimCardCollector {
    /**
     * Collects SIM card information.
     *
     * @return A list of [SimCard] objects.
     */
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    fun collect(): List<SimCard>
    class Builder {
        fun build(context: Context): SimCardCollector =
            SimCardCollectorImpl(context)
    }
}

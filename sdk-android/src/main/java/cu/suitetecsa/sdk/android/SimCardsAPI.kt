package cu.suitetecsa.sdk.android

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.domain.model.SimCard
import cu.suitetecsa.sdk.android.framework.SimCardCollector
import cu.suitetecsa.sdk.android.framework.SimCardCollectorImpl

/**
 * API for retrieving SIM card information.
 *
 * @property simCardCollector The SIM card collector used to retrieve the information.
 */
class SimCardsAPI private constructor(private val simCardCollector: SimCardCollector) {

    /**
     * Retrieves the list of SIM cards installed on the device.
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
    fun getSimCards(): List<SimCard> {
        return simCardCollector.collect()
    }

    /**
     * Builder for constructing an instance of SimCardsAPI.
     *
     * @property context The application context.
     */
    class Builder(private val context: Context) {
        private var simCardCollector: SimCardCollector? = null

        /**
         * Sets the custom SIM card collector.
         *
         * @param dataSource The custom SIM card collector.
         * @return The updated Builder.
         */
        fun withDataSource(dataSource: SimCardCollector): Builder =
            also { it.simCardCollector = dataSource }

        /**
         * Builds an instance of SimCardsAPI.
         *
         * @return The instance of SimCardsAPI.
         */
        fun build(): SimCardsAPI =
            SimCardsAPI(simCardCollector ?: SimCardCollectorImpl(context))
    }
}

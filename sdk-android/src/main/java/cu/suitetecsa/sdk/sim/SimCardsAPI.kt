package cu.suitetecsa.sdk.sim

import android.content.Context
import cu.suitetecsa.sdk.sim.datasource.SimCardDataSource
import cu.suitetecsa.sdk.sim.datasource.SimCardDataSourceImpl
import cu.suitetecsa.sdk.sim.model.SimCard

class SimCardsAPI private constructor(private val simCardDataSource: SimCardDataSource) {

    fun getSimCards(): List<SimCard> {
        return simCardDataSource.getSimCards()
    }

    class Builder(private val context: Context) {
        private var simCardDataSource: SimCardDataSource? = null

        fun dataSource(dataSource: SimCardDataSource): Builder {
            this.simCardDataSource = dataSource
            return this
        }

        fun build(): SimCardsAPI {
            val dataSource = simCardDataSource ?: createDefaultDataSource(context)
            return SimCardsAPI(dataSource)
        }

        private fun createDefaultDataSource(context: Context): SimCardDataSource {
            return SimCardDataSourceImpl(context)
        }
    }
}

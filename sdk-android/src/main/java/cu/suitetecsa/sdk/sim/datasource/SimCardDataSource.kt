package cu.suitetecsa.sdk.sim.datasource

import cu.suitetecsa.sdk.sim.model.SimCard
import kotlinx.coroutines.flow.Flow

interface SimCardDataSource {
    fun getSimCards(): List<SimCard>
}

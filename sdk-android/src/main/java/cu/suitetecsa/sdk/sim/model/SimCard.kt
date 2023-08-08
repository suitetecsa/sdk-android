package cu.suitetecsa.sdk.sim.model

data class SimCard(
    val serialNumber: String,
    val displayName: String,
    val slotIndex: Int,
    val subscriptionId: Int
)
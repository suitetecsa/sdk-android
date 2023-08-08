package cu.suitetecsa.sdk.ussd.model

data class BonusBalance(
    val credit: BonusCredit,
    val unlimitedData: BonusUnlimitedData,
    val data: BonusData,
    val dataCu: BonusDataCU
)
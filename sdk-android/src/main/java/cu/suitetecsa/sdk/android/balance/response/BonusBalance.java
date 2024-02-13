package cu.suitetecsa.sdk.android.balance.response;

import cu.suitetecsa.sdk.android.model.BonusCredit;
import cu.suitetecsa.sdk.android.model.BonusData;
import cu.suitetecsa.sdk.android.model.BonusDataCU;
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData;

/**
 * Clase para representar la respuesta de saldo de bonificación
 */
public record BonusBalance(BonusCredit credit, BonusUnlimitedData unlimitedData, BonusData data,
                           BonusDataCU dataCu) implements UssdResponse {
}

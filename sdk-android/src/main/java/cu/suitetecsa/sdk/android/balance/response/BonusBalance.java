package cu.suitetecsa.sdk.android.balance.response;

import cu.suitetecsa.sdk.android.model.BonusCredit;
import cu.suitetecsa.sdk.android.model.BonusData;
import cu.suitetecsa.sdk.android.model.BonusDataCU;
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData;

/**
 * Clase para representar la respuesta de saldo de bonificación
 */
public class BonusBalance implements UssdResponse {
    private final BonusCredit credit;
    private final BonusUnlimitedData unlimitedData;
    private final BonusData data;
    private final BonusDataCU dataCu;

    public BonusBalance(BonusCredit credit, BonusUnlimitedData unlimitedData, BonusData data, BonusDataCU dataCu) {
        this.credit = credit;
        this.unlimitedData = unlimitedData;
        this.data = data;
        this.dataCu = dataCu;
    }

    public BonusCredit getCredit() {
        return credit;
    }

    public BonusUnlimitedData getUnlimitedData() {
        return unlimitedData;
    }

    public BonusData getData() {
        return data;
    }

    public BonusDataCU getDataCu() {
        return dataCu;
    }
}

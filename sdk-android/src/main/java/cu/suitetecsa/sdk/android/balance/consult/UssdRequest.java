package cu.suitetecsa.sdk.android.balance.consult;

import android.net.Uri;

public enum UssdRequest {
    PRINCIPAL_BALANCE("*222" + Uri.parse("#")),
    DATA_BALANCE("*222*328" + Uri.parse("#")),
    VOICE_BALANCE("*222*869" + Uri.parse("#")),
    MESSAGES_BALANCE("*222*767" + Uri.parse("#")),
    BONUS_BALANCE("*222*266" + Uri.parse("#")),
    CUSTOM("Nothing");

    private final String ussdCode;

    UssdRequest(String ussdCode) {
        this.ussdCode = ussdCode;
    }

    public String getUssdCode() {
        return ussdCode;
    }
}

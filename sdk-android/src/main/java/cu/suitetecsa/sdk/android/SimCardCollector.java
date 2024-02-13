package cu.suitetecsa.sdk.android;

import android.content.Context;

import java.util.List;

import cu.suitetecsa.sdk.android.model.SimCard;

/**
 * Interface for collecting SIM card information.
 */
public interface SimCardCollector {
    /**
     * Collects the SIM card information.
     *
     * @return The list of SIM cards.
     */
    List<SimCard> collect();

    class Builder {
        public SimCardCollector build(Context context) {
            return new SimCardCollectorImpl(context);
        }
    }
}

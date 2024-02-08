package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.model.MainData;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class MainDataParser {

    /**
     * Parses the main data from a given CharSequence and returns a MainData object.
     *
     * @return The parsed main data as a MainData object, or null if the data cannot be parsed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Contract("_ -> new")
    public static MainData parseMainData(CharSequence input) {
        // Regular expression to match the usageBasedPricing pattern
        String usageBasedPricingRegexPattern = "Tarifa:\\s+(?<tfc>[^\"]*?)\\.";
        Pattern usageBasedPricingPattern = Pattern.compile(usageBasedPricingRegexPattern);
        Matcher usageBasedPricingMatcher = usageBasedPricingPattern.matcher(input);

        boolean usageBasedPricing = false;

        if (usageBasedPricingMatcher.find()) {
            // Parse the usageBasedPricing
            usageBasedPricing = !Objects.equals(usageBasedPricingMatcher.group("tfc"), "No activa");
        }

        // Regular expression to match the dataAllNetwork, dataLte, and remainingDays pattern
        String mainDataRegexPattern = "Paquetes:\\s+(?<dataAllNetwork>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?" +
                "(\\s+\\+\\s+)?" +
                "((?<dataLte>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?" +
                "(\\s+no activos)?" +
                "(\\s+validos\\s+(?<dueDate>(\\d+))\\s+dias)?\\.";
        Pattern mainDataPattern = Pattern.compile(mainDataRegexPattern);
        Matcher mainDataMatcher = mainDataPattern.matcher(input);

        Long dataAllNetwork = null;
        Long dataLte = null;
        Integer remainingDays = null;

        if (mainDataMatcher.find()) {
            // Parse the dataAllNetwork, dataLte, and remainingDays
            String dataAllNetworkStr = mainDataMatcher.group("dataAllNetwork");
            if (dataAllNetworkStr != null) {
                dataAllNetwork = StringUtils.toBytes(dataAllNetworkStr);
            }

            String dataLteStr = mainDataMatcher.group("dataLte");
            if (dataLteStr != null) {
                dataLte = StringUtils.toBytes(dataLteStr);
            }

            String remainingDaysStr = mainDataMatcher.group("dueDate");
            if (remainingDaysStr != null) {
                remainingDays = Integer.parseInt(remainingDaysStr);
            }
        }

        // Create and return the MainData object
        return new MainData(usageBasedPricing, dataAllNetwork, dataLte, remainingDays);
    }
}

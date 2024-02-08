package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.balance.response.BonusBalance;
import cu.suitetecsa.sdk.android.model.BonusCredit;
import cu.suitetecsa.sdk.android.model.BonusData;
import cu.suitetecsa.sdk.android.model.BonusDataCU;
import cu.suitetecsa.sdk.android.model.BonusUnlimitedData;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class BonusBalanceParser {

    /**
     * Parses the bonus balance from a given CharSequence and returns a BonusBalance object.
     *
     * @return The parsed bonus balance as a BonusBalance object.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Contract("_ -> new")
    public static BonusBalance parseBonusBalance(CharSequence input) throws ParseException {
        // Regular expressions to match the bonus credit, bonus data, and bonus data CU patterns
        String creditPattern = "\\$(?<bonusCredit>([\\d.]+))\\s+vence\\s+(?<bonusCreditDueDate>(\\d{2}-\\d{2}-\\d{2}))\\.";
        Pattern creditRegex = Pattern.compile(creditPattern);
        Matcher creditMatcher = creditRegex.matcher(input);

        String dataPattern =
                "Datos:\\s+(ilimitados\\s+vence\\s+(?<unlimitedData>(\\d{2}-\\d{2}-\\d{2}))\\.)?" +
                        "(\\s+)?((?<dataAllNetworkBonus>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B))?\\s+\\+\\s+" +
                        "((?<bonusDataLte>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?\\s+vence\\s+" +
                        "((?<bonusDataDueDate>(\\d{2}-\\d{2}-\\d{2})))\\.";
        Pattern dataRegex = Pattern.compile(dataPattern);
        Matcher dataMatcher = dataRegex.matcher(input);

        String dataCUPattern =
                "Datos\\.cu\\s+(?<bonusDataCu>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?\\s+vence\\s+" +
                        "(?<bonusDataCuDueDate>(\\d{2}-\\d{2}-\\d{2}))\\.";
        Pattern dataCURegex = Pattern.compile(dataCUPattern);
        Matcher dataCUMatcher = dataCURegex.matcher(input);

        BonusCredit bonusCredit = null;
        BonusData bonusData = null;
        BonusDataCU bonusDataCU = null;
        BonusUnlimitedData bonusUnlimitedData = null;

        if (creditMatcher.find()) {
            // Parse bonus credit
            bonusCredit = new BonusCredit(
                    Float.parseFloat(Objects.requireNonNull(creditMatcher.group("bonusCredit"))),
                    StringUtils.toDateMillis(creditMatcher.group("bonusCreditDueDate"))
            );
        }

        if (dataMatcher.find()) {
            // Parse bonus data and bonus unlimited data
            String dataAllNetworkBonus = dataMatcher.group("dataAllNetworkBonus");
            String bonusDataLte = dataMatcher.group("bonusDataLte");
            String bonusDataDueDate = dataMatcher.group("bonusDataDueDate");
            bonusData = new BonusData(
                    dataAllNetworkBonus != null ? StringUtils.toBytes(dataAllNetworkBonus) : null,
                    bonusDataLte != null ? StringUtils.toBytes(bonusDataLte) : null,
                    StringUtils.toDateMillis(bonusDataDueDate)
            );

            String unlimitedData = dataMatcher.group("unlimitedData");
            if (unlimitedData != null) {
                bonusUnlimitedData = new BonusUnlimitedData(StringUtils.toDateMillis(unlimitedData));
            }
        }

        if (dataCUMatcher.find()) {
            // Parse bonus data CU
            bonusDataCU = new BonusDataCU(
                    StringUtils.toBytes(Objects.requireNonNull(dataCUMatcher.group("bonusDataCu"))),
                    StringUtils.toDateMillis(dataCUMatcher.group("bonusDataCuDueDate"))
            );
        }

        // Return the parsed bonus balance
        return new BonusBalance(bonusCredit, bonusUnlimitedData, bonusData, bonusDataCU);
    }
}

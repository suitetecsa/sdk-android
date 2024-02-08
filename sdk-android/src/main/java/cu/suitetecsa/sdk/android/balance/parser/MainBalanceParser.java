package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.model.MainBalance;
import cu.suitetecsa.sdk.android.model.MainData;
import cu.suitetecsa.sdk.android.model.MainSms;
import cu.suitetecsa.sdk.android.model.MainVoice;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class MainBalanceParser {

    /**
     * Parses the main balance data from a given CharSequence and returns a MainBalance object.
     *
     * @return The parsed main balance data as a MainBalance object, or null if the data cannot be parsed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    public static MainBalance parseMainBalance(CharSequence input) throws ParseException {
        // Regular expression to match the credit, activeUntil, and dueDate pattern
        String creditRegexPattern = "Saldo:\\s+(?<principalCredit>([\\d.]+))\\s+CUP\\.\\s+([^\"]*?)?" +
                "Linea activa hasta\\s+(?<activeUntil>(\\d{2}-\\d{2}-\\d{2}))" +
                "\\s+vence\\s+(?<dueDate>(\\d{2}-\\d{2}-\\d{2}))\\.";
        Pattern creditPattern = Pattern.compile(creditRegexPattern);
        Matcher creditMatcher = creditPattern.matcher(input);

        if (creditMatcher.find()) {
            // Parse the credit, activeUntil, and dueDate
            float credit = Float.parseFloat(Objects.requireNonNull(creditMatcher.group("principalCredit")));
            long activeUntil = StringUtils.toDateMillis(creditMatcher.group("activeUntil"));
            long dueDate = StringUtils.toDateMillis(creditMatcher.group("dueDate"));

            // Regular expression to match the dataAllNetwork and dataLte pattern
            String dataRegexPattern = "Datos:\\s+(?<dataAllNetwork>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?" +
                    "(\\s+\\+\\s+)?" +
                    "((?<dataLte>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)\\.";
            Pattern dataPattern = Pattern.compile(dataRegexPattern);
            Matcher dataMatcher = dataPattern.matcher(input);

            Long dataAllNetwork = null;
            Long dataLte = null;

            if (dataMatcher.find()) {
                // Parse the dataAllNetwork and dataLte
                String dataAllNetworkStr = dataMatcher.group("dataAllNetwork");
                if (dataAllNetworkStr != null) {
                    dataAllNetwork = StringUtils.toBytes(dataAllNetworkStr);
                }
                String dataLteStr = dataMatcher.group("dataLte");
                if (dataLteStr != null) {
                    dataLte = StringUtils.toBytes(dataLteStr);
                }
            }

            // Create the MainData object
            MainData mainData = new MainData(false, dataAllNetwork, dataLte, null);

            // Regular expression to match the voice pattern
            String voiceRegexPattern = "Voz:\\s+(?<voice>(\\d{1,3}:\\d{2}:\\d{2}))\\.";
            Pattern voicePattern = Pattern.compile(voiceRegexPattern);
            Matcher voiceMatcher = voicePattern.matcher(input);

            Long voice = null;

            if (voiceMatcher.find()) {
                // Parse the voice
                String voiceStr = voiceMatcher.group("voice");
                assert voiceStr != null;
                voice = StringUtils.toSeconds(voiceStr);
            }

            // Create the MainVoice object
            MainVoice mainVoice = voice != null ? new MainVoice(voice, null) : null;

            // Regular expression to match the sms pattern
            String smsRegexPattern = "SMS:\\s+(?<sms>(\\d+))\\.";
            Pattern smsPattern = Pattern.compile(smsRegexPattern);
            Matcher smsMatcher = smsPattern.matcher(input);

            Integer sms = null;

            if (smsMatcher.find()) {
                // Parse the sms
                String smsStr = smsMatcher.group("sms");
                assert smsStr != null;
                sms = Integer.parseInt(smsStr);
            }

            // Create the MainSms object
            MainSms mainSms = sms != null ? new MainSms(sms, null) : null;

            // Create and return the MainBalance object
            return new MainBalance(credit, mainData, mainVoice, mainSms, null, null, activeUntil, dueDate);
        }

        return null;
    }
}

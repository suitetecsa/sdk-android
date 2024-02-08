package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.model.MailData;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class MailDataParser {

    /**
     * Parses the mail data from a given CharSequence and returns a MailData object.
     *
     * @return The parsed mail data as a MailData object, or null if the data cannot be parsed.
     */
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    public static MailData parseMailData(CharSequence input) throws ParseException {
        // Regular expression to match the mail data pattern
        String mailDataRegexPattern =
                "Mensajeria:\\s+(?<dataMail>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?" +
                        "(\\s+no activos)?" +
                        "(\\s+validos\\s+(?<dueDate>(\\d+))\\s+dias)?\\.";
        Pattern mailDataPattern = Pattern.compile(mailDataRegexPattern);
        Matcher mailDataMatcher = mailDataPattern.matcher(input);


        Integer remainingDays = null;

        if (!mailDataMatcher.find()) return null;;
        // Parse the mail data
        String dataMailStr = mailDataMatcher.group("dataMail");
        assert dataMailStr != null;
        long dataMail = StringUtils.toBytes(dataMailStr);

        String remainingDaysStr = mailDataMatcher.group("dueDate");
        if (remainingDaysStr != null) {
            remainingDays = Integer.parseInt(remainingDaysStr);
        }

        // Create and return the MailData object
        return new MailData(dataMail, remainingDays);
    }
}

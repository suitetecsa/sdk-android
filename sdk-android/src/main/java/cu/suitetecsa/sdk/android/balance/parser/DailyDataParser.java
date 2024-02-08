package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.model.DailyData;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class DailyDataParser {

    /**
     * Parses the daily data from a given CharSequence and returns a DailyData object.
     *
     * @return The parsed daily data as a DailyData object, or null if the data cannot be parsed.
     */
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Contract("_ -> new")
    public static DailyData parseDailyData(CharSequence input) throws ParseException {
        // Regular expression to match the daily data pattern
        String dailyDataRegexPattern =
                "Diaria:\\s+(?<dataDaily>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?" +
                        "(\\s+no activos)?" +
                        "(\\s+validos\\s+(?<dueDate>(\\d+))\\s+horas)?\\.";
        Pattern dailyDataPattern = Pattern.compile(dailyDataRegexPattern);
        Matcher dailyDataMatcher = dailyDataPattern.matcher(input);

        Integer remainingHours = null;

        if (!dailyDataMatcher.find()) return null;
        // Parse the daily data
        String dataDailyStr = dailyDataMatcher.group("dataDaily");
        assert dataDailyStr != null;
        long dataDaily = StringUtils.toBytes(dataDailyStr);

        String remainingHoursStr = dailyDataMatcher.group("dueDate");
        if (remainingHoursStr != null) {
            remainingHours = Integer.parseInt(remainingHoursStr);
        }

        // Create and return the DailyData object
        return new DailyData(dataDaily, remainingHours);
    }
}

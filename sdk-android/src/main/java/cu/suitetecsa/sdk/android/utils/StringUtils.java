package cu.suitetecsa.sdk.android.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
    public static long toSeconds(@NotNull String time) {
        String[] parts = time.split(":");
        long totalSeconds = 0;

        for (String part : parts) {
            int SECONDS_PER_MINUTE = 60;
            totalSeconds = totalSeconds * SECONDS_PER_MINUTE + Long.parseLong(part);
        }

        return totalSeconds;
    }

    public static long toDateMillis(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(date));
        return calendar.getTimeInMillis();
    }

    public static Date toDate(String date) throws ParseException {
        return new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).parse(date);
    }

    public static long toBytes(@NotNull String data) {
        String count = data.replaceAll("[GMKBT]", "");
        String unit = data.split(" ")[data.split(" ").length - 1].toUpperCase();
        return (long) (Double.parseDouble(count) * Math.pow(1024, "BKMGT".indexOf(unit.charAt(0))));
    }
}

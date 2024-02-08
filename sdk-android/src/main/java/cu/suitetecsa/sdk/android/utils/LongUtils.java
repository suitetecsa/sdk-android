package cu.suitetecsa.sdk.android.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LongUtils {
    public static @NotNull String toDateString(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.getTime());
    }

    @NonNull
    public static String toTimeString(Long time) {
        int SECONDS_IN_HOUR = 3600;
        int SECONDS_IN_MINUTE = 60;
        long hours = time / SECONDS_IN_HOUR;
        long minutes = (time % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        long seconds = time % SECONDS_IN_MINUTE;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Converts a size value in bytes to a human-readable string representation.
     *
     * @return The size value formatted as a string with the appropriate unit (bytes, KB, MB, GB, TB).
     */
    public static @NotNull String toSizeString(long size) {
        double SIZE_UNIT_MAX_LENGTH = 1024.0;
        String[] sizeUnits = {"bytes", "KB", "MB", "GB", "TB"};
        double sizeValue = size;
        int sizeUnitIndex = 0;
        while (sizeValue >= SIZE_UNIT_MAX_LENGTH && sizeUnitIndex < sizeUnits.length - 1) {
            sizeValue /= SIZE_UNIT_MAX_LENGTH;
            sizeUnitIndex++;
        }
        return String.format(Locale.getDefault(), "%.2f %s", sizeValue, sizeUnits[sizeUnitIndex]);
    }

    public static int toRemainingDays(long date) {
        int HOURS_PER_DAY = 24;
        int MINUTES_PER_HOUR = 60;
        int MILLISECONDS = 1000;
        Calendar calendar = Calendar.getInstance();
        long diffInMillis = date - calendar.getTimeInMillis();
        return (int) ((diffInMillis / (HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR * MILLISECONDS)) + 1);
    }
}

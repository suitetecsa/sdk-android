package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.balance.response.MessagesBalance;

public class MainSmsParser {

    /**
     * Parses the main SMS balance from a given CharSequence and returns a MessagesBalance object.
     *
     * @return The parsed main SMS balance as a MessagesBalance object, or null if the data cannot be parsed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Contract("_ -> new")
    public static MessagesBalance parseMainSms(CharSequence input) throws ParseException {
        // Regular expression to match the SMS balance pattern
        String smsPattern =
                "Usted dispone de\\s+(?<sms>(\\d+))\\s+SMS(\\s+no activos)?" +
                        "(\\s+validos por\\s+(?<dueDate>(\\d+))\\s+dias)?(\\.)?";
        Pattern smsRegex = Pattern.compile(smsPattern);
        Matcher smsMatcher = smsRegex.matcher(input);


        Integer remainingDays = null;

        if(!smsMatcher.find()) throw new ParseException(input.toString(), 0);
        // Parse the SMS balance and due date
        String smsStr = smsMatcher.group("sms");
        assert smsStr != null;
        int sms = Integer.parseInt(smsStr);

        String remainingDaysStr = smsMatcher.group("dueDate");
        if (remainingDaysStr != null) {
            remainingDays = Integer.parseInt(remainingDaysStr);
        }

        // Create and return the MessagesBalance object
        return new MessagesBalance(sms, remainingDays);
    }
}

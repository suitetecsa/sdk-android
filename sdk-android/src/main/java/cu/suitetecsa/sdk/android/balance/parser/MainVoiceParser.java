package cu.suitetecsa.sdk.android.balance.parser;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cu.suitetecsa.sdk.android.balance.response.VoiceBalance;
import cu.suitetecsa.sdk.android.utils.StringUtils;

public class MainVoiceParser {

    /**
     * Parses the main voice balance from a given CharSequence and returns a VoiceBalance object.
     *
     * @return The parsed main voice balance as a VoiceBalance object, or null if the data cannot be parsed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Contract("_ -> new")
    public static VoiceBalance parseMainVoice(CharSequence input) throws ParseException {
        // Regular expression to match the voice balance pattern
        String voicePattern =
                "Usted dispone de\\s+(?<voice>(\\d+:\\d{2}:\\d{2}))\\s+MIN(\\s+no activos)?" +
                        "(\\s+validos por\\s+(?<dueDate>(\\d+))\\s+dias)?";
        Pattern voiceRegex = Pattern.compile(voicePattern);
        Matcher voiceMatcher = voiceRegex.matcher(input);

        Integer remainingDays = null;

        if(!voiceMatcher.find()) throw new ParseException(input.toString(), 0);;
        // Parse the voice balance and due date
        String voiceStr = voiceMatcher.group("voice");
        assert voiceStr != null;
        long voice = StringUtils.toSeconds(voiceStr);

        String remainingDaysStr = voiceMatcher.group("dueDate");
        if (remainingDaysStr != null) {
            remainingDays = Integer.parseInt(remainingDaysStr);
        }

        // Create and return the VoiceBalance object
        return new VoiceBalance(voice, remainingDays);
    }
}

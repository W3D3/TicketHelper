package logic;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketExtractor {

    static Pattern ticketPattern = Pattern.compile("([A-Z])+-\\d+");

    @NotNull
    public static String extractFromString(String input) {
        String result = "";
        Matcher matcher = ticketPattern.matcher(input);

        // find first occurrence
        if (matcher.find()) {
            result = matcher.group(0);
        }

        return result;
    }
}

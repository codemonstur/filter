package filter;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Rule implements Consumer<String> {

    public String input;
    public String output;
    public Boolean printMatchFailure;
    public List<Rule> rules;

    private transient Pattern compiledPattern;

    public void accept(final String line) {
        final var matcher = filterPattern().matcher(line);
        if (matcher.matches()) {
            final var raw = toRawOutput(output, matcher);
            if (rules != null) for (final var rule : rules) {
                if (rule.matches(raw)) {
                    rule.accept(raw);
                    break;
                }
            }
            else if (raw != null) System.out.println(raw);
        } else if (printMatchFailure != null && printMatchFailure)
            System.out.println(line);
    }

    private boolean matches(final String line) {
        return filterPattern().matcher(line).matches();
    }

    private Pattern filterPattern() {
        if (compiledPattern == null)
            compiledPattern = Pattern.compile(input == null ? ".+" : input);
        return compiledPattern;
    }

    private static String toRawOutput(final String format, final Matcher matcher) {
        if (format == null) return null;
        String output = format.replace("\\0", matcher.group());
        for (int i = 0; i < matcher.groupCount(); i++)
            output = output.replace("\\"+(i+1), matcher.group(i+1));
        return output;
    }

}

package logfilter;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Rule implements Consumer<String> {

    public String filter;
    public String format;
    public Boolean printMatchFailure;
    public List<Rule> rules;

    private transient Pattern compiledPattern;

    public void accept(final String line) {
        final var matcher = filterPattern().matcher(line);
        if (matcher.matches()) {
            final var output = toOutput(format, matcher);
            if (rules != null) for (final var rule : rules) {
                if (rule.matches(output)) {
                    rule.accept(output);
                    break;
                }
            }
            else if (output != null) System.out.println(output);
        } else if (printMatchFailure != null && printMatchFailure)
            System.out.println(line);
    }

    private boolean matches(final String line) {
        return filterPattern().matcher(line).matches();
    }

    private Pattern filterPattern() {
        if (compiledPattern == null)
            compiledPattern = Pattern.compile(filter == null ? ".+" : filter);
        return compiledPattern;
    }

    private static String toOutput(final String format, final Matcher matcher) {
        if (format == null) return null;
        String output = format.replace("\\0", matcher.group());
        for (int i = 0; i < matcher.groupCount(); i++)
            output = output.replace("\\"+(i+1), matcher.group(i+1));
        return output;
    }

}

package filter;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code Rule} class.
 */
public final class Rule implements Consumer<String> {

    /**
     * The {@code input} field.
     */
    public String input;
    /**
     * The {@code output} field.
     */
    public String output;
    /**
     * The {@code printMatchFailure} field.
     */
    public Boolean printMatchFailure;
    /**
     * The {@code rules} field.
     */
    public List<Rule> rules;

    private transient Pattern compiledPattern;

    /**
     * Performs the {@code accept} operation.
     *
     * @param line the line
     */
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


    /**
     * Creates a new instance.
     */
    public Rule() {
    }
}

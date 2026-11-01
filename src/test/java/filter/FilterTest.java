package filter;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterTest {

    @Test
    void rule_accept_simpleOutput() {
        final Rule r = new Rule();
        r.input = "a(.*)";
        r.output = "X\\1";
        // should not throw
        r.accept("abc");
    }

    @Test
    void rule_accept_noOutput() {
        final Rule r = new Rule();
        r.input = "hello";
        r.output = null;
        r.accept("hello");
    }

    @Test
    void rule_accept_matchFailurePrints() {
        final Rule r = new Rule();
        r.input = "zzz";
        r.printMatchFailure = true;
        r.accept("hello");
    }

    @Test
    void rule_nestedRules() {
        final Rule outer = new Rule();
        outer.input = "a(.*)";
        outer.output = "\\1";
        final Rule inner = new Rule();
        inner.input = "b.*";
        inner.output = "INNER";
        outer.rules = List.of(inner);
        outer.accept("abc");
    }

    @Test
    void toRawOutputViaReflection() throws Exception {
        final Method m = Rule.class.getDeclaredMethod("toRawOutput", String.class, Matcher.class);
        m.setAccessible(true);
        final Pattern p = Pattern.compile("a(b)(c)");
        final Matcher matcher = p.matcher("abc");
        assertTrue(matcher.matches());
        final String out = (String) m.invoke(null, "X\\0 \\1 \\2", matcher);
        assertEquals("Xabc b c", out);
    }

    @Test
    void filterPattern_nullInput() throws Exception {
        final Method m = Rule.class.getDeclaredMethod("filterPattern");
        m.setAccessible(true);
        final Rule r = new Rule();
        r.input = null;
        final Pattern p = (Pattern) m.invoke(r);
        assertTrue(p.matcher("anything").matches());
    }
}

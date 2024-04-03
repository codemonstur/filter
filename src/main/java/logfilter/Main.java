package logfilter;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.file.Files.newInputStream;
import static jcli.CliParserBuilder.newCliParser;

public enum Main {;

    public static void main(final String... args) throws IOException {
        final var arguments = newCliParser(CliArguments::new)
                .onHelpPrintHelpAndExit().onErrorPrintHelpAndExit().parseSuppressErrors(args);

        streamInput(loadRules(arguments.rulesFile));
    }

    private static Rule loadRules(final Path file) throws IOException {
        final var yaml = new Yaml();
        try (final var in = newInputStream(file)) {
            return yaml.loadAs(in, Rule.class);
        }
    }

    private static void streamInput(final Consumer<String> lineReader) {
        try (final var br = new BufferedReader(new InputStreamReader(System.in))) {
            String line; while ((line = br.readLine()) != null) {
                lineReader.accept(line);
            }
        } catch (final IOException ignore) {}
    }

}

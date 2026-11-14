package filter;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

import java.nio.file.Path;

/**
 * The {@code CliArguments} class.
 */
@CliCommand(name = "filter", description = "Reads input from stdin, runs the lines through the configured rules and outputs results")
public final class CliArguments {

    /**
     * The {@code rulesFile} field.
     */
    @CliOption(name = 'c', longName = "config", isMandatory = true, description = "The rules configuration file")
    public Path rulesFile;

    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true)
    public boolean help;


    /**
     * Creates a new instance.
     */
    public CliArguments() {
    }
}

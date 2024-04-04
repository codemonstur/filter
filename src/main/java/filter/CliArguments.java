package filter;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

import java.nio.file.Path;

@CliCommand(name = "filter", description = "Reads input from stdin, runs the lines through the configured rules and outputs results")
public final class CliArguments {

    @CliOption(name = 'c', longName = "config", isMandatory = true, description = "The rules configuration file")
    public Path rulesFile;

    @CliOption(name = 'h', longName = "help", isHelp = true)
    public boolean help;

}

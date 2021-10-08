package de.yanwittmann.util;

public abstract class CommandGenerator {

    public static ArgParser getSettingsCommand() {
        ArgParser settingsCommand = new ArgParser();
        settingsCommand.setPrefix("opt");
        settingsCommand.setPrefixRequired(true);
        settingsCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("unicode")
                        .setParameterName("active")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.BOOLEAN)
        );
        return settingsCommand;
    }

    public static ArgParser getMathCommand() {
        ArgParser mathCommand = new ArgParser();
        mathCommand.setPrefix("math");
        mathCommand.setPrefixRequired(true);
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("tautologie", "ta")
                        .setParameterName("expression")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
                        .setDescription("Checks if the given expression is a tautologie.")
        );
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("truth", "tr")
                        .setParameterName("expression")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
                        .setDescription("Generates a truth table to a given expression.")
        );
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("variables", "va")
                        .setParameterName("expression")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
                        .setDescription("Extracts the variables from a given expression.")
        );
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("equals", "eq")
                        .setParameterName("expression")
                        .setRequired(false)
                        .setParameterRequired(false)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
                        .setDescription("Checks if two expressions lead to the same truth table. Provide expressions with parameters -p1 and -p2")
        );
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("-p1", "--parameter1")
                        .setParameterName("parameter")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
        );
        mathCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("-p2", "--parameter2")
                        .setParameterName("parameter")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
        );
        return mathCommand;
    }
}

package de.yanwittmann.cmdtool.util;

public abstract class CommandGenerator {

    public static ArgParser getHelpCommand() {
        ArgParser helpCommand = new ArgParser();
        helpCommand.setPrefix("help");
        helpCommand.setPrefixRequired(true);
        return helpCommand;
    }

    public static ArgParser getSettingsCommand() {
        ArgParser settingsCommand = new ArgParser();
        settingsCommand.setPrefix("option");
        settingsCommand.setPrefixRequired(true);
        settingsCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("unicode")
                        .setParameterName("active")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.BOOLEAN)
                        .setDescription("Will allow unicode characters to be printed to the console.")
        );
        return settingsCommand;
    }

    public static ArgParser getHistoryCommand() {
        ArgParser historyCommand = new ArgParser();
        historyCommand.setPrefix("history");
        historyCommand.setPrefixRequired(true);
        historyCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("clear", "c")
                        .setRequired(false)
                        .setDescription("Clears the history.")
        );
        return historyCommand;
    }

    public static ArgParser getNotesCommand() {
        ArgParser notesCommand = new ArgParser();
        notesCommand.setPrefix("note");
        notesCommand.setPrefixRequired(true);
        notesCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("add", "a")
                        .setParameterName("note")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.STRING)
                        .setDescription("The note text to create the note from.")
        );
        notesCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("remove", "r")
                        .setParameterName("note")
                        .setRequired(false)
                        .setParameterRequired(true)
                        .setParameterType(ArgParser.Argument.ParameterType.INTEGER)
                        .setDescription("The note index to remove.")
        );
        notesCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("list", "l")
                        .setRequired(false)
                        .setDescription("Lists all notes.")
        );
        notesCommand.addArgument(
                new ArgParser.Argument()
                        .addIdentifier("clear", "c")
                        .setRequired(false)
                        .setDescription("Clears all notes.")
        );
        return notesCommand;
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
                        .addIdentifier("truthbuilder", "trb")
                        .setParameterName("expression")
                        .setRequired(false)
                        .setDescription("Build yourself a truth table from several expressions.")
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
                        .setDescription("Checks if two expressions lead to the same truth table. Provide expressions with parameters -p1 and -p2.")
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

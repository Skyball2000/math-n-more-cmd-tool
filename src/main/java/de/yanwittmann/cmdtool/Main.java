package de.yanwittmann.cmdtool;

import com.fathzer.soft.javaluator.Operator;
import de.yanwittmann.cmdtool.math.ExpressionEvaluation;
import de.yanwittmann.cmdtool.math.TreeBooleanEvaluator;
import de.yanwittmann.cmdtool.util.ArgParser;
import de.yanwittmann.cmdtool.util.CommandGenerator;
import de.yanwittmann.cmdtool.util.Util;
import org.snim2.checker.ast.Formula;
import org.snim2.checker.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String TOOL_VERSION = "1.0.1";

    public static void main(String[] args) {

        System.out.println("| Command Line Math-Tool version " + TOOL_VERSION + " written by Yan Wittmann");
        System.out.println("| Uses [fathzer javaluator] and [snim2 tautology-checker]");
        System.out.println("| Type [help] for command syntax help\n");

        Scanner scanner = new Scanner(System.in);

        ArgParser mathCommand = CommandGenerator.getMathCommand();
        ArgParser settingsCommand = CommandGenerator.getSettingsCommand();
        ArgParser helpCommand = CommandGenerator.getHelpCommand();

        while (true) {
            String input = Util.askForInput(scanner, Util.INPUT_REGULAR);
            if (input == null || input.length() == 0) continue;
            if (input.equals("exit") || input.equals("quit")) {
                System.out.println("bye");
                return;
            }
            if (input.equals("clear") || input.equals("cls")) {
                for (int i = 0; i < 100; i++) System.out.println();
                continue;
            }

            if (helpCommand.matches(input)) {
                try {
                    System.out.println(settingsCommand);
                    System.out.println(mathCommand);

                } catch (Exception e) {
                    System.err.println("An error occurred while performing the operation: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (settingsCommand.matches(input)) {
                try {
                    ArgParser.Results result = mathCommand.parse(input);
                    boolean argUnicode = result.isPresent("unicode");

                    if (argUnicode) {
                        unicodeOutput = result.getBoolean("unicode");
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred while performing the operation: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (mathCommand.matches(input)) {
                try {
                    ArgParser.Results result = mathCommand.parse(input);

                    boolean argTautologie = result.isPresent("tautologie");
                    boolean argTruth = result.isPresent("truth");
                    boolean argTruthBuilder = result.isPresent("truthbuilder");
                    boolean argVariables = result.isPresent("variables");
                    boolean argEquals = result.isPresent("equals");
                    boolean argP1 = result.isPresent("-p1");
                    boolean argP2 = result.isPresent("-p2");

                    if (argTautologie) {
                        InputStream inputStream = new ByteArrayInputStream(
                                result.getString("tautologie")
                                        .replace("1", "TT")
                                        .replace("0", "FF")
                                        .getBytes()
                        );
                        Parser myParser = new Parser(inputStream);
                        inputStream.close();

                        Formula formula = myParser.getAST();
                        System.out.println("    Abstract Syntax Tree: " + normalizeExpressionOutput(formula));
                        formula = formula.removeImplications();
                        System.out.println("    Removed Implications: " + normalizeExpressionOutput(formula));
                        formula = formula.toNnf();
                        System.out.println("    Negative Normal Form: " + normalizeExpressionOutput(formula));
                        formula = formula.nnfToCnf();
                        System.out.println(" Conjunctive Normal Form: " + normalizeExpressionOutput(formula));
                        formula = formula.simplifyCnf();
                        System.out.println("              Simplified: " + normalizeExpressionOutput(formula));
                        System.out.println("                  Result: " + (org.snim2.checker.ast.True.VALUE == formula));
                        System.out.println();

                    } else if (argTruth) {
                        System.out.println(normalizeExpressionOutput(result.getString("truth")));
                        System.out.println(ExpressionEvaluation.generateTruthTable(result.getString("truth")));

                    } else if (argTruthBuilder) {
                        System.out.println("Enter the input variables, split by a space character:");
                        List<String> variables = ExpressionEvaluation.extractVariables(Util.askForInput(scanner, Util.INPUT_INDENT_1));
                        System.out.println("Enter one expression per line, leave empty to stop. To assign a new variable, enter [VAR] = [EXPR]. Use [undo] and [restart] to control the input.");
                        List<String> expressions = Util.multiCmdInput(scanner);
                        System.out.println(ExpressionEvaluation.buildTruthTableFromMultipleExpressions(variables, expressions));

                    } else if (argVariables) {
                        System.out.println(normalizeExpressionOutput(result.getString("variables")));
                        System.out.println(ExpressionEvaluation.extractVariables(result.getString("variables")));

                    } else if (argEquals && argP1 && argP2) {
                        String result1 = ExpressionEvaluation.generateTruthTable(result.getString("-p1"));
                        String result2 = ExpressionEvaluation.generateTruthTable(result.getString("-p2"));

                        if (result1.equals(result2)) {
                            System.out.println("Both expressions lead to the same truth table:");
                            System.out.println("(" + normalizeExpressionOutput(result.getString("-p1")) + ")  <=>  (" + result.getString("-p2") + ")");
                            System.out.println(result1);
                        } else {
                            System.out.println("The expressions lead to different truth tables:");
                            System.out.println(normalizeExpressionOutput(result.getString("-p1")));
                            System.out.println(result1);
                            System.out.println(normalizeExpressionOutput(result.getString("-p2")));
                            System.out.println(result2);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("An error occurred while parsing/solving the input: " + e.getMessage());
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static String normalizeExpressionOutput(Formula formula) {
        return normalizeExpressionOutput(formula.toString());
    }

    private static boolean unicodeOutput = false;

    public static String normalizeExpressionOutput(String expression) {
        String not, and, or, equi, impl, nand, nor, xor;
        if (unicodeOutput) {
            not = UNICODE_NOT;
            and = UNICODE_AND;
            or = UNICODE_OR;
            equi = UNICODE_EQUI;
            impl = UNICODE_IMPL;
            nand = UNICODE_NAND;
            nor = UNICODE_NOR;
            xor = UNICODE_XOR;
        } else {
            not = ASCII_NOT;
            and = ASCII_AND;
            or = ASCII_OR;
            equi = ASCII_EQUI;
            impl = ASCII_IMPL;
            nand = ASCII_NAND;
            nor = ASCII_NOR;
            xor = ASCII_XOR;
        }
        for (Operator o : TreeBooleanEvaluator.NAND_OPERATORS) expression = expression.replace(o.getSymbol(), nand);
        for (Operator o : TreeBooleanEvaluator.NOR_OPERATORS) expression = expression.replace(o.getSymbol(), nor);
        for (Operator o : TreeBooleanEvaluator.NOT_OPERATORS) expression = expression.replace(o.getSymbol(), not);
        for (Operator o : TreeBooleanEvaluator.OR_OPERATORS) expression = expression.replace(o.getSymbol(), or);
        for (Operator o : TreeBooleanEvaluator.AND_OPERATORS) expression = expression.replace(o.getSymbol(), and);
        for (Operator o : TreeBooleanEvaluator.EQUI_OPERATORS) expression = expression.replace(o.getSymbol(), equi);
        for (Operator o : TreeBooleanEvaluator.IMPL_OPERATORS) expression = expression.replace(o.getSymbol(), impl);
        for (Operator o : TreeBooleanEvaluator.XOR_OPERATORS) expression = expression.replace(o.getSymbol(), xor);
        return expression.replaceAll(" +", " ");
    }

    private final static String UNICODE_NOT = " \u00AC ";
    private final static String UNICODE_AND = " \u2227 ";
    private final static String UNICODE_OR = " \u2228 ";
    private final static String UNICODE_EQUI = " \u2194 ";
    private final static String UNICODE_IMPL = " \u2192 ";
    private final static String UNICODE_NAND = " NAND ";
    private final static String UNICODE_NOR = " NOR ";
    private final static String UNICODE_XOR = " ⊕ ";

    private final static String ASCII_NOT = " ! ";
    private final static String ASCII_AND = " AND ";
    private final static String ASCII_OR = " OR ";
    private final static String ASCII_EQUI = " <=> ";
    private final static String ASCII_IMPL = " => ";
    private final static String ASCII_NAND = " NAND ";
    private final static String ASCII_NOR = " NOR ";
    private final static String ASCII_XOR = " XOR ";
}
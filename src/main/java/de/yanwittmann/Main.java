package de.yanwittmann;

import de.yanwittmann.math.TreeBooleanEvaluator;
import de.yanwittmann.util.ArgParser;
import de.yanwittmann.util.CommandGenerator;
import org.snim2.checker.ast.Formula;
import org.snim2.checker.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    private final static String TOOL_VERSION = "1.0.1";

    public static void main(String[] args) {

        System.out.println("| Command Line Math-Tool version " + TOOL_VERSION + " written by Yan Wittmann");
        System.out.println("| Uses [fathzer javaluator] and [snim2 tautology-checker]");
        System.out.println("| Type [help] for command syntax help\n");

        ArgParser mathCommand = CommandGenerator.getMathCommand();
        ArgParser settingsCommand = CommandGenerator.getSettingsCommand();

        while (true) {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input == null || input.length() == 0) continue;
            if (input.equals("exit") || input.equals("quit")) {
                System.out.println("bye");
                return;
            }
            if (input.equals("clear") || input.equals("cls")) {
                for (int i = 0; i < 100; i++) System.out.println();
                continue;
            }

            if (mathCommand.matches(input)) {
                try {
                    ArgParser.Results result = mathCommand.parse(input);

                    boolean argTautologie = result.isPresent("tautologie");
                    boolean argTruth = result.isPresent("truth");
                    boolean argVariables = result.isPresent("variables");
                    boolean argEquals = result.isPresent("equals");
                    boolean argP1 = result.isPresent("-p1");
                    boolean argP2 = result.isPresent("-p2");
                    boolean argUnicode = result.isPresent("unicode");

                    if (argUnicode) {
                        unicodeOutput = result.getBoolean("unicode");

                    } else if (argTautologie) {
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
                        System.out.println(TreeBooleanEvaluator.generateTruthTable(result.getString("truth")));

                    } else if (argVariables) {
                        System.out.println(normalizeExpressionOutput(result.getString("variables")));
                        System.out.println(TreeBooleanEvaluator.extractVariables(result.getString("variables")));

                    } else if (argEquals && argP1 && argP2) {
                        String result1 = TreeBooleanEvaluator.generateTruthTable(result.getString("-p1"));
                        String result2 = TreeBooleanEvaluator.generateTruthTable(result.getString("-p2"));

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
                }
            }
        }
    }

    private static String normalizeExpressionOutput(Formula formula) {
        return normalizeExpressionOutput(formula.toString());
    }

    private static boolean unicodeOutput = false;

    private static String normalizeExpressionOutput(String expression) {
        String not, and, or, equi, impl;
        if (unicodeOutput) {
            not = UNICODE_NOT;
            and = UNICODE_AND;
            or = UNICODE_OR;
            equi = UNICODE_EQUI;
            impl = UNICODE_IMPL;
        } else {
            not = ASCII_NOT;
            and = ASCII_AND;
            or = ASCII_OR;
            equi = ASCII_EQUI;
            impl = ASCII_IMPL;
        }
        return expression
                .replace("!", not).replace("not", not).replace("NOT", not).replace(not + " ", not)
                .replace("/\\", and).replace("and", and).replace("AND", and).replace("&&", and)
                .replace("\\/", or).replace("or", or).replace("OR", or).replace("||", or)
                .replace("<=>", equi).replace("EQUI", equi)
                .replace("=>", impl).replace("IMPL", impl);
    }

    private final static String UNICODE_NOT = "\u00AC";
    private final static String UNICODE_AND = "\u2227";
    private final static String UNICODE_OR = "\u2228";
    private final static String UNICODE_EQUI = "\u2194";
    private final static String UNICODE_IMPL = "\u2192";

    private final static String ASCII_NOT = "!";
    private final static String ASCII_AND = "AND";
    private final static String ASCII_OR = "OR";
    private final static String ASCII_EQUI = "<=>";
    private final static String ASCII_IMPL = "=>";
}
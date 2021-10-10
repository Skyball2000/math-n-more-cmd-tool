package de.yanwittmann.cmdtool.math;

import com.fathzer.soft.javaluator.Operator;
import de.yanwittmann.cmdtool.Main;
import de.yanwittmann.cmdtool.util.Util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class ExpressionEvaluation {

    public static List<String> extractVariables(String expression) {
        expression = expression.replace(")", " ").replace("(", " ");
        List<String> operands = TreeBooleanEvaluator.getPARAMETERS().getOperators().stream().map(Operator::getSymbol).collect(Collectors.toList());
        operands.add("TRUE");
        operands.add("FALSE");
        operands.add("TT");
        operands.add("FF");
        for (int i = operands.size() - 1; i >= 0; i--) operands.add(operands.get(i).toLowerCase());
        operands = Util.reverse(operands.stream().distinct().sorted(Comparator.comparing(String::length))).collect(Collectors.toList());
        for (String operator : operands) {
            if (operator.length() > 1) {
                expression = expression.replace(operator, " ");
            } else {
                String quoted = Pattern.quote(operator);
                expression = expression.replaceAll(quoted + "($| )", " ").replaceAll("(^| )" + quoted, " ");
            }
        }
        expression = expression.replaceAll("[^a-zA-Z0-9][01][^a-zA-Z0-9]", "");
        return Arrays.stream(expression.split(" "))
                .filter(v -> v.length() > 0)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static String buildTruthTableFromMultipleExpressions(List<String> variables, List<String> expressions) {
        // extract assignments from expressions
        Map<String, String> expressionsWithAssignment = new LinkedHashMap<>();
        for (String expression : expressions) {
            Matcher matcher = Pattern.compile("([a-zA-Z0-9]+) ?= ?(.+)").matcher(expression.replace("=>", " => "));
            if (matcher.find()) {
                expressionsWithAssignment.put(matcher.group(1), matcher.group(2));
            } else {
                expressionsWithAssignment.put(expression, expression);
            }
        }

        List<List<String>> columns = new ArrayList<>();
        List<String> headerRow = new ArrayList<>(variables);
        expressionsWithAssignment.entrySet().stream().map(e -> e.getValue().equals(e.getKey()) ? Main.normalizeExpressionOutput(e.getKey()) : e.getKey() + " = " + Main.normalizeExpressionOutput(e.getValue())).forEach(headerRow::add);
        columns.add(headerRow);

        // iterate through all input combinations to generate every row of the truth table
        TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();
        int amountCombinations = (int) Math.pow(2, variables.size());
        for (int i = 0; i < amountCombinations; i++) {
            List<String> inputs = Arrays.stream(String.format("%" + variables.size() + "s", Integer.toBinaryString(i)).replace(" ", "0").split("")).collect(Collectors.toList());
            List<String> row = new ArrayList<>(inputs);
            final MyEvaluatorContext<String> evaluationContext = buildEvaluationContextForVariables(variables, inputs);
            for (Map.Entry<String, String> expressionEntry : expressionsWithAssignment.entrySet()) {
                String result = TreeBooleanEvaluator.evaluate(evaluator, evaluationContext, expressionEntry.getValue()) ? "1" : "0";
                row.add(result);
                evaluationContext.set(expressionEntry.getKey(), result);
            }
            columns.add(row);
        }

        return Util.formatAsTable(columns, true, true);
    }

    /**
     * <ol>
     *     <li>Extract variables from expression</li>
     *     <li>Iterate over all possible combinations</li>
     *     <li>Evaluate the expression with the current variable context</li>
     * </ol>
     *
     * @param expression The expression to create the truth table for.
     * @return The table as a string.
     */
    public static String generateTruthTable(String expression) {
        TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();
        List<String> variables = extractVariables(expression);
        List<BasicTruthTableEntry> truthTableEntries = new ArrayList<>();

        int amountCombinations = (int) Math.pow(2, variables.size());
        for (int i = 0; i < amountCombinations; i++) {
            List<String> inputs = Arrays.stream(String.format("%" + variables.size() + "s", Integer.toBinaryString(i)).replace(" ", "0").split("")).collect(Collectors.toList());
            final MyEvaluatorContext<String> evaluationContext = buildEvaluationContextForVariables(variables, inputs);
            truthTableEntries.add(new BasicTruthTableEntry(evaluationContext, inputs));
        }

        truthTableEntries.forEach(e -> e.evaluate(evaluator, expression));
        List<List<String>> tableList = new ArrayList<>();
        List<String> tableHead = new ArrayList<>(variables);
        tableHead.add("out");
        tableList.add(tableHead);
        for (BasicTruthTableEntry entry : truthTableEntries) {
            List<String> tableRow = new ArrayList<>(entry.getInputs());
            tableRow.add(entry.getResult() ? "1" : "0");
            tableList.add(tableRow);
        }
        return Util.formatAsTable(tableList, true, true);
    }

    private static MyEvaluatorContext<String> buildEvaluationContextForVariables(List<String> variables, List<String> inputs) {
        final MyEvaluatorContext<String> evaluationContext = new MyEvaluatorContext<>();
        for (int j = 0; j < variables.size() && j < inputs.size(); j++)
            evaluationContext.set(variables.get(j), inputs.get(j));
        return evaluationContext;
    }

    private static class BasicTruthTableEntry {
        private Map<String, Boolean> results = new LinkedHashMap<>();
        private boolean result;
        private MyEvaluatorContext<String> evaluationContext;
        private List<String> inputs;

        public BasicTruthTableEntry(MyEvaluatorContext<String> evaluationContext, List<String> inputs) {
            this.evaluationContext = evaluationContext;
            this.inputs = inputs;
        }

        public boolean getResult() {
            return result;
        }

        public Map<String, Boolean> getResults() {
            return results;
        }

        public List<String> getInputs() {
            return inputs;
        }

        public void evaluate(TreeBooleanEvaluator evaluator, String expression) {
            result = TreeBooleanEvaluator.evaluate(evaluator, evaluationContext, expression);
            results.putAll(evaluationContext.getSequenceStack());
        }
    }


}

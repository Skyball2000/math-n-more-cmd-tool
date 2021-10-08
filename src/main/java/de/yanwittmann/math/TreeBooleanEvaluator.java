package de.yanwittmann.math;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import de.yanwittmann.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class TreeBooleanEvaluator extends AbstractEvaluator<String> {

    final static List<Operator> NOT_OPERATORS = new ArrayList<>();
    final static List<Operator> AND_OPERATORS = new ArrayList<>();
    final static List<Operator> OR_OPERATORS = new ArrayList<>();
    final static List<Operator> IMPL_OPERATORS = new ArrayList<>();
    final static List<Operator> EQUI_OPERATORS = new ArrayList<>();

    static {
        NOT_OPERATORS.add(new Operator("not", 1, Operator.Associativity.RIGHT, 5));
        NOT_OPERATORS.add(new Operator("NOT", 1, Operator.Associativity.RIGHT, 5));
        NOT_OPERATORS.add(new Operator("!", 1, Operator.Associativity.RIGHT, 5));
        NOT_OPERATORS.add(new Operator("¬", 1, Operator.Associativity.RIGHT, 5));
        AND_OPERATORS.add(new Operator("&&", 2, Operator.Associativity.LEFT, 4));
        AND_OPERATORS.add(new Operator("AND", 2, Operator.Associativity.LEFT, 4));
        AND_OPERATORS.add(new Operator("and", 2, Operator.Associativity.LEFT, 4));
        AND_OPERATORS.add(new Operator("∧", 2, Operator.Associativity.LEFT, 4));
        OR_OPERATORS.add(new Operator("||", 2, Operator.Associativity.LEFT, 3));
        OR_OPERATORS.add(new Operator("OR", 2, Operator.Associativity.LEFT, 3));
        OR_OPERATORS.add(new Operator("or", 2, Operator.Associativity.LEFT, 3));
        OR_OPERATORS.add(new Operator("∨", 2, Operator.Associativity.LEFT, 3));
        IMPL_OPERATORS.add(new Operator("=>", 2, Operator.Associativity.LEFT, 2));
        IMPL_OPERATORS.add(new Operator("IMPL", 2, Operator.Associativity.LEFT, 2));
        IMPL_OPERATORS.add(new Operator("impl", 2, Operator.Associativity.LEFT, 2));
        IMPL_OPERATORS.add(new Operator("→", 2, Operator.Associativity.LEFT, 2));
        EQUI_OPERATORS.add(new Operator("<=>", 2, Operator.Associativity.LEFT, 1));
        EQUI_OPERATORS.add(new Operator("EQUI", 2, Operator.Associativity.LEFT, 1));
        EQUI_OPERATORS.add(new Operator("equi", 2, Operator.Associativity.LEFT, 1));
        EQUI_OPERATORS.add(new Operator("↔", 2, Operator.Associativity.LEFT, 1));
    }

    private static final Parameters PARAMETERS;

    static {
        PARAMETERS = new Parameters();
        NOT_OPERATORS.forEach(PARAMETERS::add);
        AND_OPERATORS.forEach(PARAMETERS::add);
        OR_OPERATORS.forEach(PARAMETERS::add);
        IMPL_OPERATORS.forEach(PARAMETERS::add);
        EQUI_OPERATORS.forEach(PARAMETERS::add);
        PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
    }

    public TreeBooleanEvaluator() {
        super(PARAMETERS);
    }

    @Override
    protected String toValue(String literal, Object evaluationContext) {
        return literal;
    }

    private static boolean getValue(String literal) {
        if (literal != null && literal.length() > 0) {
            String lowercase = literal.toLowerCase();
            if ("t".equals(lowercase) || "tt".equals(lowercase) || "true".equals(lowercase) || "1".equals(lowercase) || lowercase.endsWith("=true"))
                return true;
            else if ("f".equals(lowercase) || "ff".equals(lowercase) || "false".equals(lowercase) || "0".equals(lowercase) || lowercase.endsWith("=false"))
                return false;
        }
        throw new IllegalArgumentException("Unknown literal: " + literal);
    }

    @Override
    protected Iterator<String> tokenize(String expression) {
        return super.tokenize(expression);
    }

    @Override
    protected String evaluate(Operator operator, Iterator<String> operands, Object evaluationContext) {
        boolean result;
        String eval;
        if (OR_OPERATORS.contains(operator)) {
            String o1 = operands.next();
            String o2 = operands.next();
            result = getValue(o1) || getValue(o2);
            eval = "(" + o1 + " " + operator.getSymbol() + " " + o2 + ")=" + result;
        } else if (AND_OPERATORS.contains(operator)) {
            String o1 = operands.next();
            String o2 = operands.next();
            result = getValue(o1) && getValue(o2);
            eval = "(" + o1 + " " + operator.getSymbol() + " " + o2 + ")=" + result;
        } else if (IMPL_OPERATORS.contains(operator)) {
            String o1 = operands.next();
            String o2 = operands.next();
            result = !getValue(o1) || getValue(o2);
            eval = "(" + o1 + " " + operator.getSymbol() + " " + o2 + ")=" + result;
        } else if (EQUI_OPERATORS.contains(operator)) {
            String o1 = operands.next();
            String o2 = operands.next();
            result = getValue(o1) == getValue(o2);
            eval = "(" + o1 + " " + operator.getSymbol() + " " + o2 + ")=" + result;
        } else if (NOT_OPERATORS.contains(operator)) {
            String o1 = operands.next();
            result = !getValue(o1);
            eval = "(" + operator.getSymbol() + (operator.getSymbol().equals("!") ? " " : "") + o1 + ")=" + result;
        } else {
            throw new IllegalArgumentException("Invalid operator: " + operator.getSymbol());
        }
        if (evaluationContext instanceof MyEvaluatorContext)
            ((MyEvaluatorContext<?>) evaluationContext).addToSequenceStack(eval.replace("=true", "").replace("=false", ""), result);
        return eval;
    }

    public static boolean evaluate(TreeBooleanEvaluator evaluator, MyEvaluatorContext<String> variables, String expression) {
        return getValue(evaluator.evaluate(expression, variables));
    }

    public static Set<String> extractVariables(String expression) {
        expression = expression.replace(")", " ").replace("(", " ");
        List<String> operands = PARAMETERS.getOperators().stream().map(Operator::getSymbol).collect(Collectors.toList());
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
                expression = expression.replaceAll(operator + "($| )", " ").replaceAll("(^| )" + operator, " ");
            }
        }
        expression = expression.replaceAll("[^a-zA-Z0-9][01][^a-zA-Z0-9]", "");
        return Arrays.stream(expression.split(" "))
                .filter(v -> v.length() > 0)
                .collect(Collectors.toSet());
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
        List<String> variables = extractVariables(expression).stream().sorted().collect(Collectors.toList());
        int amountCombinations = (int) Math.pow(2, variables.size());
        List<TruthTableEntry> truthTableEntries = new ArrayList<>();

        for (int i = 0; i < amountCombinations; i++) {
            final MyEvaluatorContext<String> evaluationContext = new MyEvaluatorContext<>();
            List<String> inputs = Arrays.stream(String.format("%" + variables.size() + "s", Integer.toBinaryString(i)).replace(" ", "0").split("")).collect(Collectors.toList());
            for (int j = 0; j < variables.size() && j < inputs.size(); j++) {
                evaluationContext.set(variables.get(j), inputs.get(j));
            }
            truthTableEntries.add(new TruthTableEntry(evaluationContext, inputs));
        }

        truthTableEntries.forEach(e -> e.evaluate(evaluator, expression));
        List<List<String>> tableList = new ArrayList<>();
        List<String> tableHead = new ArrayList<>(variables);
        tableHead.add("out");
        tableList.add(tableHead);
        for (TruthTableEntry entry : truthTableEntries) {
            List<String> tableRow = new ArrayList<>(entry.getInputs());
            tableRow.add(entry.getResult() ? "1" : "0");
            tableList.add(tableRow);
        }
        return Util.formatAsTable(tableList, true, true);
    }

    private static class TruthTableEntry {
        private Map<String, Boolean> results = new LinkedHashMap<>();
        private boolean result;
        private MyEvaluatorContext<String> evaluationContext;
        private List<String> inputs;

        public TruthTableEntry(MyEvaluatorContext<String> evaluationContext, List<String> inputs) {
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

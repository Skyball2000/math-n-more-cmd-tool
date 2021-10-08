package de.yanwittmann.cmdtool.math;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

import java.util.*;

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

    public static Parameters getPARAMETERS() {
        return PARAMETERS;
    }
}

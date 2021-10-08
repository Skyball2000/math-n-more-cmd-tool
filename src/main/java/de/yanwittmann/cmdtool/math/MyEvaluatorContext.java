package de.yanwittmann.cmdtool.math;

import com.fathzer.soft.javaluator.StaticVariableSet;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyEvaluatorContext<T> extends StaticVariableSet<T> {

    private final Map<String, Boolean> sequenceStack = new LinkedHashMap<>();

    public void addToSequenceStack(String expression, Boolean result) {
        sequenceStack.put(expression, result);
    }

    public Map<String, Boolean> getSequenceStack() {
        return sequenceStack;
    }
}

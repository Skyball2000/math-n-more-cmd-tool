package de.yanwittmann.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Util {

    public static String formatAsTable(List<List<String>> rows, boolean headerLine, boolean columnLines) {
        if (columnLines) {
            for (List<String> row : rows) {
                for (int i = row.size() - 1; i >= 1; i--) {
                    row.add(i, " ║ ");
                }
            }
        }

        int[] maxLengths = new int[rows.get(0).size()];
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        StringBuilder formatBuilder = new StringBuilder();
        formatBuilder.append((columnLines ? " " : ""));
        for (int maxLength : maxLengths) {
            formatBuilder.append("%-").append(maxLength + (columnLines ? 0 : 2)).append("s");
        }
        String format = formatBuilder.toString();

        if (headerLine) {
            int totalLength = Arrays.stream(maxLengths).sum();
            List<String> separator = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            int bound = totalLength + (columnLines ? 2 : maxLengths.length + rows.get(0).size() - 2);
            for (int i = 0; i < bound; i++) {
                sb.append("═");
            }
            separator.add(sb.toString());
            rows.add(1, separator);
        }

        StringBuilder result = new StringBuilder();
        String latestLine = null;
        for (List<String> row : rows) {
            if (row.get(0).startsWith("═")) {
                if (latestLine != null) {
                    char[] separatorArray = row.get(0).toCharArray();
                    char[] latestLineArray = latestLine.toCharArray();
                    for (int i = 0; i < latestLineArray.length && i < separatorArray.length; i++) {
                        if (latestLineArray[i] == '║') separatorArray[i] = '╬';
                    }
                    result.append(new String(separatorArray)).append("\n");
                } else {
                    result.append(row.get(0)).append("\n");
                }
            } else {
                latestLine = String.format(format, row.toArray(new String[0]));
                result.append(latestLine).append("\n");
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }
}

package de.yanwittmann.cmdtool.chart;

import de.yanwittmann.cmdtool.data.DataProvider;
import de.yanwittmann.j2chartjs.quick.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.*;

public class ChartCreator {

    private File csvFile;
    private File outFile;
    private String chartType;
    private QuickChart<?, ?> chart;
    private String title;
    private boolean startAtZero = true;

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartAtZero(boolean startAtZero) {
        this.startAtZero = startAtZero;
    }

    public void makeChart() throws IOException {
        Reader reader = new FileReader(csvFile);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        Map<String, List<Double>> entries = new LinkedHashMap<>();

        switch (chartType) {
            case "bar":
                chart = new QuickBarChart();
                break;
            case "line":
                chart = new QuickLineChart();
                break;
            case "pie":
                chart = new QuickPieChart();
                break;
            case "doughnut":
                chart = new QuickDoughnutChart();
                break;
            case "radar":
                chart = new QuickRadarChart();
                break;
        }
        chart.setIndexMode(true);
        chart.setBeginAtZero(startAtZero);
        if (title != null) chart.setTitle(title);

        int rowIndex = 0;
        for (CSVRecord record : csvParser.getRecords()) {
            if (rowIndex == 0) {
                for (int i = 0; true; i++) {
                    try {
                        entries.put(record.get(i), new ArrayList<>());
                    } catch (Exception e) {
                        break;
                    }
                }
                rowIndex++;
                continue;
            }
            List<String> headers = new ArrayList<>(entries.keySet());
            for (int i = 0; i < headers.size(); i++) {
                entries.get(headers.get(i)).add(Double.parseDouble(record.get(i).trim()));
            }
            chart.addLabels("Dataset " + rowIndex);
            rowIndex++;
        }

        for (Map.Entry<String, List<Double>> entry : entries.entrySet()) {
            if (chart instanceof QuickBarChart)
                ((QuickBarChart) chart).addDataset(entry.getKey(), entry.getValue());
            else if (chart instanceof QuickLineChart)
                ((QuickLineChart) chart).addDataset(entry.getKey(), entry.getValue());
            else if (chart instanceof QuickPieChart)
                ((QuickPieChart) chart).addDataset(entry.getKey(), entry.getValue());
            else if (chart instanceof QuickDoughnutChart)
                ((QuickDoughnutChart) chart).addDataset(entry.getKey(), entry.getValue());
            else if (chart instanceof QuickRadarChart)
                ((QuickRadarChart) chart).addDataset(entry.getKey(), entry.getValue());
        }
    }

    public void writeChart() throws IOException {
        if (chart == null) return;
        if (outFile == null)
            outFile = new File(
                    new File(System.getProperty("java.io.tmpdir"), "generated-charts"),
                    "chart-" + csvFile.getName() + new Random().nextInt(10000) + ".html"
            );
        if (!outFile.getParentFile().exists()) outFile.getParentFile().mkdirs();
        DataProvider.writeFile(outFile,
                CHART_EXPORT_TEMPLATE
                        .replace("INSERT-CHART-CONFIG", chart.build())
                        .replace("INSERT-CHART-TITLE", title == null ? "Generated Chart" : title + " - Chart")
        );
        System.out.println("Chart file written to " + outFile.getAbsolutePath());
    }

    public void openChart() throws IOException {
        Desktop.getDesktop().open(outFile);
    }

    private final static String CHART_EXPORT_TEMPLATE =
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>INSERT-CHART-TITLE</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js@3.5.1/dist/chart.min.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "<canvas id=\"chartCanvas\" style=\"border: gray 2px solid;\" width=\"100\" height=\"48\">\n" +
            "</canvas>\n" +
            "<script>\n" +
            "    const ctx = document.getElementById('chartCanvas').getContext('2d');\n" +
            "    const myChart = new Chart(ctx,INSERT-CHART-CONFIG);\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>";
}

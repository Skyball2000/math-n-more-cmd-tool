package de.yanwittmann.cmdtool.data;

import jnafilechooser.api.JnaFileChooser;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    private final File dataDir;
    private File generalData;
    private NotesData notesData;
    private NotesData historyData;

    public DataProvider(File dataDir) {
        this.dataDir = dataDir;
        createFileObjects();
        readDataFromDataDir();
    }

    private void createFileObjects() {
        generalData = new File(dataDir, FILENAME_GENERAL_DATA);
    }

    private void readDataFromDataDir() {
        JSONObject generalDataJson = null;
        try {
            if (generalData.exists()) generalDataJson = new JSONObject(String.join("", readFile(generalData)));
        } catch (IOException e) {
            System.out.println("Unable to read general data file: " + e.getMessage());
        }

        if (generalDataJson == null) {
            notesData = new NotesData(null);
        } else {
            notesData = new NotesData(generalDataJson.optJSONObject("notes"));
        }

        if (generalDataJson == null) {
            historyData = new NotesData(null);
        } else {
            historyData = new NotesData(generalDataJson.optJSONObject("history"));
            historyData.setMaxSize(15);
        }
    }

    public NotesData getNotesData() {
        return notesData;
    }

    public NotesData getHistoryData() {
        return historyData;
    }

    public void save() {
        JSONObject generalDataJson = new JSONObject();
        generalDataJson.put("notes", notesData.toJson());
        generalDataJson.put("history", historyData.toJson());
        try {
            writeFile(generalData, generalDataJson.toString());
        } catch (IOException e) {
            System.out.println("Unable to write general data file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public File pickFile(String filterName, String... filters) {
        JnaFileChooser fc = new JnaFileChooser();
        if (filterName != null && filterName.length() > 0)
            fc.addFilter(filterName, filters);
        fc.showOpenDialog(null);
        return fc.getSelectedFile();
    }

    private final static String FILENAME_GENERAL_DATA = "data";

    public static DataProvider createMainDataProvider() {
        String os = System.getProperty("os.name").toLowerCase();
        File dataDir;
        if (os.contains("win")) {
            dataDir = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "math-n-more-cmd-tool");
        } else if (os.contains("osx")) {
            dataDir = new File(System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "math-n-more-cmd-tool");
        } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
            dataDir = new File(System.getProperty("user.home") + File.separator + "application-data" + File.separator + "math-n-more-cmd-tool");
        } else {
            dataDir = new File(System.getProperty("user.home") + File.separator + ".math-n-more-cmd-tool");
        }
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            System.err.println("Unable to locate or create main data directory " + dataDir.getAbsolutePath());
            return null;
        }
        return new DataProvider(dataDir);
    }

    public static List<String> readFile(File file) throws IOException {
        try {
            return Files.readAllLines(file.toPath());
        } catch (Exception e) {
            return readFileFallback(file);
        }
    }

    private static List<String> readFileFallback(File file) throws IOException {
        List<String> result = new ArrayList<>();

        try (FileReader f = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            while (f.ready()) {
                char c = (char) f.read();
                if (c == '\n') {
                    result.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                result.add(sb.toString());
            }
        }

        return result;
    }

    public static void writeFile(File file, List<String> lines) throws IOException {
        writeFile(file, lines.toArray(new String[0]));
    }

    public static void writeFile(File file, String[] lines) throws IOException {
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < lines.length; i++) {
            outputWriter.write(lines[i]);
            if (i < lines.length - 1) outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public static void writeFile(File file, String text) throws IOException {
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter(file));
        outputWriter.write(text);
        outputWriter.flush();
        outputWriter.close();
    }
}

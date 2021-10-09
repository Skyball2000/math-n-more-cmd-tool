package de.yanwittmann.cmdtool.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotesData extends AbstractSavable {

    private JSONArray notesArray;
    private int maxSize = -1;

    public NotesData(JSONObject data) {
        if (data == null) {
            notesArray = new JSONArray();
            return;
        }
        notesArray = data.optJSONArray("entries");
        if (notesArray == null) notesArray = new JSONArray();
    }

    public String getNoteAtIndex(int i) {
        return notesArray.optString(i);
    }

    public List<String> getNotes() {
        List<String> notes = new ArrayList<>();
        for (int i = 0; i < notesArray.length(); i++) notes.add(notesArray.optString(i));
        return notes;
    }

    public void removeNote(int i) {
        notesArray.remove(i);
    }

    public int addNote(String note) {
        notesArray.put(note);
        limitSize();
        return notesArray.length() - 1;
    }

    public void clearNotes() {
        notesArray = new JSONArray();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        limitSize();
    }

    private void limitSize() {
        if (maxSize != -1 && notesArray.length() > maxSize)
            notesArray.remove(0);
    }

    @Override
    public JSONObject toJson() {
        JSONObject export = new JSONObject();
        export.put("entries", notesArray);
        return export;
    }
}

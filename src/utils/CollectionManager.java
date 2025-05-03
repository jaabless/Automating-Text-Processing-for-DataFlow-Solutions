package utils;

import model.TextManager;
import java.util.ArrayList;
import java.util.List;

public class CollectionManager {
    private final List<TextManager> entries = new ArrayList<>();

    public void addEntry(String content) {
        TextManager entry = new TextManager(content);
        if (!entries.contains(entry)) {
            entries.add(entry);
        }
    }

    public boolean updateEntry(int index, String newContent) {
        if (index >= 0 && index < entries.size()) {
            entries.set(index, new TextManager(newContent));
            return true;
        }
        return false;
    }

    public boolean deleteEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            return true;
        }
        return false;
    }

    public List<String> getAllEntries() {
        List<String> result = new ArrayList<>();
        for (TextManager entry : entries) {
            result.add(entry.toString());
        }
        return result;
    }
}

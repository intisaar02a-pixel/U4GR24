package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages highscores persisted in a .txt file.
 * File format: one entry per line: name;score
 *
 * @author Intisaar & Maya
 */
public class HighscoreManager {

    private final File file;
    private final List<HighscoreEntry> entries;

    /**
     * Creates manager with file.
     *
     * @param filePath path to highscores txt
     * @author Intisaar & Maya
     */
    public HighscoreManager(String filePath) {
        this.file = new File(filePath);
        this.entries = new ArrayList<>();
        load();
    }

    /**
     * Loads highscores from file if it exists.
     *
     * @author Intisaar & Maya
     */
    public void load() {
        entries.clear();
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 2) continue;
                String name = parts[0].trim();
                int score;
                try {
                    score = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                entries.add(new HighscoreEntry(name, score));
            }
            sortAndTrim();
        } catch (IOException e) {
            // For a beginner-friendly project: ignore read errors, keep empty list.
        }
    }

    /**
     * Saves highscores to file.
     *
     * @author Intisaar & Maya
     */
    public void save() {
        sortAndTrim();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (HighscoreEntry e : entries) {
                bw.write(e.name() + ";" + e.score());
                bw.newLine();
            }
        } catch (IOException e) {
            // Ignore write errors (could show dialog in controller if desired).
        }
    }

    /**
     * Returns a copy of top entries.
     *
     * @return list of entries
     * @author Intisaar & Maya
     */
    public List<HighscoreEntry> getTop10() {
        sortAndTrim();
        return new ArrayList<>(entries);
    }

    /**
     * Checks if a score qualifies for top 10.
     *
     * @param score score
     * @return true if qualifies
     * @author Intisaar & Maya
     */
    public boolean qualifies(int score) {
        sortAndTrim();
        if (entries.size() < 10) return score > 0;
        return score > entries.get(entries.size() - 1).score();
    }

    /**
     * Adds a new entry and persists.
     *
     * @param name name
     * @param score score
     * @author Intisaar & Maya
     */
    public void addEntry(String name, int score) {
        entries.add(new HighscoreEntry(name, score));
        sortAndTrim();
        save();
    }

    /**
     * Sort descending and keep only 10.
     *
     * @author Intisaar & Maya
     */
    private void sortAndTrim() {
        entries.sort(Comparator.comparingInt(HighscoreEntry::score).reversed());
        while (entries.size() > 10) {
            entries.remove(entries.size() - 1);
        }
    }
}
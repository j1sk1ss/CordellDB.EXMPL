package org.cordell.com.cordelldb.manager;

import org.codehaus.plexus.util.FileUtils;
import org.cordell.com.cordelldb.common.Triple;
import org.cordell.com.cordelldb.objects.ObjectRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Manager {
    /**
     * Init manager by path (Or create, if manager not exists)
     * @param file Path to manager
     */
    public Manager(String file) {
        dbPath = Paths.get(file);
        if (!Files.exists(dbPath.toAbsolutePath())) {
            try {
                if (!dbPath.toFile().createNewFile()) System.out.println("Error creating file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create new manager in location
     * @param location Location of manager
     * @param fileName Manager name (without extension)
     */
    public Manager(String location, String fileName) {
        dbPath = Paths.get(location + fileName);

        var file = new File(location + fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) System.out.println("Error creating file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Copy manager db to new destination
     * @param source Source manager
     * @param location New location
     * @param fileName New filename
     */
    public Manager(Manager source, String location, String fileName) {
        dbPath = Paths.get(location + fileName);

        try {
            var sourceFile = source.dbPath.toFile();
            var destinationFile = dbPath.toFile();
            if (!destinationFile.exists()) {
                try {
                    if (!destinationFile.createNewFile()) System.out.println("Error creating file");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            FileUtils.copyDirectory(sourceFile, destinationFile);
        } catch (IOException e) {
            System.out.println("Error creating file");
        }
    }

    private final Path dbPath;

    /**
     * Set string of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void setString(String key, String value) throws IOException {
        var val = getRecord(key);
        if (val== null) {
            addLine2File(key + ":" + value);
            return;
        }

        var lines = load();
        lines.set(val.x, val.y + ":" + value);
        putLine2File(val.x, lines.get(val.x));
    }

    /**
     * Get string from DB
     * @param key Key of value
     * @return String
     * @throws IOException Exception when something goes wrong
     */
    public String getString(String key) throws IOException {
        var val = getRecord(key);
        if (val == null) return null;
        return val.z.asString();
    }

    /**
     * Set integer of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void setInt(String key, int value) throws IOException {
        setString(key, Integer.toString(value));
    }

    /**
     * Get integer from DB
     * @param key Key of value
     * @return Integer
     * @throws IOException Exception when something goes wrong
     */
    public int getInt(String key) throws IOException {
        var val = getRecord(key);
        if (val == null) return -1;
        return val.z.asInteger();
    }

    /**
     * Set double of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void setDouble(String key, double value) throws IOException {
        setString(key, Double.toString(value));
    }

    /**
     * Get double from DB
     * @param key Key of value
     * @return Double
     * @throws IOException Exception when something goes wrong
     */
    public double getDouble(String key) throws IOException {
        var val = getRecord(key);
        if (val == null) return -1d;
        return val.z.asDouble();
    }

    /**
     * Set boolean of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void setBoolean(String key, boolean value) throws IOException {
        setString(key, Boolean.toString(value));
    }

    /**
     * Get boolean from DB
     * @param key Key of value
     * @return Boolean
     * @throws IOException Exception when something goes wrong
     */
    public boolean getBoolean(String key) throws IOException {
        var val = getRecord(key);
        if (val == null) return false;
        return val.z.asBoolean();
    }

    /**
     * Rewrite all db without key
     * @param key Key for delete
     * @throws IOException Exception when something goes wrong
     */
    public void deleteRecord(String key) throws IOException {
        var key2delete = getRecord(key);
        if (key2delete == null) return;
        deleteLineFromFile(key2delete.x);
    }

    /**
     * Get record from DB
     * @param key key of value
     * @return null if not found |
     * Triple where x - index in file, y - key, z - record
     */
    public Triple<Integer, String, ObjectRecord> getRecord(String key) throws IOException {
        var lines = load();
        for (var i = 0; i < lines.size(); i++) {
            var pair = lines.get(i).split(":");
            if (pair[0].equals(key))
                return new Triple<>(i, pair[0], new ObjectRecord(pair[1]));
        }

        return null;
    }

    private void addLine2File(String line) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(dbPath.toAbsolutePath().toString(), true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    private void putLine2File(int index, String line) throws IOException {
        var lines = load();
        if (index >= lines.size() || index < 0) return;

        lines.set(index, line);
        save(lines);
    }

    private void deleteLineFromFile(int index) throws IOException {
        var lines = load();
        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
            save(lines);
        }
        else throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    private void save(List<String> data) throws IOException {
        Files.write(dbPath, data);
    }

    private List<String> load() throws IOException {
        return Files.readAllLines(dbPath);
    }
}

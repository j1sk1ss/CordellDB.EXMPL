package org.cordell.com.cordelldb.manager;

import org.codehaus.plexus.util.FileUtils;
import org.cordell.com.cordelldb.common.Triple;
import org.cordell.com.cordelldb.objects.ObjectRecord;
import org.cordell.com.cordelldb.threads.SaveThread;

import java.io.*;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class Manager {
    /**
     * Init manager by path (Or create, if manager not exists)
     * @param file Path to manager
     */
    public Manager(String file) {
        dbPath = Paths.get(file);
        temporaryStorage = new CopyOnWriteArrayList<>();

        if (!Files.exists(dbPath.toAbsolutePath())) {
            try {
                if (!dbPath.toFile().createNewFile()) System.out.println("Error creating file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveThread = new SaveThread(this, 100000);
        saveThread.start();
    }

    /**
     * Create new manager in location
     * @param location Location of manager
     * @param fileName Manager name (without extension)
     */
    public Manager(String location, String fileName) {
        dbPath = Paths.get(location + fileName);
        temporaryStorage = new CopyOnWriteArrayList<>();

        var file = new File(location + fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) System.out.println("Error creating file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveThread= new SaveThread(this, 100000);
        saveThread.start();
    }

    /**
     * Copy manager db to new destination
     * @param source Source manager
     * @param location New location
     * @param fileName New filename
     */
    public Manager(Manager source, String location, String fileName) {
        dbPath = Paths.get(location + fileName);
        temporaryStorage = new CopyOnWriteArrayList<>();

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

        saveThread = new SaveThread(this, 1000);
        saveThread.start();
    }

    private final Path dbPath;
    private final CopyOnWriteArrayList<Triple<Integer, String, ObjectRecord>> temporaryStorage;
    private SaveThread saveThread;

    /**
     * Return key set from DB
     * @return Key set
     */
    public List<String> getKeySet() {
        var output = new ArrayList<String>();
        for (var triple : temporaryStorage)
            output.add(triple.y());

        return output;
    }

    /**
     * Set string of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void setString(String key, String value) throws IOException {
        var val = getRecord(key);
        if (val == null) {
            temporaryStorage.add(new Triple<>(temporaryStorage.size(), key, new ObjectRecord(value)));
            return;
        }

        temporaryStorage.set(val.x(), new Triple<>(val.x(), val.y(), new ObjectRecord(value)));
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
        return val.z().asString();
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
     */
    public int getInt(String key) {
        var val = getRecord(key);
        if (val == null) return -1;
        return val.z().asInteger();
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
     */
    public double getDouble(String key) {
        var val = getRecord(key);
        if (val == null) return -1d;
        return val.z().asDouble();
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
     */
    public boolean getBoolean(String key) {
        var val = getRecord(key);
        if (val == null) return false;
        return val.z().asBoolean();
    }

    /**
     * Rewrite all db without key
     * @param key Key for delete
     */
    public void deleteRecord(String key) {
        var key2delete = getRecord(key);
        if (key2delete == null) return;
        temporaryStorage.remove(key2delete);
    }

    /**
     * Get record from DB
     * @param key key of value
     * @return null if not found |
     * Triple where x - index in file, y - key, z - record
     */
    public Triple<Integer, String, ObjectRecord> getRecord(String key) {
        try {
            if (temporaryStorage == null || temporaryStorage.isEmpty()) {
                return null;
            }

            return temporaryStorage.parallelStream()
                    .filter(line -> line.y().equals(key))
                    .findFirst()
                    .orElse(null);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Get all records with specified value
     * @param value Value
     * @return List of records
     */
    public List<Triple<Integer, String, ObjectRecord>> getKeys(String value) {
        var records = new CopyOnWriteArrayList<Triple<Integer, String, ObjectRecord>>();
        temporaryStorage.parallelStream().forEach(line -> {
            if (line.y().equals(value)) {
                records.add(line);
            }
        });

        return records;
    }

    /**
     * Get name of manager body file
     * @return Name of file
     */
    public String getName() {
        return dbPath.getFileName().toString();
    }

    public void save() throws IOException {
        var linesToSave = new ArrayList<String>();
        for (var line : temporaryStorage) {
            linesToSave.add(line.y() + ":" + line.z().asString());
        }

        Files.write(dbPath, linesToSave);
    }

    public void load() throws IOException {
        var lines = Files.readAllLines(dbPath);
        temporaryStorage.clear();

        for (int i = 0; i < lines.size(); i++) {
            try {
                var line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                var pair = line.split(":", 2);
                if (pair.length != 2) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                temporaryStorage.add(new Triple<>(i, pair[0], new ObjectRecord(pair[1])));
            } catch (Exception e) {
                System.out.println("Error processing line " + i + ": " + e.getMessage());
            }
        }
    }
}

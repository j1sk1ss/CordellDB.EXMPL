package org.cordell.com.cordelldb.manager;

import org.cordell.com.cordelldb.common.Triple;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Manager {
    public Manager(String location, String fileName) {
        Path = location;
        FileName = fileName;

        var file = new File(Path + FileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) System.out.println("Error creating file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final String Path;
    private final String FileName;

    /**
     * Add new key
     * @param key Key
     * @param value Value
     * @throws IOException Exception when something goes wrong
     */
    public void AddRecord(String key, String value) throws IOException {
        if (getRecord(key) == null)
            addLine2File(key + ":" + value);
    }

    /**
     * Set value of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void SetRecord(String key, String value) throws IOException {
        var val = getRecord(key);
        if (val== null) return;

        var data = load().split("\n");
        data[val.x] = val.y + ":" + value;
        putLine2File(val.x, data[val.x]);
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
     * Get value of key in DB
     * @param key key of value
     * @return null if not found |
     * Triple where x - index in file, y - key, z - value
     */
    public Triple<Integer, String, String> getRecord(String key) throws IOException {
        var data = load().split("\n");
        for (var i = 0; i < data.length; i++) {
            var pair = data[i].split(":");
            if (pair[0].equals(key))
                return new Triple<>(i, pair[0], pair[1]);
        }

        return null;
    }

    private void addLine2File(String line) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(Path + FileName, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    private void putLine2File(int index, String line) throws IOException {
        var data = load();
        var splitData = data.split("\n");
        if (index >= splitData.length || index < 0) return;

        splitData[index] = line;
        data = String.join("\n", splitData);

        save(data);
    }

    private void deleteLineFromFile(int index) throws IOException {
        var path = Paths.get(Path + FileName);
        var lines = Files.readAllLines(path);

        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
            Files.write(path, lines);
        } else throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    private void save(String data) throws IOException {
        Files.write(Paths.get(Path + FileName), data.getBytes());
    }

    private String load() throws IOException {
        return new String(Files.readAllBytes(Paths.get(Path + FileName)));
    }
}

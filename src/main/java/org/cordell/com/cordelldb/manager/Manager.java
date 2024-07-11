package org.cordell.com.anizottieconomy.db;

import org.cordell.com.anizottieconomy.common.Triple;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;


public class DataManager {
    public DataManager(String location, String fileName) {
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
    public void AddKey(String key, String value) throws IOException {
        if (GetValue(key) == null) AppendLine(key + ":" + value);
    }

    /**
     * Rewrite all db without key
     * @param key Key for delete
     * @throws IOException Exception when something goes wrong
     */
    public void DeleteKey(String key) throws IOException {
        var key2delete = GetValue(key);
        if (key2delete == null) return;

        var data = LoadData().split("\n");
        var cleanData = new StringBuilder();
        for (var i = 0; i < data.length; i++) {
            if (i == key2delete.x) continue;
            cleanData.append(data[i]).append("\n");
        }

        SaveData(cleanData.toString());
    }

    /**
     * Set value of key
     * @param key Key of value
     * @param value New value of key
     * @throws IOException Exception when something goes wrong
     */
    public void SetValue(String key, String value) throws IOException {
        var val = GetValue(key);
        if (val== null) return;

        var data = LoadData().split("\n");
        data[val.x] = val.y + ":" + value;

        SaveData(String.join("\n", data));
    }

    /**
     * Get value of key in DB
     * @param key key of value
     * @return null if not found
     */
    public Triple<Integer, String, String> GetValue(String key) throws IOException {
        var data = LoadData().split("\n");
        for (var i = 0; i < data.length; i++) {
            var pair = data[i].split(":");
            if (pair[0].equals(key))
                return new Triple<>(i, pair[0], pair[1]);
        }

        return null;
    }

    private void PutLine(int index, String line) throws IOException {
        var data = LoadData();
        var splitData = data.split("\n");

        splitData[index] = line;
        data = String.join("\n", splitData);

        SaveData(data);
    }

    private void AppendLine(String line) throws IOException {
        var writer = new FileWriter(Path + FileName);
        writer.append(line).append("\n");
        writer.close();
    }

    private String LoadLine(int index) throws IOException {
        return LoadData().split("\n")[index];
    }

    private void SaveData(String data) throws IOException {
        var writer = new FileWriter(Path + FileName);
        writer.write(data);
        writer.close();
    }

    private String LoadData() throws IOException {
        var reader = new FileReader(Path + FileName);
        var scan = new Scanner(reader);

        var data = new StringBuilder();
        while (scan.hasNextLine())
            data.append(scan.nextLine());

        reader.close();
        return data.toString();
    }
}

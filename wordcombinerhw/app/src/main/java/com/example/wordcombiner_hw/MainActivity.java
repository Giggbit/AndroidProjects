package com.example.wordcombiner_hw;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String WORDS_URL = "https://raw.githubusercontent.com/sunmeat/storage/main/text/dictionary1.txt";
    private static final String TAG = "WordCombiner";
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        new Thread(() -> {
            try {
                List<String> words = downloadWords();
                List<String> combinedWords = combineWords(words);
                saveWordsToFile(combinedWords);
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage(), e);
            }
        }).start();
    }

    private List<String> downloadWords() throws Exception {
        List<String> words = new ArrayList<>();
        URL url = new URL(WORDS_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
            reader.close();
        } else {
            throw new Exception("Failed to download words");
        }

        return words;
    }

    private List<String> combineWords(List<String> words) {
        List<String> combinedWords = new ArrayList<>();
        int wordCount = words.size();

        for (int i = 0; i < wordCount; i++) {
            String word1 = words.get(i);
            if (word1.length() < 4) continue;

            String suffix = word1.substring(word1.length() - 4);
            for (int j = 0; j < wordCount; j++) {
                String word2 = words.get(j);
                if (word2.length() < 4 || i == j) continue;

                String prefix = word2.substring(0, 4);
                if (suffix.equals(prefix)) {
                    combinedWords.add(word1 + word2.substring(4));
                }
            }
        }

        Log.d(TAG, "Combined " + combinedWords.size() + " words");
        return combinedWords;
    }

    private void saveWordsToFile(List<String> words) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "combined_words.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (String word : words) {
                writer.write(word);
                writer.newLine(); 
            }

            writer.close();
            runOnUiThread(() -> Toast.makeText(this, "Файл сохранен в " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());

        } catch (Exception e) {
            Log.e(TAG, "Failed to save words to file", e);
        }
    }
}

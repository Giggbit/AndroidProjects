package com.example.hw_recyclerview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<String> imageUrls = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            imageUrls.add("https://picsum.photos/300/200?random=" + i);
        }

        ImageAdapter adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
    }
}

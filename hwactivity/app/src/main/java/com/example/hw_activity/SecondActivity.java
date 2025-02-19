package com.example.hw_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button = findViewById(R.id.button2);

        int receivedNumber = getIntent().getIntExtra("number", 1);
        button.setText(String.valueOf(receivedNumber));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedNumber = receivedNumber + 1;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("number", updatedNumber);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
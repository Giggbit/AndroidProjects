package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSend, btnGetData;
    private TextView textViewData;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI-компонентов
        editText = findViewById(R.id.editText);
        btnSend = findViewById(R.id.btnSend);
        btnGetData = findViewById(R.id.btnGetData);
        textViewData = findViewById(R.id.textViewData);

        // Инициализация Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        // Обработчик кнопки "Надіслати"
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if (!message.isEmpty()) {
                    databaseReference.push().setValue(message);
                    Toast.makeText(MainActivity.this, "Повідомлення надіслано!", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Введіть текст", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработчик кнопки "Отримати"
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromFirebase();
            }
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> messages = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String message = data.getValue(String.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                textViewData.setText(messages.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read data", error.toException());
            }
        });
    }
}

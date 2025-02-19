package com.example.hw_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setText("1");

        // Обработчик нажатия на кнопку
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем текущее число с кнопки
                int currentNumber = Integer.parseInt(button.getText().toString());

                // Создаем Intent для перехода ко второму Activity
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("number", currentNumber);  // Передаем число в Intent
                startActivityForResult(intent, 1);  // Ожидаем результат из второго Activity
            }
        });
    }

    // Обработка результата из второго Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Получаем обновленное число из Intent
            int updatedNumber = data.getIntExtra("number", 1);
            button.setText(String.valueOf(updatedNumber));  // Обновляем текст кнопки
        }
    }
}
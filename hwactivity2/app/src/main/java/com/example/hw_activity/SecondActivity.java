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

        // Получаем число, переданное из первого Activity
        int receivedNumber = getIntent().getIntExtra("number", 1);
        button.setText(String.valueOf(receivedNumber));

        // Обработчик нажатия на кнопку
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Увеличиваем число на 1
                int updatedNumber = receivedNumber + 1;

                // Создаем Intent для возвращения в первое Activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("number", updatedNumber);  // Передаем обновленное число
                setResult(RESULT_OK, resultIntent);  // Устанавливаем результат для первого Activity
                finish();  // Закрываем второе Activity и возвращаемся в первое
            }
        });
    }
}
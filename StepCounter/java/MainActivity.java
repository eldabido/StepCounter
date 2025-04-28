package com.example.stepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Менеджер для работы с сенсорами.
    private SensorManager mSensorManager = null;
    // Сенсор для подсчета шагов.
    private Sensor stepSensor;
    // Общее количество шагов.
    private int totalSteps = 0;
    // Предыдущее общее количество шагов (для сброса).
    private int previewsTotalSteep = 0;
    // Прогресс бар для отображения прогресса.
    private ProgressBar progressBar;
    // Текстовое поле для отображения количества шагов.
    private TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);
        // Сброс шагов и загрузка данных.
        resetSteps();
        loadData();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    protected void onResume() {
        super.onResume();
        // Проверка наличия сенсора и регистрация слушателя.
        if (stepSensor == null) {
            Toast.makeText(this, "У этого устройства нет сенсора", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        // Отмена регистрации слушателя при приостановке активности.
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Обработка изменения данных сенсора
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Обновление общего количества шагов
            totalSteps = (int)event.values[0];
            // Вычисление текущего количества шагов
            int currentSteps = totalSteps - previewsTotalSteep;
            // Обновление текстового поля и прогресс бара
            steps.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps);
        }
    }

    private void resetSteps() {
        // Обработка короткого нажатия на текстовое поле
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Длинное нажатие для сброса", Toast.LENGTH_SHORT).show();
            }
        });

        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previewsTotalSteep = totalSteps;
                steps.setText("0");
                progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }

    // Метод для сохранения данных.
    private void saveData() {
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key1", String.valueOf(previewsTotalSteep));
        editor.apply();
    }

    // Метод для загрузки данных.
    private void loadData() {
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int savedNumber = (int) sharedPref.getFloat("key1", 0f);
        previewsTotalSteep = savedNumber;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
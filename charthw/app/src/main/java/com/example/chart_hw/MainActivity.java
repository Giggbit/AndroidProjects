package com.example.chart_hw;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineChart lineChart = findViewById(R.id.lineChart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 100));
        entries.add(new Entry(1, 105));
        entries.add(new Entry(2, 102));
        entries.add(new Entry(3, 110));
        entries.add(new Entry(4, 120));

        LineDataSet lineDataSet = new LineDataSet(entries, "Bitcoin Price");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1500);
    }
}

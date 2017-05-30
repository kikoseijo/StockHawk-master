package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private LineChart historyChart;
    private TextView textViewSymbol;
    private String symbolToShow = "---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyChart = (LineChart) findViewById(R.id.historyLineChart);
        textViewSymbol = (TextView)findViewById(R.id.textViewSymbol);

        if (getIntent().hasExtra("symbol"))
        {
            symbolToShow = getIntent().getStringExtra("symbol");
        }
        else
        {
            //handle the error
            System.out.println("no symbol extra!");
        }

        textViewSymbol.setText(symbolToShow);

        List<Entry> entries = new ArrayList<Entry>();
        final List<Long> xAxisValues = new ArrayList<>();
        int xAxisPosition = 0;

        String selection = Contract.Quote.COLUMN_SYMBOL + " = ?";
        String[] selectionArgs = {symbolToShow};
        Cursor c = getContentResolver().query(Contract.Quote.URI, null, selection, selectionArgs, null);

        c.moveToFirst();
        String history = c.getString(Contract.Quote.POSITION_HISTORY);
        c.close();

        String[] historyMoments = history.split("\n");


        if (historyMoments.length == 1)
        {
            System.out.println("There is no history for this Stock");

            entries.add(new Entry(xAxisPosition, 34.67f));
            xAxisValues.add(Long.parseLong("4000000000"));
            xAxisPosition++;
            entries.add(new Entry(xAxisPosition, 37.12f));
            xAxisValues.add(Long.parseLong("5000000000"));
            xAxisPosition++;
            entries.add(new Entry(xAxisPosition, 26.05f));
            xAxisValues.add(Long.parseLong("6000000000"));
            xAxisPosition++;
            entries.add(new Entry(xAxisPosition, 25.67f));
            xAxisValues.add(Long.parseLong("7000000000"));
            xAxisPosition++;
            entries.add(new Entry(xAxisPosition, 29.80f));
            xAxisValues.add(Long.parseLong("8000000000"));
            xAxisPosition++;
            entries.add(new Entry(xAxisPosition, 33.13f));
            xAxisValues.add(Long.parseLong("9000000000"));

        } else {

            for (String mom : historyMoments) {
                long timeInMillis = Long.parseLong(mom.split(", ")[0]);
                float price = Float.parseFloat(mom.split(", ")[1]);

                xAxisValues.add(timeInMillis);

                Entry entry = new Entry(xAxisPosition, price);
                entries.add(entry);
                xAxisPosition++;
            }
        }


        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.stock_history));
        dataSet.setColor(R.color.colorDetail2);
        dataSet.setValueTextColor(R.color.colorPrimary);

        final XAxis xAxis = historyChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date(xAxisValues.get((int)value));
        xAxis.setGranularity(1);

        return  new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
            }
        });

        LineData lineData = new LineData(dataSet);

        historyChart.setDragEnabled(true);
        historyChart.setScaleEnabled(true);
        historyChart.fitScreen();
        historyChart.setData(lineData);
        historyChart.invalidate(); // refresh
    }
}

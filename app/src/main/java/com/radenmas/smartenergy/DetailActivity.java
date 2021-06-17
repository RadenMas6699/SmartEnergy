package com.radenmas.smartenergy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.smartenergy.databinding.ActivityDetailBinding;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    LineChart chart;

    FirebaseDatabase database;
    DatabaseReference reference;

    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    Calendar calendar;
    SimpleDateFormat dateFormat, clockFormat;

    private String type, reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getData from Intent
        type = getIntent().getStringExtra("type");
        reff = getIntent().getStringExtra("reff");
        //setType
        binding.tvType.setText(type);

        //setDatabase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Smart Energy");

        retrieveData();

        setDigital();

    }

    private void setDigital() {
        Query query = reference.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    DecimalFormat df = new DecimalFormat("#");

                    String dataTime = child.child("time").getValue().toString();
                    String dataReff = child.child(reff).getValue().toString();
                    long time = Long.parseLong(dataTime);

                    float dataSensor = Float.parseFloat(dataReff);

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(time * 1000);

                    String date = DateFormat.format("dd MMM yyyy", cal).toString();
                    String clock = DateFormat.format("HH:mm zz", cal).toString();

                    binding.tvDate.setText(date);
                    binding.tvClock.setText(clock);

                    switch (type) {
                        case "Tegangan":
                            binding.tvValue.setText(df.format(dataSensor) + "   V");
                            break;
                        case "Arus":
                            binding.tvValue.setText(df.format(dataSensor) + "   A");
                            break;
                        case "Daya":
                            binding.tvValue.setText(df.format(dataSensor) + "   W");
                            break;
                        case "CosPhi":
                            binding.tvValue.setText(df.format(dataSensor));
                            break;
                        case "Frekuensi":
                            binding.tvValue.setText(df.format(dataSensor) + "   Hz");
                            break;
                        case "Energy":
                            binding.tvValue.setText(df.format(dataSensor) + "   kWh");
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void retrieveData() {
        Query query = reference.orderByKey().limitToLast(200);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> data = new ArrayList<>();

                if (snapshot.hasChildren()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        DataPoints dataPoints = child.getValue(DataPoints.class);

                        switch (type) {
                            case "Tegangan":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getVolt()));
                                break;
                            case "Arus":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getArus()));
                                break;
                            case "Daya":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getPower()));
                                break;
                            case "CosPhi":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getCos_phi()));
                                break;
                            case "Frekuensi":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getFrequensi()));
                                break;
                            case "Energy":
                                data.add(new Entry(dataPoints.getTime(), dataPoints.getEnergy()));
                                break;
                        }

                    }
                    showChart(data);
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet.setCubicIntensity(0.05f);
                } else {
                    binding.chart.clear();
                    binding.chart.invalidate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showChart(ArrayList<Entry> data) {
        lineDataSet.setValues(data);
        lineDataSet.setLabel("DataSet 1");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawValues(false);
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0f);//45
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(10f);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(3, true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Date date = new Date((long) value);

                calendar = Calendar.getInstance(Locale.ENGLISH);
                clockFormat = new SimpleDateFormat("HH:mm zz");
                String s = clockFormat.format(date);

//                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//                cal.setTimeInMillis(date * 1000);
//
//                String clock = DateFormat.format("HH:mm zz", cal).toString();

                return s;
            }
        });

        YAxis yAxisL = binding.chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisL.setDrawGridLines(false);
        yAxisL.setDrawLabels(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        binding.chart.setMarker(mv);

        binding.chart.getLegend().setEnabled(false);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.getAxisRight().setEnabled(false);
        binding.chart.getAxisLeft().setEnabled(false);
        binding.chart.getXAxis().setEnabled(false);
        binding.chart.notifyDataSetChanged();
        binding.chart.clear();
        binding.chart.setData(lineData);
        binding.chart.invalidate();
        binding.chart.moveViewTo(lineData.getEntryCount(), 1000L, YAxis.AxisDependency.LEFT);
    }


//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        switch (i) {
//            case R.id.oneHour:
//                Toast.makeText(this, "1 Jam", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.threeHours:
//                Toast.makeText(this, "3 Jam", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                Toast.makeText(this, "30 Menit", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void Back(View view) {
        onBackPressed();
    }
}
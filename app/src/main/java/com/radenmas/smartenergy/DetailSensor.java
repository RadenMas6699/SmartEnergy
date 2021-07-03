package com.radenmas.smartenergy;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DetailSensor extends AppCompatActivity {

    private TextView tvType, tvDate, tvClock, tvValue, halfHour, oneHour, threeHours, oneDay, oneWeek, oneMonth;

    private String type, reff;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private LineChart chart;
    private LineDataSet lineDataSet = new LineDataSet(null, null);
    private ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    private LineData lineData;

    private String path = "Smart Energy";
    int int30menit = 450;
    int int1jam = 900;
    int int3jam = 2700;
    int int1hari = 21600;
    int int1minggu = 151200;
    int int1bulan = 648000;

    //        450
    //        900
    //        2700
    //        21600
    //        151200
    //        648000

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_sensor);

        init();
        onClick();


        //getData from Intent
        type = getIntent().getStringExtra("type");

        //setType
        tvType.setText(type);

        //setDatabase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(path);

        //show data digital
        setDigital();


        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    private void setDigital() {
        Query query = reference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    DecimalFormat df = new DecimalFormat("#");
                    DecimalFormat koma = new DecimalFormat("#.##");

                    DataPoints dataPoints = child.getValue(DataPoints.class);

                    long time = dataPoints.getTime();

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(time * 1000);

                    String date = DateFormat.format("dd MMM yyyy", cal).toString();
                    String clock = DateFormat.format("HH:mm zz", cal).toString();

                    tvDate.setText(date);
                    tvClock.setText(clock);

                    switch (type) {
                        case "Tegangan":
                            float dataVolt = (float) dataPoints.getVolt();
                            tvValue.setText(df.format(dataVolt) + "   V");
                            break;
                        case "Arus":
                            float dataArus = dataPoints.getArus();
                            tvValue.setText(koma.format(dataArus) + "   A");
                            break;
                        case "Daya":
                            float dataDaya = (float) dataPoints.getPower();
                            tvValue.setText(df.format(dataDaya) + "   W");
                            break;
                        case "CosPhi":
                            float dataCos = dataPoints.getCos_phi();
                            tvValue.setText(koma.format(dataCos) + "   Cos φ");
                            break;
                        case "Frekuensi":
                            float dataFreq = (float) dataPoints.getFrequensi();
                            tvValue.setText(df.format(dataFreq) + "   Hz");
                            break;
                        case "Energy":
                            float dataEnergy = dataPoints.getEnergy();
                            tvValue.setText(koma.format(dataEnergy) + "   kWh");
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        tvType = findViewById(R.id.tvType);
        chart = findViewById(R.id.chart);

        tvDate = findViewById(R.id.tvDate);
        tvClock = findViewById(R.id.tvClock);
        tvValue = findViewById(R.id.tvValue);

        halfHour = findViewById(R.id.halfHour);
        oneHour = findViewById(R.id.oneHour);
        threeHours = findViewById(R.id.threeHours);
        oneDay = findViewById(R.id.oneDay);
        oneWeek = findViewById(R.id.oneWeek);
        oneMonth = findViewById(R.id.oneMonth);
    }

    private void onClick() {
        halfHour.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int30menit);
        });

        oneHour.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int1jam);
        });

        threeHours.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int3jam);
        });

        oneDay.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int1hari);
        });

        oneWeek.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int1minggu);
        });

        oneMonth.setOnClickListener(v -> {
            chart.invalidate();
            GraphSuhu(int1bulan);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (halfHour.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int30menit);
        } else if (oneHour.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int1jam);
        } else if (threeHours.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int3jam);
        } else if (oneDay.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int1hari);
        } else if (oneWeek.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int1minggu);
        } else if (oneMonth.callOnClick()) {
            chart.invalidate();
            GraphSuhu(int1bulan);
        } else {
            chart.invalidate();
            GraphSuhu(int30menit);
        }
    }

    public void GraphSuhu(final int limit) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        Query query = databaseReference.orderByKey().limitToLast(limit);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> data = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
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
                    chart.invalidate();
                } else {
                    chart.clear();
                    chart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showChart(ArrayList<Entry> data) {
        lineDataSet.setValues(data);
        lineDataSet.setLabel("DataSet 1");
        lineDataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue_light);
            lineDataSet.setFillDrawable(drawable);
        } else {
            lineDataSet.setFillAlpha(5);
        }
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.05f);
        lineDataSet.setDrawValues(false);
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0f);//45
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(false);
        xAxis.setLabelCount(3, true);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                Date date = new Date((long) value);
//                SimpleDateFormat fmt;
//                fmt = new SimpleDateFormat("HH:mm zz");
//                fmt.setTimeZone(TimeZone.getDefault());
//                String s = fmt.format(date);
//                return s;
//            }
//        });

        YAxis yAxisL = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisL.setDrawGridLines(false);
        yAxisL.setDrawLabels(false);

//        switch (type) {
//            case "Tegangan":
//                yAxisL.setAxisMinimum(110);
//                yAxisL.setAxisMaximum(330);
//                break;
//            case "Arus":
//                yAxisL.setAxisMinimum(0);
//                yAxisL.setAxisMaximum(20);
//                break;
//            case "Daya":
//                yAxisL.setAxisMinimum(0);
//                yAxisL.setAxisMaximum(1000);
//                break;
//            case "CosPhi":
//                yAxisL.setAxisMinimum(0);
//                yAxisL.setAxisMaximum(5);
//                break;
//            case "Frekuensi":
//                yAxisL.setAxisMinimum(40);
//                yAxisL.setAxisMaximum(70);
//                break;
//            case "Energy":
//                yAxisL.setAxisMinimum(0);
//                yAxisL.setAxisMaximum(100);
//                break;
//        }

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.notifyDataSetChanged();
        chart.clear();
        chart.setData(lineData);
        chart.invalidate();
//        chart.moveViewTo(lineData.getEntryCount(),50L, YAxis.AxisDependency.LEFT);
        chart.moveViewToAnimated(lineData.getXMax(), 50L, YAxis.AxisDependency.LEFT, 1000);
    }

    public void Back(View view) {
        onBackPressed();
    }
}
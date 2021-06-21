package com.radenmas.smartenergy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {

    private TextView tvType, tvDate, tvClock, tvValue;
    private LineChart chart;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private LineDataSet lineDataSet = new LineDataSet(null, null);
    private ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    private LineData lineData;

    private String type, reff;

    private RadioGroup rgTimeSortir, rgTimeSortir2;

    private boolean isChecking = true;
    private int mCheckedId = R.id.halfHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();

        //getData from Intent
//        type = getIntent().getStringExtra("type");
//        reff = getIntent().getStringExtra("reff");

        type = "Arus";
        reff = "arus";

        //setType
        tvType.setText(type);

        //setDatabase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Smart Energy");

        retrieveData();

        setDigital();


        rgTimeSortir.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                rgTimeSortir2.clearCheck();
                mCheckedId = checkedId;
                showType();
            }
            isChecking = true;
        });

        rgTimeSortir2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                rgTimeSortir.clearCheck();
                mCheckedId = checkedId;
                showType();
            }
            isChecking = true;
        });
    }

    public void showType() {
        if (mCheckedId == R.id.halfHour) {
            chart.moveViewTo(lineData.getEntryCount() - 450, 50f, YAxis.AxisDependency.LEFT);
        } else if (mCheckedId == R.id.oneHour) {
            chart.moveViewTo(lineData.getEntryCount() - 900, 50f, YAxis.AxisDependency.LEFT);
        } else if (mCheckedId == R.id.threeHours) {
            chart.moveViewTo(lineData.getEntryCount() - 2700, 50f, YAxis.AxisDependency.LEFT);
        } else if (mCheckedId == R.id.oneDay) {
            chart.moveViewTo(lineData.getEntryCount() - 21600, 50f, YAxis.AxisDependency.LEFT);
        } else if (mCheckedId == R.id.oneWeek) {
            chart.moveViewTo(lineData.getEntryCount() - 151200, 50f, YAxis.AxisDependency.LEFT);
        } else if (mCheckedId == R.id.oneMonth) {
            chart.moveViewTo(lineData.getEntryCount() - 648000, 50f, YAxis.AxisDependency.LEFT);
        }
    }

    private void initView() {
        tvType = findViewById(R.id.tvType);
        tvDate = findViewById(R.id.tvDate);
        tvClock = findViewById(R.id.tvClock);
        tvValue = findViewById(R.id.tvValue);
        chart = findViewById(R.id.chart);
        rgTimeSortir = findViewById(R.id.rgTimeSortir);
        rgTimeSortir2 = findViewById(R.id.rgTimeSortir2);
    }

    private void setDigital() {
        Query query = reference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    DecimalFormat df = new DecimalFormat("#");

                    DataPoints dataPoints = child.getValue(DataPoints.class);

                    long time = dataPoints.getTime();

                    float dataSensor = (float) dataPoints.getArus();

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(time * 1000);

                    String date = DateFormat.format("dd MMM yyyy", cal).toString();
                    String clock = DateFormat.format("HH:mm zz", cal).toString();

                    tvDate.setText(date);
                    tvClock.setText(clock);

                    switch (type) {
                        case "Tegangan":
                            tvValue.setText(df.format(dataSensor) + "   V");
                            break;
                        case "Arus":
                            tvValue.setText(df.format(dataSensor) + "   A");
                            break;
                        case "Daya":
                            tvValue.setText(df.format(dataSensor) + "   W");
                            break;
                        case "CosPhi":
                            tvValue.setText(df.format(dataSensor));
                            break;
                        case "Frekuensi":
                            tvValue.setText(df.format(dataSensor) + "   Hz");
                            break;
                        case "Energy":
                            tvValue.setText(df.format(dataSensor) + "   kWh");
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
                } else {
                    chart.clear();
                    chart.invalidate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showChart(ArrayList<Entry> data) {
        lineDataSet.setValues(data);
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
        xAxis.setLabelRotationAngle(0f); //45
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(false);
        xAxis.setLabelCount(3, true);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long longtime = (long) value;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(longtime * 1000);

                String s = DateFormat.format("HH:mm zz", cal).toString();

                return s;
            }
        });

        YAxis yAxisL = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisL.setDrawGridLines(false);
        yAxisL.setDrawLabels(false);

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
        chart.notifyDataSetChanged();
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    public void Back(View view) {
        onBackPressed();
    }

}
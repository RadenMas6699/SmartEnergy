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
import android.widget.Button;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
        type = getIntent().getStringExtra("type");
        reff = getIntent().getStringExtra("reff");

        //setType
        tvType.setText(type);

        //setDatabase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Smart Energy");

        retrieveData(450);

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

//        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("No chart data available. Use the menu to add entries and data sets!");

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.invalidate();
    }

    public void showType() {

//        450
//        900
//        2700
//        21600
//        151200
//        648000


        if (mCheckedId == R.id.halfHour) {
            retrieveData(450);
//            chart.moveViewTo(lineData.getEntryCount() - 45, 50f, YAxis.AxisDependency.RIGHT);
        } else if (mCheckedId == R.id.oneHour) {
            retrieveData(900);
//            chart.moveViewTo(lineData.getEntryCount() - 90, 50f, YAxis.AxisDependency.RIGHT);
        } else if (mCheckedId == R.id.threeHours) {
            retrieveData(2700);
//            chart.moveViewTo(lineData.getEntryCount() - 270, 50f, YAxis.AxisDependency.RIGHT);
        } else if (mCheckedId == R.id.oneDay) {
            retrieveData(21600);
//            chart.moveViewTo(lineData.getEntryCount() - 2160, 50f, YAxis.AxisDependency.RIGHT);
        } else if (mCheckedId == R.id.oneWeek) {
            retrieveData(151200);
//            chart.moveViewTo(lineData.getEntryCount() - 15120, 50f, YAxis.AxisDependency.RIGHT);
        } else if (mCheckedId == R.id.oneMonth) {
            retrieveData(648000);
//            chart.moveViewTo(lineData.getEntryCount() - 64800, 50f, YAxis.AxisDependency.RIGHT);
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

//    private void addEntry() {
//
//        LineData data = chart.getData();
//
//        if (data == null) {
//            data = new LineData();
//            chart.setData(data);
//        }
//
//        ILineDataSet set = data.getDataSetByIndex(0);
//        // set.addEntry(...); // can be called as well
//
//        if (set == null) {
//            set = createSet();
//            data.addDataSet(set);
//        }
//
//        // choose a random dataSet
//        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
//        ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
//        float value = (float) (Math.random() * 50) + 50f * (randomDataSetIndex + 1);
//
//        data.addEntry(new Entry(randomSet.getEntryCount(), value), randomDataSetIndex);
//        data.notifyDataChanged();
//
//        // let the chart know it's data has changed
//        chart.notifyDataSetChanged();
//
//        chart.setVisibleXRangeMaximum(10);
//        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//        // this automatically refreshes the chart (calls invalidate())
//        chart.moveViewTo(data.getEntryCount() - 11, 50f, YAxis.AxisDependency.LEFT);
//
//    }
//
//    private LineDataSet createSet() {
//
//        LineDataSet set = new LineDataSet(null, "DataSet 1");
//        set.setLineWidth(2.5f);
//        set.setCircleRadius(4.5f);
//        set.setColor(Color.rgb(240, 99, 99));
//        set.setCircleColor(Color.rgb(240, 99, 99));
//        set.setHighLightColor(Color.rgb(190, 190, 190));
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setValueTextSize(10f);
//
//        return set;
//    }
//
//    @Override
//    public void onValueSelected(Entry e, Highlight h) {
//        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onNothingSelected() {
//    }

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
                            float dataArus = (float) dataPoints.getArus();
                            tvValue.setText(df.format(dataArus) + "   A");
                            break;
                        case "Daya":
                            float dataDaya = (float) dataPoints.getPower();
                            tvValue.setText(df.format(dataDaya) + "   W");
                            break;
                        case "CosPhi":
                            float dataCos = (float) dataPoints.getCos_phi();
                            tvValue.setText(df.format(dataCos));
                            break;
                        case "Frekuensi":
                            float dataFreq = (float) dataPoints.getFrequensi();
                            tvValue.setText(df.format(dataFreq) + "   Hz");
                            break;
                        case "Energy":
                            float dataEnergy = (float) dataPoints.getEnergy();
                            tvValue.setText(df.format(dataEnergy) + "   kWh");
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveData(int limit) {
        Query query = reference.orderByKey().limitToLast(limit);
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
                    lineDataSet.setDrawValues(false);
                    lineDataSet.setDrawCircles(false);
                    lineDataSet.setDrawFilled(true);
                    if (Utils.getSDKInt() >= 18) {
                        Drawable drawable = ContextCompat.getDrawable(DetailActivity.this, R.drawable.fade_blue_light);
                        lineDataSet.setFillDrawable(drawable);
                    } else {
                        lineDataSet.setFillAlpha(5);
                    }
                    lineDataSet.setLineWidth(1.5f);
//                    lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//                    lineDataSet.setCubicIntensity(0.05f);
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

//        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);

        lineData = new LineData(iLineDataSets);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0f); //45
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setCenterAxisLabels(true);
//        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
//        xAxis.setLabelCount(3, true);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        xAxis.setValueFormatter(new ValueFormatter() {


//            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);
//
//            @Override
//            public String getFormattedValue(float value) {
//
//                long millis = TimeUnit.HOURS.toMillis((long) value);
//                return mFormat.format(new Date(millis));
//            }


            @Override
            public String getFormattedValue(float value) {
                long longtime = (long) value;

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(longtime * 1000);

                String s = DateFormat.format("HH:mm:ss zz", cal).toString();

                return s;
            }
        });

        YAxis yAxisL = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisL.setDrawGridLines(false);
        yAxisL.setDrawLabels(false);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setVisibleXRangeMaximum(10);

        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(false);

        chart.setData(lineData);

        chart.moveViewTo(lineData.getEntryCount() - 11, 50f, YAxis.AxisDependency.LEFT);

        chart.invalidate();

    }

    public void Back(View view) {
        onBackPressed();
    }

}
package com.radenmas.smartenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvValue, tvTime;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvValue = findViewById(R.id.tvValue);
        tvTime = findViewById(R.id.tvTime);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvValue.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            float time = e.getX();
            long longtime = (long) time;

            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(longtime * 1000);

            String clock = DateFormat.format("HH:mm:ss zz", cal).toString();

            tvTime.setText(clock);

            tvValue.setText(Utils.formatNumber((e.getY()), 2, true));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

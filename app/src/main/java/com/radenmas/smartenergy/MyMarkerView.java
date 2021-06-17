package com.radenmas.smartenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm zz", Locale.getDefault());
    private final TextView tvSuhu, tvTime;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvSuhu = findViewById(R.id.tvSuhu);
        tvTime = findViewById(R.id.tvTime);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvSuhu.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            float time = e.getX();
            String jam = sdf.format(time);
            tvTime.setText(jam);
            tvSuhu.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

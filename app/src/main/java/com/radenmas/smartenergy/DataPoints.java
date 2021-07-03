package com.radenmas.smartenergy;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class DataPoints {

    long time;
    int volt;
    float arus;
    int power;
    float cos_phi;
    int frequensi;
    float energy;

    public DataPoints() {
    }

    public DataPoints(long time, int volt, float arus, int power, float cos_phi, int frequensi, float energy) {
        this.time = time;
        this.volt = volt;
        this.arus = arus;
        this.power = power;
        this.cos_phi = cos_phi;
        this.frequensi = frequensi;
        this.energy = energy;
    }

    public long getTime() {
        return time;
    }

    public int getVolt() {
        return volt;
    }

    public float getArus() {
        return arus;
    }

    public int getPower() {
        return power;
    }

    public float getCos_phi() {
        return cos_phi;
    }

    public int getFrequensi() {
        return frequensi;
    }

    public float getEnergy() {
        return energy;
    }
}
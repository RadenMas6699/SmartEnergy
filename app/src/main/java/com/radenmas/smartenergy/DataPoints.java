package com.radenmas.smartenergy;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class DataPoints {

    long time;
    int volt;
    int arus;
    int power;
    int cos_phi;
    int frequensi;
    int energy;

    public DataPoints() {
    }

    public DataPoints(long time, int volt, int arus, int power, int cos_phi, int frequensi, int energy) {
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

    public int getArus() {
        return arus;
    }

    public int getPower() {
        return power;
    }

    public int getCos_phi() {
        return cos_phi;
    }

    public int getFrequensi() {
        return frequensi;
    }

    public int getEnergy() {
        return energy;
    }
}
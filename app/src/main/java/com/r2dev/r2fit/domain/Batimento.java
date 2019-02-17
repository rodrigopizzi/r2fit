package com.r2dev.r2fit.domain;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.concurrent.TimeUnit;

public class Batimento {

    private long start;
    private long end;
    private float bpm;

    public Batimento(long start, long end, float bpm) {
        this.start = start;
        this.end = end;
        this.bpm = bpm;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public float getBpm() {
        return bpm;
    }

    public DataPoint toDataPoint(DataPoint dataPoint) {
        dataPoint.setTimeInterval(start, end, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_BPM).setFloat(bpm);

        return dataPoint;
    }
}

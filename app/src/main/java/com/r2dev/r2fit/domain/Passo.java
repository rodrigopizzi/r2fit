package com.r2dev.r2fit.domain;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.concurrent.TimeUnit;

public class Passo {
    private long start;
    private long end;
    private int quantidade;

    public Passo(long start, long end, int quantidade) {
        this.start = start;
        this.end = end;
        this.quantidade = quantidade;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public DataPoint toDataPoint(DataPoint dataPoint) {
        dataPoint.setTimeInterval(start, end, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(quantidade);

        return dataPoint;
    }
}

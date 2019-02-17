package com.r2dev.r2fit.domain;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.concurrent.TimeUnit;

public class Intervalo {

    public static enum TipoAtividade{Caminhada, Corrida}

    private TipoAtividade tipoAtividade;
    private long start;
    private long end;

    public Intervalo(TipoAtividade tipoAtividade, long start, long end) {
        this.tipoAtividade = tipoAtividade;
        this.start = start;
        this.end = end;
    }

    public DataPoint toDataPoint(DataPoint dataPoint) {
        dataPoint.setTimeInterval(start, end, TimeUnit.MILLISECONDS);

        if (tipoAtividade == TipoAtividade.Caminhada) {
            dataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
        } else {
            dataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        }

        return dataPoint;
    }
}

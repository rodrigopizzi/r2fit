package com.r2dev.r2fit.service;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.r2dev.r2fit.domain.Batimento;
import com.r2dev.r2fit.domain.Intervalo;
import com.r2dev.r2fit.domain.Passo;
import com.r2dev.r2fit.domain.Treino;

import java.util.concurrent.TimeUnit;

public class TreinoService {

    private GoogleFitAPIConnection api;

    public TreinoService(GoogleFitAPIConnection api) {
        this.api = api;
    }

    public void insert(Treino treino) {
        Session session = new Session.Builder()
                .setName("Corrida com R2FIT")
                .setDescription("Long run around Shoreline Park")
                .setIdentifier("UniqueIdentifierHere")
                .setActivity(FitnessActivities.RUNNING)
                .setStartTime(treino.getStart(), TimeUnit.MILLISECONDS)
                .setEndTime(treino.getEnd(), TimeUnit.MILLISECONDS)
                .build();

        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(intervalos(treino))
                .addDataSet(batimentos(treino))
                .addDataSet(passos(treino))
                .build();

        api.insertSession(insertRequest);
    }

    private DataSet intervalos(Treino treino) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(api.getContext().getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setName(api.NAME + "-activity segments")
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet intervaloDataSet = DataSet.create(dataSource);
        for (Intervalo intervalo:treino.getIntervalos()) {
            intervaloDataSet.add(intervalo.toDataPoint(intervaloDataSet.createDataPoint()));
        }

        return intervaloDataSet;
    }

    private DataSet batimentos(Treino treino) {
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(api.getContext().getPackageName())
                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                        .setStreamName(api.NAME + "-heart count")
                        .setType(DataSource.TYPE_DERIVED)
                        .build();

        DataSet batimentoDataSet = DataSet.create(dataSource);
        for (Batimento batimento:treino.getBatimentos()) {
            batimentoDataSet.add(batimento.toDataPoint(batimentoDataSet.createDataPoint()));
        }

        return batimentoDataSet;
    }

    private DataSet passos(Treino treino) {
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(api.getContext().getPackageName())
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(api.NAME + "-activity segments")
                        .setName(api.NAME + "-activity segments")
                        .setType(DataSource.TYPE_DERIVED)
                        .build();

        DataSet passosDataSet = DataSet.create(dataSource);
        for (Passo passo:treino.getPassos()) {
            passosDataSet.add(passo.toDataPoint(passosDataSet.createDataPoint()));
        }

        return passosDataSet;
    }

}

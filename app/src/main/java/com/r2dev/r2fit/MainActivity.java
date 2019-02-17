package com.r2dev.r2fit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.r2dev.r2fit.domain.Treino;
import com.r2dev.r2fit.service.GoogleFitAPIConnection;
import com.r2dev.r2fit.service.TreinoService;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "R2FitAPI";
    private GoogleFitAPIConnection api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new GoogleFitAPIConnection();
        api.authentication(this);
    }

    public void gravarBatimentos(View view) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        Treino treino = new Treino(startTime, endTime);

        MockData mockData = new MockData();
        mockData.createIntervalos(treino);
        mockData.createBPM(treino);
        mockData.createPassos(treino);

        TreinoService treinoService = new TreinoService(api);
        treinoService.insert(treino);
    }

    private DataSet steps() {
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, -2);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        cal.add(Calendar.MINUTE, +2);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(LOG_TAG + "-activity segments")
                        .setName(LOG_TAG + "-activity segments")
                        .setType(DataSource.TYPE_RAW)
                        .build();

// Create a data set
        int stepCountDelta = 950;
        DataSet dataSet = DataSet.create(dataSource);
// For each data point, specify a start time, end time, and the data value -- in this case,
// the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);

        return dataSet;
    }
}
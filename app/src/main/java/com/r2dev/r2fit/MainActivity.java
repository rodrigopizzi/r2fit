package com.r2dev.r2fit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    private static final String LOG_TAG = "R2FitAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
        }
    }

    private void accessGoogleFit() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(LOG_TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(LOG_TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }

    public void gravarBatimentos(View view) {
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();


        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
// in the middle of the run.
        DataSource activitySegmentDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setName(LOG_TAG + "-activity segments")
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        cal.add(Calendar.MINUTE, +10);
        long startWalkTime = cal.getTimeInMillis();

        DataPoint firstRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(firstRunningDp);

        cal.add(Calendar.MINUTE, +15);
        long endWalkTime = cal.getTimeInMillis();

        DataPoint walkingDp = activitySegments.createDataPoint()
                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
        walkingDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
        activitySegments.add(walkingDp);

        DataPoint secondRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
        secondRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(secondRunningDp);

// Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("2 Corrida com R2FIT")
                .setDescription("Long run around Shoreline Park")
                .setIdentifier("UniqueIdentifierHere")
                .setActivity(FitnessActivities.RUNNING)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .build();

// Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
//                .addDataSet(steps())
                .addDataSet(heart())
                .addDataSet(activitySegments)
                .build();

        Task<Void> response = Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this)).insertSession(insertRequest);
        response.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "Parece que deu certo!!!!!");
            }
        });
        response.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

    private DataSet heart() {
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
                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                        .setStreamName(LOG_TAG + "-heart count")
                        .setType(DataSource.TYPE_DERIVED)
                        .build();

        DataSet dataSet = DataSet.create(dataSource);
// For each data point, specify a start time, end time, and the data value -- in this case,
// the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_BPM).setFloat(120f);
        dataSet.add(dataPoint);

        return dataSet;
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
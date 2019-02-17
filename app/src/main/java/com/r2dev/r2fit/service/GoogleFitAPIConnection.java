package com.r2dev.r2fit.service;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class GoogleFitAPIConnection {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    private final FitnessOptions fitnessOptions;
    public final String NAME = "R2FitAPI";
    private Activity context;

    public GoogleFitAPIConnection() {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_LOCATION_BOUNDING_BOX, FitnessOptions.ACCESS_WRITE)
                .build();

    }

    public void authentication(Activity context) {
        this.context = context;

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    context,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(context),
                    fitnessOptions);
        }
    }

    public void insertSession(SessionInsertRequest insertRequest) {
        Task<Void> response = Fitness.getSessionsClient(this.context, GoogleSignIn.getLastSignedInAccount(this.context)).insertSession(insertRequest);
        response.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(GoogleFitAPIConnection.this.NAME, "Parece que deu certo!!!!!");
            }
        });
        response.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Activity getContext() {
        return context;
    }
}

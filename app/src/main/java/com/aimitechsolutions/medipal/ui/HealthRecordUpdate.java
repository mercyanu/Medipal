package com.aimitechsolutions.medipal.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.aimitechsolutions.medipal.R;

public class HealthRecordUpdate extends Fragment {

    //views
    View fragV;
    EditText weightView;
    EditText heightView;
    Spinner bGroupSpinner;
    EditText bPressureView;
    EditText bSugarView;
    Spinner testTimeSpinner;
    Spinner genotypeSpinner;
    EditText cholesterolView;
    EditText allergiesView;
    ProgressBar pg;

    //members
    private String date = "";
    private String weight = "";
    private String height = "";
    private String bGroup = "";
    private String bPressure = "";
    private String bSugar = "";
    private String testTime = "";
    private String genotype = "";
    private String cholesterol = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.healthrecord_update, container, false);
        init();


        return fragV;
    }

    private void init(){

    }
}

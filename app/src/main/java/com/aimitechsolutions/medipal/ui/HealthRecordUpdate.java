package com.aimitechsolutions.medipal.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.HealthInfoModel;
import com.aimitechsolutions.medipal.model.User;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HealthRecordUpdate extends Fragment {

    private final String TAG = "HEALTHRECORDUPDATE";
    //views
    View fragV;
    EditText weightView;
    EditText heightView;
    Spinner bGroupSpinner;
    EditText bPressureViewTop;
    EditText bPressureViewBottom;
    EditText bSugarView;
    Spinner testTimeSpinner;
    Spinner genotypeSpinner;
    EditText cholesterolView;
    EditText allergiesView;
    ProgressBar pg;
    Button saveButton;

    //members
    private String date = "";
    private String weight = "";
    private String height = "";
    private String bGroup = "";
    private String bPressureTop = "";
    private String bPressureBottom = "";
    private String bSugar = "";
    private String testTime = "";
    private String genotype = "";
    private String cholesterol = "";
    private String allergies = "";

    //adapters
    ArrayAdapter<CharSequence> adapterBGroup;
    ArrayAdapter<CharSequence> adapterTestTime;
    ArrayAdapter<CharSequence> adapterGenotype;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.healthrecord_update, container, false);
        init();
        displayFields();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                date = new SimpleDateFormat("EEE MMM dd yyyy").format(new Date());
                //new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                if(weightView.getText() != null) weight = weightView.getText().toString();
                if(heightView.getText() != null) height = heightView.getText().toString();
                //work on the blood pressure's dependency on each other
                if(bPressureViewTop.getText() != null) bPressureTop = bPressureViewTop.getText().toString();
                if(bPressureViewBottom.getText() != null) bPressureBottom = bPressureViewBottom.getText().toString();
                if(bSugarView.getText() != null) bSugar = bSugarView.getText().toString();
                if(cholesterolView.getText() != null) cholesterol = cholesterolView.getText().toString();
                if(allergiesView.getText() != null) allergies = allergiesView.getText().toString();

                HealthInfoModel info = new HealthInfoModel(date, weight, height, bGroup, bPressureTop, bPressureBottom, bSugar, testTime, genotype, cholesterol, allergies);
                updateDB(info);
            }
        });


        return fragV;
    }

    private void init(){
        weightView = fragV.findViewById(R.id.weight_value);
        heightView = fragV.findViewById(R.id.height_value);
        bGroupSpinner = fragV.findViewById(R.id.blood_group);
        bPressureViewTop = fragV.findViewById(R.id.blood_pressure_top);
        bPressureViewBottom = fragV.findViewById(R.id.blood_pressure_bottom);
        bSugarView = fragV.findViewById(R.id.blood_sugar);
        testTimeSpinner = fragV.findViewById(R.id.test_taken);
        genotypeSpinner = fragV.findViewById(R.id.genotype);
        cholesterolView = fragV.findViewById(R.id.cholesterol);
        allergiesView = fragV.findViewById(R.id.allergies);
        pg = fragV.findViewById(R.id.progressBar23);
        saveButton = fragV.findViewById(R.id.save_button);

        adapterBGroup = ArrayAdapter.createFromResource(fragV.getContext(),
                R.array.blood_group, R.layout.spinner_layout);
        adapterBGroup.setDropDownViewResource(R.layout.spinner_layout);
        bGroupSpinner.setAdapter(adapterBGroup);
        bGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        bGroup = "";
                        break;
                    case 1:
                        bGroup = "O+";
                        break;
                    case 2:
                        bGroup = "O-";
                        break;
                    case 3:
                        bGroup = "A+";
                        break;
                    case 4:
                        bGroup = "A-";
                        break;
                    case 5:
                        bGroup = "B+";
                        break;
                    case 6:
                        bGroup = "B-";
                        break;
                    case 7:
                        bGroup = "AB+";
                        break;
                    case 8:
                        bGroup = "AB-";
                        break;

                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterTestTime = ArrayAdapter.createFromResource(fragV.getContext(),
                        R.array.test_time, R.layout.spinner_layout);
        adapterTestTime.setDropDownViewResource(R.layout.spinner_layout);
        testTimeSpinner.setAdapter(adapterTestTime);
        testTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        testTime = "";
                        break;
                    case 1:
                        testTime = "Fasting";
                        break;
                    case 2:
                        testTime = "Before Meal";
                        break;
                    case 3:
                        testTime = "After Meal(1-2hrs)";
                        break;
                    case 4:
                        testTime = "Before Meal";
                        break;
                    case 5:
                        testTime = "Bedtime";
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterGenotype = ArrayAdapter.createFromResource(fragV.getContext(),
                        R.array.genotype, R.layout.spinner_layout);
        adapterGenotype.setDropDownViewResource(R.layout.spinner_layout);
        genotypeSpinner.setAdapter(adapterGenotype);
        genotypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        genotype = "";
                        break;
                    case 1:
                        genotype = "AA";
                        break;
                    case 2:
                        genotype = "AS";
                        break;
                    case 3:
                        genotype = "SS";
                        break;
                    case 4:
                        genotype = "SC";
                        break;
                    case 5:
                        genotype = "CC";
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void displayFields(){
        DocumentReference documentReference = db.collection("health_information").document(FetchNow.getUserId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot gottenDocument = task.getResult();
                    if(gottenDocument.exists()){
                        HealthInfoModel gottenInfo = gottenDocument.toObject(HealthInfoModel.class);
                        weightView.setText(gottenInfo.getWeight());
                        heightView.setText(gottenInfo.getHeight());
                        bGroupSpinner.setSelection(adapterBGroup.getPosition(gottenInfo.getBGroup()));
                        bPressureViewTop.setText(gottenInfo.getBPressureTop());
                        bPressureViewBottom.setText(gottenInfo.getBPressureBottom());
                        bSugarView.setText(gottenInfo.getBSugar());
                        testTimeSpinner.setSelection(adapterTestTime.getPosition(gottenInfo.getTestTime()));
                        genotypeSpinner.setSelection(adapterGenotype.getPosition(gottenInfo.getGenotype()));
                        cholesterolView.setText(gottenInfo.getCholesterol());
                        allergiesView.setText(gottenInfo.getAllergies());
                    }
                    else{
                        Log.d(TAG, "User not found in DB");
                    }
                }
                else{
                    Log.d(TAG, "Failed to retrieve document due to:"+task.getException());
                }
            }
        });

    }

    private void updateDB(HealthInfoModel info){
        DocumentReference documentReference = db.collection("health_information").document(FetchNow.getUserId());
        documentReference.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(fragV.getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                pg.setVisibility(View.INVISIBLE);
                HealthRecordView healthRecordView = new HealthRecordView();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.record_container, healthRecordView);
                ft.commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed writing database due to:" +e.getMessage());
                Toast.makeText(fragV.getContext(), "Unable to reach server", Toast.LENGTH_SHORT).show();
                pg.setVisibility(View.INVISIBLE);

            }
        });
    }
}

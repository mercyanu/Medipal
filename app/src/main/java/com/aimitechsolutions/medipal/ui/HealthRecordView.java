package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HealthRecordView extends Fragment {

    View fragV;
    ImageView editView;
    TextView dateView;
    TextView weightView;
    TextView heightView;
    TextView bGroupView;
    TextView bPressureView;
    TextView bSugarView;
    TextView testTimeView;
    TextView genotypeView;
    TextView cholesterolView;
    TextView allergiesView;
    ProgressBar pg;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.healthrecord_view, container, false);
        initViews();
        pg.setVisibility(View.VISIBLE);

        DocumentReference documentReference = db.collection("health_information").document(FetchNow.getUserId());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot == null){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setIcon(R.drawable.red_tick)
                            .setTitle("You do not have any health information yet!")
                            .setMessage("Add health information now?")
                            .setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HealthRecordUpdate update = new HealthRecordUpdate();
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.record_container, update);
                                    ft.addToBackStack("healthrecord");
                                    ft.commit();
                                }
                            });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
                else{
                    String date = "Last update: "+ documentSnapshot.getString("date");
                    dateView.setText(date);
                    weightView.setText(documentSnapshot.getString("weight"));
                    heightView.setText(documentSnapshot.getString("height"));
                    bGroupView.setText(documentSnapshot.getString("bgroup"));
                    String bPressure = documentSnapshot.getString("bpressureTop") + "/" + documentSnapshot.getString("bpressureBottom");
                    bPressureView.setText(bPressure);
                    bSugarView.setText(documentSnapshot.getString("bsugar"));
                    testTimeView.setText(documentSnapshot.getString("testTime"));
                    genotypeView.setText(documentSnapshot.getString("genotype"));
                    cholesterolView.setText(documentSnapshot.getString("cholesterol"));
                    allergiesView.setText(documentSnapshot.getString("allergies"));
                    pg.setVisibility(View.INVISIBLE);
                }
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Could not fetch data from database", Toast.LENGTH_SHORT).show();
            }
        });



        editView = fragV.findViewById(R.id.edit_icon);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthRecordUpdate update = new HealthRecordUpdate();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.record_container, update);
                ft.addToBackStack("healthrecord");
                ft.commit();
            }
        });

        return fragV;
    }

    private void initViews(){
        dateView = fragV.findViewById(R.id.date_value);
        weightView = fragV.findViewById(R.id.weight_value);
        heightView = fragV.findViewById(R.id.height_value);
        bGroupView = fragV.findViewById(R.id.blood_group_value);
        bPressureView = fragV.findViewById(R.id.blood_pressure_value);
        bSugarView = fragV.findViewById(R.id.blood_sugar_value);
        testTimeView = fragV.findViewById(R.id.test_taken);
        genotypeView = fragV.findViewById(R.id.genotype_value);
        cholesterolView = fragV.findViewById(R.id.cholesterol_value);
        allergiesView = fragV.findViewById(R.id.allergies_value);
        pg = fragV.findViewById(R.id.progress_bar3);
    }
}

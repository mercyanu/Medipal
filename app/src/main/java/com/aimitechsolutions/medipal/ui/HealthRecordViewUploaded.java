package com.aimitechsolutions.medipal.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.adapters.ImageUploadAdapter;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.ImageUploadDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordViewUploaded extends Fragment{

    private final static String TAG = "HEALTHRECORDVIEWUPLOAD";
    ProgressBar pg;
    View fragV;
    Button upload;
    ListView listView;
    ImageView imageView;

    ArrayList<ImageUploadDetails> uploadList;

    String[] mPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_CODE = 123;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.healthrecord_view_uploaded, container, false);
        listView = fragV.findViewById(R.id.list);
        pg = fragV.findViewById(R.id.progress_bar27);
        pg.setVisibility(View.VISIBLE);
        imageView = fragV.findViewById(R.id.image_from_url);

        setHasOptionsMenu(true);

        CollectionReference collectionReference = firebaseFirestore.collection("upload_details").document("1")
                .collection(FetchNow.getUserId());
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    //List<ImageUploadDetails> listOfDocs = queryDocumentSnapshots.toObjects(ImageUploadDetails.class);
                    uploadList = new ArrayList<>(queryDocumentSnapshots.toObjects(ImageUploadDetails.class));
                    Log.d(TAG, "ArrayList size is "+ uploadList.size());
                    ImageUploadAdapter adapter = new ImageUploadAdapter(getContext(), uploadList);
                    listView.setAdapter(adapter);
                    pg.setVisibility(View.INVISIBLE);
                }
                else { Log.d(TAG, "Query docss is empty"); pg.setVisibility(View.INVISIBLE); }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Query failed you in a way");
                pg.setVisibility(View.INVISIBLE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pg.setVisibility(View.VISIBLE);
                ImageUploadDetails currentUpload = uploadList.get(position);
                displayImage(currentUpload.getUrl());

            }
        });


        upload = fragV.findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    //open dialog to choose image
                    GetImageDialog getImageUpload = new GetImageDialog();
                    getImageUpload.show(getChildFragmentManager(), "GetImageDialog");
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(), mPermissions, REQUEST_CODE);
                }

            }
        });

        return fragV;
    }

    private void displayImage(String sentUri){
        Picasso.get().load(sentUri).into(imageView);
        pg.setVisibility(View.INVISIBLE);
    }

    private boolean checkPermissions(){
        if(ContextCompat.checkSelfPermission(getActivity(),mPermissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), mPermissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), mPermissions[2]) == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this, "You have all permissions", Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) { menu.clear();}
}

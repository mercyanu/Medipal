package com.aimitechsolutions.medipal.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.User;
import com.aimitechsolutions.medipal.utils.ConnectNetwork;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.aimitechsolutions.medipal.utils.Validator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendInvite extends Fragment {

    private static final String TAG = "SENDINVITE";
    //views
    View fragV;
    EditText emailView;
    Button searchButton;
    ProgressBar pg;

    //members
    String mEmailAddress;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SendInvite() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.send_invite_frag, container, false);

        emailView = fragV.findViewById(R.id.allergies);
        searchButton = fragV.findViewById(R.id.search_button);
        pg = fragV.findViewById(R.id.progressBar20);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mEmailAddress = emailView.getText().toString();
                if(ConnectNetwork.checkInternet(getActivity())){
                    if(!Validator.isEmpty(mEmailAddress)){
                        if(Validator.emailValid(mEmailAddress)){
                            pg.setVisibility(View.VISIBLE);
                            searchUser();
                        }
                        else CustomToast.displayToast(getActivity(), fragV, "Invalid email address");
                    }
                    else CustomToast.displayToast(getActivity(), fragV, "Please enter all fields");
                }
                else{
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setIcon(R.drawable.red_tick)
                            .setMessage("Turn on device wifi/data network")
                            .setTitle("No internet connection")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
        });

        return fragV;
    }

    private void searchUser(){
        CollectionReference collectionReference = db.collection("users");
        Query query = collectionReference.whereEqualTo("email", mEmailAddress);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> gottenDocs = queryDocumentSnapshots.getDocuments();
                if(gottenDocs.size() == 1){
                    for(DocumentSnapshot doc : gottenDocs){
                        User gottenUser = doc.toObject(User.class);
                        if(gottenUser != null){
                            if(!gottenUser.getEmail().equals(FetchNow.getUserEmail())){
                                Log.d(TAG, "Full name is "+gottenUser.getFname()+gottenUser.getLname());
                                //set dialog fragment
                                SendInviteDialog sendInviteDialog = new SendInviteDialog();
                                Bundle userBundle = new Bundle();
                                userBundle.putString("id", gottenUser.getUid());
                                userBundle.putString("user_type", gottenUser.getUserType());
                                userBundle.putString("first_name", gottenUser.getFname());
                                userBundle.putString("last_name", gottenUser.getLname());
                                sendInviteDialog.setArguments(userBundle);
                                sendInviteDialog.show(getActivity().getSupportFragmentManager(),"SendInviteDialog");
                                pg.setVisibility(View.INVISIBLE);
                                }
                            else {
                                Toast.makeText(getActivity(), "You are logged in as " + FetchNow.getUserEmail(), Toast.LENGTH_LONG).show();
                                pg.setVisibility(View.INVISIBLE);
                            }
                        }
                        else Log.d(TAG, "User object is null"); pg.setVisibility(View.INVISIBLE);
                    }
                }
                else Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_LONG).show(); pg.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Network failure or Fire Rule or something"+e.getMessage());
                pg.setVisibility(View.INVISIBLE);
            }
        });

        /*collectionReference.whereEqualTo("email", mEmailAddress)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Task was successful");
                            QuerySnapshot gottenDocument = task.getResult();
                            if(gottenDocument != null){
                                Log.d(TAG, "querydocument snapshot not empty");
                                List<DocumentSnapshot> documentList = gottenDocument.getDocuments();
                                for(DocumentSnapshot document : documentList){
                                    if(document != null){
                                        Log.d(TAG, "Document is not null");
                                        User gottenUser = document.toObject(User.class);
                                        Toast.makeText(getContext(), "The user name is " + gottenUser.getFname() + " " + gottenUser.getLname(), Toast.LENGTH_SHORT).show();
                                    }
                                    else Log.d(TAG, "Document is null");
                                }
                            }
                            else Log.d(TAG, "You have nothing in the querydocument snapshot");
                        } else Log.d(TAG, "Data not found due to" + task.getException());
                    }
                }); */
    }

    private void hideKeyboard(){
        if(fragV != null){
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(fragV.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

package com.aimitechsolutions.medipal.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedInvites extends Fragment {

    private final String TAG = "RECEIVEINVITES";
    View fragV;
    ListView list;

    ArrayList<User> userList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ReceivedInvites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.received_invites_frag, container, false);
        list = fragV.findViewById(R.id.listViewReceived);

        CollectionReference collectionReference = db.collection("pending_invite").document(FetchNow.getUserId())
                .collection("received");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> gottenDocs = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                for(DocumentSnapshot document : gottenDocs){
                    //fetch data for each
                    DocumentReference documentReference = db.collection("users").document(document.getId());
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userList.add(documentSnapshot.toObject(User.class));
                            Log.d(TAG, "1 The user list is now"+ userList.size());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                Log.d(TAG, "2 user list is now"+ userList.size());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Unable to fetch the recieved list", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "3 The user list is now"+ userList.size());

        return fragV;
    }

}

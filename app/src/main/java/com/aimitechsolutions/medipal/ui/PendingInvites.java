package com.aimitechsolutions.medipal.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingInvites extends Fragment {

    private final String TAG = "PENDINGINVITES";
    View fragV;

    ArrayList<User> sentUsersArray = new ArrayList<>();
    ArrayList<User> receivedUsersArray = new ArrayList<>();

    Bundle sentBundle = new Bundle();
    Bundle receivedBundle = new Bundle();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PendingInvites() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.pending_invites_frag, container, false);

        setHasOptionsMenu(true);  //use setHasOptionsMenu and override method opPrepareOptionsMenu to remove/customise action menu options

        showFragments();
        DocumentReference documentReference = db.collection("pending_invites").document(FetchNow.getUserId());
        /*documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        Map<String, Object> map = document.getData();
                        if(map != null){
                            List<String> userIDs = new ArrayList<>();
                            for(final Map.Entry<String, Object> entry : map.entrySet()){

                                /*DocumentReference documentReference1 = db.collection("users").document(entry.getKey());
                                documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document1 = task.getResult();
                                            if(document1 != null && document1.exists()){
                                                Log.d(TAG, "Key is "+ entry.getKey()+"...with value of"+entry.getValue());
                                                if(entry.getValue().equals("received")){
                                                    Log.d(TAG, "you have a received invite");
                                                    receivedUsersArray.add(document1.toObject(User.class));
                                                }
                                                else if(entry.getValue().equals("sent")){sentUsersArray.add(document1.toObject(User.class));}
                                            }else Log.d(TAG, "Inner Document is empty");
                                        }else Log.d(TAG, "inner task not successful");
                                    }
                                });
                            }
                            showFragments();
                        }else Log.d(TAG, "Map is null");
                    }else Log.d(TAG, "The document is empty, hence do nothing");
                }else Log.d(TAG, "task was unsuccessful due to:"+task.getException());
            }
        });

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                List<String> userIDs = new ArrayList<>();
                for(Map.Entry<String, Object> entry : map.entrySet()){
                    Log.d(TAG, "You have these IDs within for loop: "+userIDs);
                    userIDs.add(entry.getKey());
                }
                Log.d(TAG, "You have these IDs: "+userIDs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Could not fetch anything");
            }
        }); */

        return fragV;
    }

    private void showFragments(){
        //if(receivedUsersArray.size() > 0){
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            //Toast.makeText(getActivity(), "You have this before sending "+ids.toString(), Toast.LENGTH_SHORT).show();
            ReceivedInvites receivedInvites = new ReceivedInvites();
            //receivedBundle.putSerializable("receive_user", receivedUsersArray);
            //receivedInvites.setArguments(receivedBundle);
            ft.add(R.id.receive_container, receivedInvites);

            SentInvites sentInvites = new SentInvites();
            //sentBundle.putSerializable("sent", sentUsersArray);
            //sentInvites.setArguments(sentBundle);
            ft.add(R.id.sent_container, sentInvites);
            ft.commit();
        //} //end if statement
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendInviteDialog extends DialogFragment {

    private final String TAG = "SENDINVITEDIALOG";

    View fragV;
    TextView userFullNameView;
    Button sendButton;
    Button cancelButton;
    ProgressBar pg;

    //member var
    String mUserId;
    String mUserType;
    String mFirstName;
    String mLastName;
    String mFullName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.send_invite_dialog, container, false);
        mUserId = this.getArguments().getString("id");
        mUserType = this.getArguments().getString("user_type");
        mFirstName = this.getArguments().getString("first_name");
        mLastName = this.getArguments().getString("last_name");
        if(mUserType.equals("A doctor")) { mFullName = "Dr. " + mFirstName+ " " + mLastName; }
        else { mFullName = mFirstName+ " " + mLastName;}

        userFullNameView = fragV.findViewById(R.id.allergies);
        userFullNameView.setText(mFullName);
        cancelButton = fragV.findViewById(R.id.cancel_button);
        sendButton = fragV.findViewById(R.id.send_button);
        pg = fragV.findViewById(R.id.progressBar11);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                //check if user exists in friends_list

                //using second structure subcollection
                CollectionReference collectionReference = db.collection("friends_list").document(FetchNow.getUserId())
                        .collection("1");
                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> gottenDocs = queryDocumentSnapshots.getDocuments();
                        if(gottenDocs.size() >= 1){
                            for(DocumentSnapshot document : gottenDocs){
                                if(document.getId().equals(mUserId)){
                                    //user is on friendlist hence toast
                                    Toast.makeText(getActivity(), mFullName+" is already on consultation list", Toast.LENGTH_LONG).show();
                                    getDialog().dismiss();
                                    pg.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    //not on friends_list hence check pending invites
                                    checkPendingReceivedInvites();
                                }
                            }
                        }
                        else{
                            //you have no friends but still check pending_invites
                            checkPendingReceivedInvites();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error accessing server, try again", Toast.LENGTH_LONG).show();
                    }
                });



                /*DocumentReference documentReference = db.collection("friends_list").document(FetchNow.getUserId());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document!=null && document.exists()){
                                        if(document.get(mUserId) == null){
                                            //check the invites collection
                                            Log.d(TAG, " Check pending invites for " + mFullName);
                                            checkPendingInvites();
                                        }
                                        else if(document.getBoolean(mUserId)){
                                            Toast.makeText(getActivity(), mFullName+" is already on consultation list", Toast.LENGTH_LONG).show();
                                            getDialog().dismiss();
                                            pg.setVisibility(View.INVISIBLE);
                                        }
                                    }    else {
                                        //user not here
                                       Log.d(TAG, "You have no friends, check pending though");
                                        checkPendingInvites();
                                    }
                                }
                            }
                        }); */
            }
        });
        return fragV;
    }

    private void checkPendingReceivedInvites(){
        DocumentReference documentReference = db.collection("pending_invite").document(FetchNow.getUserId())
                .collection("received").document(mUserId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                        //user already sent invite hence ask if to receive
                        pg.setVisibility(View.INVISIBLE);
                        //ask if to accept invite "USER SENT AN INVITE ALREADY, DO YOU WANT TO ACCEPT
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setMessage("Accept invite?")
                                .setTitle(mFullName + " already sent you an invite")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pg.setVisibility(View.VISIBLE);
                                        acceptInvite();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        getDialog().dismiss();

                                    }
                                });
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                }
                else {
                    //doc not found in received hence check sent
                    checkPendingSentInvites();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //doc not found in received hence check sent
                Toast.makeText(getActivity(), "Error accessing server, try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkPendingSentInvites(){
        DocumentReference documentReference = db.collection("pending_invite").document(FetchNow.getUserId())
                .collection("sent").document(mUserId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                        //request already sent
                        Toast.makeText(getActivity(), "You already sent an invite to "+ mFullName, Toast.LENGTH_LONG).show();
                        getDialog().dismiss();
                        pg.setVisibility(View.INVISIBLE);
                }
                else {
                    //doc not found in received and sent hence send invite
                    sendInvite();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //doc not found in received and sent hence send invite
                Toast.makeText(getActivity(), "Error accessing server, try again", Toast.LENGTH_LONG).show();
            }
        });



        /*final DocumentReference documentReference = db.collection("pending_invites").document(FetchNow.getUserId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Task is successful");
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        Log.d(TAG, "Document is not null i.e this user has some pending sent/received");
                        if(document.get(mUserId) == null){
                            Log.d(TAG, "Sending invite perse");
                            sendInvite(); //UPDATE the document to have the new field fo mUser and sent/rece as value
                        }
                        else if(document.getString(mUserId).equals("sent")){
                            Toast.makeText(getActivity(), "You already sent an invite to "+ mFullName, Toast.LENGTH_LONG).show();
                            getDialog().dismiss();
                            pg.setVisibility(View.INVISIBLE);

                        }
                        else if(document.getString(mUserId).equals("received")){
                            pg.setVisibility(View.INVISIBLE);
                            //ask if to accept invite "USER SENT AN INVITE ALREADY, DO YOU WANT TO ACCEPT
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder.setMessage("Accept invite?")
                                    .setTitle(mFullName + " already sent you an invite")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pg.setVisibility(View.VISIBLE);
                                            acceptInvite();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            getDialog().dismiss();

                                        }
                                    });
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                        }
                    } else {
                        Log.d(TAG, "Current user has no invites atall, hence CREATE");
                        sendInvite();

                    }
                }
                else {
                    Log.d(TAG, "task isn's successful");
                    pg.setVisibility(View.INVISIBLE);
                }
            }
        });*/
    }

    private void acceptInvite(){
        //delete record from pending invites and add user to friends list
        //***Because this has alot of dependent read & write operations, there's need to use transactions
        final WriteBatch batch = db.batch();

        DocumentReference currentUserPending = db.collection("pending_invite").document(FetchNow.getUserId())
                .collection("received").document(mUserId);
        batch.delete(currentUserPending);
        //DocumentReference currentPending = db.collection("pending_invites").document(FetchNow.getUserId());
        //batch.update(currentPending, mUserId, FieldValue.delete());

        DocumentReference recepientUserPending = db.collection("pending_invite").document(mUserId)
                .collection("sent").document(FetchNow.getUserId());
        batch.delete(recepientUserPending);
        //final DocumentReference receipientUser = db.collection("pending_invites").document(mUserId);
        //batch.update(receipientUser, FetchNow.getUserId(), FieldValue.delete());

        DocumentReference currentDoc = db.collection("friends_list").document(FetchNow.getUserId())
                .collection("1").document(mUserId);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("status", true);
        batch.set(currentDoc, map1);

        DocumentReference receipientDoc = db.collection("friends_list").document(mUserId)
                .collection("1").document(FetchNow.getUserId());
        Map<String, Object> map2 = new HashMap<>();
        map2.put("status", true);
        batch.set(receipientDoc, map2);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(), "You have added "+ mFullName, Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.INVISIBLE);
                    getDialog().dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "Task incomplete due to " + task.getException(), Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.INVISIBLE);
                    getDialog().dismiss();
                }
            }
        });

        /*final DocumentReference currentDoc = db.collection("friends_list").document(FetchNow.getUserId());
        final Map<String, Object> map1 = new HashMap<>();
        map1.put(mUserId, true);
        currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        batch.update(currentDoc, map1);
                    }
                    else{
                        //the document doesnt exist create a new friendlist doc
                        batch.set(currentDoc, map1);
                    }
                }

                final DocumentReference recipientDoc = db.collection("friends_list").document(mUserId);
                final Map<String, Object> map2 = new HashMap<>();
                map2.put(FetchNow.getUserId(), true);
                recipientDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document != null && document.exists()){
                                                batch.update(recipientDoc, map2);
                                            }
                                            else{
                                                batch.set(recipientDoc, map2);
                                            }

                                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "You have added "+ mFullName, Toast.LENGTH_LONG).show();
                                        pg.setVisibility(View.INVISIBLE);
                                        getDialog().dismiss();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Task incomplete due to " + task.getException(), Toast.LENGTH_LONG).show();
                                        pg.setVisibility(View.INVISIBLE);
                                        getDialog().dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });*/
    }

    private void sendInvite(){
        //create document and field for each sender and receiver and vice versa
        final WriteBatch batch = db.batch();

        DocumentReference currentUserPending = db.collection("pending_invite").document(FetchNow.getUserId())
                .collection("sent").document(mUserId);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("status", true);
        batch.set(currentUserPending, map1);

        DocumentReference recepientUserPending = db.collection("pending_invite").document(mUserId)
                .collection("received").document(FetchNow.getUserId());
        Map<String, Object> map2 = new HashMap<>();
        map2.put("status", true);
        batch.set(recepientUserPending, map2);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Invitation sent to "+mFullName, Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    pg.setVisibility(View.INVISIBLE);
                }
                else {
                    Toast.makeText(getActivity(), "Unable to send invitation, please try again", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                    pg.setVisibility(View.INVISIBLE);
                }
            }
        });


        //use a transaction instead to perform read to check if the other user document exist and proceed updating it else if it doesnt have any pending
        //create a new document which will now have this as the first invite(receive)

        /*final DocumentReference currentPending = db.collection("pending_invites").document(FetchNow.getUserId());
        final Map<String, Object> map1 = new HashMap<>();
        map1.put(mUserId, "sent");
        currentPending.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        batch.update(currentPending, map1);
                    }
                    else{
                        batch.set(currentPending, map1);
                    }
                }

                final DocumentReference receipientPending = db.collection("pending_invites").document(mUserId);
                final Map<String, Object> map2 = new HashMap<>();
                map2.put(FetchNow.getUserId(), "received");
                //check if receipient has pending invite to continue with update else create a new invite
                receipientPending.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null && document.exists()){
                                Log.d(TAG, "Recipient has some invites, proceed with update");
                                batch.update(receipientPending, map2);
                            } else {
                                Log.d(TAG, "The receipient has no invites at all, hence create");
                                batch.set(receipientPending, map2);
                            }

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Invitation sent to "+mFullName, Toast.LENGTH_LONG).show();
                                        getDialog().dismiss();
                                        pg.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Unable to send invitation, please try again", Toast.LENGTH_LONG).show();
                                        getDialog().dismiss();
                                        pg.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Task isnt succecful");
                            pg.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });*/
    }

    void sampleDisplay(){ //use in sent & received frags
        DocumentReference documentReference = db.collection("friends_list").document(FetchNow.getUserId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        Map<String, Object> map = document.getData();
                        if(map != null){
                            for(Map.Entry<String, Object> entry : map.entrySet()){
                                Toast.makeText(getActivity(), "Your friend is "+entry.getKey(), Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity(), "Your freinsship stat is "+entry.getValue(), Toast.LENGTH_LONG).show();


                            }
                        }
                    }
                }
            }
        });
    }
}
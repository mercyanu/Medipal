package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class SendInviteDialog extends DialogFragment {

    private final String TAG = "SENDINVITEDIALOG";

    View fragV;
    TextView userFullNameView;
    Button sendButton;
    Button cancelButton;

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
        if(mUserType.equals("A doctor")) mFullName = "Dr. " + mFirstName+ " " + mLastName;
        mFullName = mFirstName+ " " + mLastName;

        userFullNameView = fragV.findViewById(R.id.full_name);
        userFullNameView.setText(mFullName);
        cancelButton = fragV.findViewById(R.id.cancel_button);
        sendButton = fragV.findViewById(R.id.send_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if user exists in friends_list
                DocumentReference documentReference = db.collection("friends_list").document(FetchNow.getUserId());
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
                                            Toast.makeText(getActivity(), mFullName+" is already on consultation list", Toast.LENGTH_SHORT).show();
                                        }
                                    }    else {
                                        //user not here
                                       Log.d(TAG, "You have no friends, check pending though");
                                        checkPendingInvites();
                                    }
                                }
                            }
                        });
            }
        });
        return fragV;
    }

    private void checkPendingInvites(){
        final DocumentReference documentReference = db.collection("pending_invites").document(FetchNow.getUserId());
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
                            getDialog().dismiss();
                            Toast.makeText(getActivity(), "You already sent an invite to "+ mFullName, Toast.LENGTH_LONG).show();
                        }
                        else if(document.getString(mUserId).equals("received")){
                            //ask if to accept invite "USER SENT AN INVITE ALREADY, DO YOU WANT TO ACCEPT
                            getDialog().dismiss();
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder.setMessage("Accept invite?")
                                    .setTitle(mFullName + " already sent you an invite")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getActivity(), "Accepting invite", Toast.LENGTH_SHORT).show();
                                            acceptInvite();
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
                    } else {
                        Log.d(TAG, "Current user has no invites atall, hence CREATE");
                        sendInvite();

                    }
                }
                else Log.d(TAG, "task isn's successful");
            }
        });
    }

    private void acceptInvite(){
        //delete record from pending invites and add user to friends list
        WriteBatch batch = db.batch();

        DocumentReference currentPending = db.collection("pending_invites").document(FetchNow.getUserId());
        batch.update(currentPending, mUserId, FieldValue.delete());

        DocumentReference receipientUser = db.collection("pending_invites").document(mUserId);
        batch.update(receipientUser, FetchNow.getUserId(), FieldValue.delete());

        DocumentReference currentFriends = db.collection("friends_list").document(FetchNow.getUserId());
        batch.update(currentFriends, mUserId, true);

        DocumentReference receipientFriends = db.collection("friends_list").document(mUserId);
        batch.update(receipientFriends, FetchNow.getUserId(), true);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(), " You can start consulting " + mFullName, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getActivity(), "Task incomplete due to " + task.getException(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendInvite(){
        //create document and field for each sender and receiver and vice versa
        final WriteBatch batch = db.batch();

        //use a transaction instead to perform read to check if the other user document exist and proceed updating it else if it doesnt have any pending
        //create a new document which will now have this as the first invite(receive)

        final DocumentReference currentPending = db.collection("pending_invites").document(FetchNow.getUserId());
        final Map<String, Object> map1 = new HashMap<>();
        currentPending.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        map1.put(mUserId, "sent");
                        batch.update(currentPending, map1);
                    }
                    else{
                        Map<String, Object> map0 = new HashMap<>();
                        map0.put(mUserId, "sent");
                        batch.set(currentPending, map0);
                    }
                }


                final DocumentReference receipientPending = db.collection("pending_invites").document(mUserId);
                final Map<String, Object> map2 = new HashMap<>();
                //check if receipient has pending invite to continue with update else create a new invite
                receipientPending.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null && document.exists()){
                                Log.d(TAG, "Recipient has some invites, proceed with update");
                                map2.put(FetchNow.getUserId(), "received");
                                batch.update(receipientPending, map2);
                            } else {
                                Log.d(TAG, "The receipient has no invites at all, hence create");
                                Map<String, Object> map3 = new HashMap<>();
                                map3.put(FetchNow.getUserId(), "received");
                                batch.set(receipientPending, map3);
                            }

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Invitation sent to "+mFullName, Toast.LENGTH_LONG).show();
                                        getDialog().dismiss();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Unable to send invitation, please try again", Toast.LENGTH_LONG).show();
                                        getDialog().dismiss();
                                    }
                                }
                            });
                        } else Log.d(TAG, "Task isnt succecful");
                    }
                });

            }
        });




    }
}
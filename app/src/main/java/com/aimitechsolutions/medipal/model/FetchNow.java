package com.aimitechsolutions.medipal.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FetchNow {

    private static final String TAG = "FETCHNOW";
    private static String mUid;
    private static String mUserType;
    private static String mFirstName;
    private static String mLastName;
    private static String mUserEmail;
    private static String mUserPhone;
    private static String mUserCountry;
    private static String mUserGender;

    /*blic FetchNow(User user){
        mUid = user.getUid();
        mUserType = user.getUserType();
        mFirstName= user.getFname();
        mLastName= user.getLname();
        mUserEmail= user.getEmail();
        mUserPhone= user.getMobile();
        mUserCountry= user.getCountry();
        mUserGender= user.getGender();
    } */

    public static boolean setCurrentUserDetails(User user){
        if(user != null){
            Log.d(TAG, "You have something in the user holder");

            mUid = user.getUid();
            mUserType = user.getUserType();
            mFirstName= user.getFname();
            mLastName= user.getLname();
            mUserEmail= user.getEmail();
            mUserPhone= user.getMobile();
            mUserCountry= user.getCountry();
            mUserGender= user.getGender();
            return true;
        }
        else {
            Log.d(TAG, "User is empty");
            return false;
        }
    }
    /*public static void fetchCurrentUserData(){
        FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbReference = databaseInstance.getReference();
        Query q = dbReference.child("users")
                .orderByChild("uid")
                .equalTo(userID);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot gottenSnapshot : dataSnapshot.getChildren()){
                    User currentUser = gottenSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        Log.d(TAG, "Current user aint empty");
                        assignMemberValues(currentUser);
                        mUid = currentUser.getUid();
                        mUserType = currentUser.getUserType();
                        mFirstName = currentUser.getFname();
                        mLastName = currentUser.getLname();
                        mUserEmail = currentUser.getEmail();
                        mUserPhone = currentUser.getMobile();
                        mUserCountry = currentUser.getCountry();
                        mUserGender = currentUser.getGender();
                    } else Log.d(TAG, "Sorry!! No current user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } */

    public static String getUserId(){
        return mUid;
}
    public static String getUserType(){
        return mUserType;
    }
    public static String getUserFirstName(){
        return mFirstName;
    }
    public static String getUserLastName(){
        return mLastName;
    }
    public static String getUserEmail(){
        return mUserEmail;
    }
    public static String getUserPhone(){
        return mUserPhone;
    }
    public static String getUserCountry(){
        return mUserCountry;
    }
    public static String getUserGender(){
        return mUserGender;
    }

    public static void resetAllMembers(){
        //assign null values to all members;
        mUid = null;
        mUserType = null;
        mFirstName= null;
        mLastName= null;
        mUserEmail= null;
        mUserPhone= null;
        mUserCountry= null;
        mUserGender= null;

    }
}

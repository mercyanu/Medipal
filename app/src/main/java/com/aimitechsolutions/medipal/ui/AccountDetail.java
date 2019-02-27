package com.aimitechsolutions.medipal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.User;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.aimitechsolutions.medipal.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class AccountDetail extends Fragment {

    private final String TAG = "ACCOUNTDETAILS";
    //view
    View fragV;
    Spinner usertypeSpinner;
    EditText firstNameView;
    EditText lastNameView;
    EditText emailView;
    EditText mobileView;
    Spinner genderSpinner;
    Button save;
    CountryCodePicker countryPicker;
    ProgressBar pg;


    //member var
    String userType = "";
    String firstName;
    String lastName;
    String mobile;
    String gender = "";
    String country;

    //adapters
    ArrayAdapter<CharSequence> adapterUser;
    ArrayAdapter<CharSequence> adapterGen;


    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.account_details_screen, container, false);

        //init
        usertypeSpinner = fragV.findViewById(R.id.usertype);
        firstNameView = fragV.findViewById(R.id.first_name);
        lastNameView = fragV.findViewById(R.id.last_name);
        emailView = fragV.findViewById(R.id.full_name);
            emailView.setText(currentUser.getEmail());
            emailView.setEnabled(false);
        mobileView = fragV.findViewById(R.id.mobile);
        mobileView = fragV.findViewById(R.id.mobile);
        genderSpinner = fragV.findViewById(R.id.gender);
        save = fragV.findViewById(R.id.save_button);
        countryPicker = fragV.findViewById(R.id.country_picker);
        pg = fragV.findViewById(R.id.progress_bar3);

        adapterGen = ArrayAdapter.createFromResource(fragV.getContext(),
                R.array.gender_array, R.layout.spinner_layout);
        adapterGen.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapterGen);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        userType = "";
                        break;
                    case 1:
                        gender = "Female";
                        break;
                    case 2:
                        gender = "Male";
                        break;
                    default:
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterUser = ArrayAdapter.createFromResource(fragV.getContext(),
                R.array.user_type_array, R.layout.spinner_layout);
        adapterUser.setDropDownViewResource(R.layout.spinner_layout);
        usertypeSpinner.setAdapter(adapterUser);

        usertypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        userType = "";
                        break;
                    case 1:
                        userType = "A doctor";
                        break;
                    case 2:
                        userType = "Not a doctor";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryPicker.getSelectedCountryName();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                firstName = firstNameView.getText().toString();
                lastName = lastNameView.getText().toString();
                lastName = lastNameView.getText().toString();
                mobile = mobileView.getText().toString();
                if (country == null) country = countryPicker.getSelectedCountryName();

                if(!Validator.isEmpty(userType) && !Validator.isEmpty(firstName) && !Validator.isEmpty(lastName)
                        && !Validator.isEmpty(mobile) && !Validator.isEmpty(gender) && !Validator.isEmpty(country)){
                    //update database
                    User user  = new User(userType, firstName, lastName, emailView.getText().toString(),
                            mobile, gender, country, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    updateUserdata(user);

                }
                else{
                    CustomToast.displayToast(fragV.getContext(), fragV, "One or more fields empty, please fill all fields.");
                    pg.setVisibility(View.INVISIBLE);
                }
            }
        });

        displayFields();

        return fragV;
    }

    private void displayFields(){
        /*final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = databaseInstance.getReference();
        Query query = databaseReference.child("users")
                .orderByChild("uid")
                .equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleShot : dataSnapshot.getChildren()){
                    User gottenUser = singleShot.getValue(User.class);
                    usertypeSpinner.setSelection(adapterUser.getPosition(gottenUser.getUserType()));
                    firstNameView.setText(gottenUser.getFname());
                    lastNameView.setText(gottenUser.getLname());
                    mobileView.setText(gottenUser.getMobile());
                    genderSpinner.setSelection(adapterGen.getPosition(gottenUser.getGender()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Could not connect to database due to=>" +databaseError, Toast.LENGTH_SHORT).show();
            }
        });*/

        DocumentReference documentReference = db.collection("users").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot gottenDocument = task.getResult();
                    if(gottenDocument.exists()){
                        User gottenUser = (User) gottenDocument.toObject(User.class);
                        usertypeSpinner.setSelection(adapterUser.getPosition(gottenUser.getUserType()));
                        firstNameView.setText(gottenUser.getFname());
                        lastNameView.setText(gottenUser.getLname());
                        mobileView.setText(gottenUser.getMobile());
                        genderSpinner.setSelection(adapterGen.getPosition(gottenUser.getGender()));
                        //use countrypicker to set value for country when you find out
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

    private void updateUserdata(User user){
        /*DatabaseReference databaseReference = databaseInstance.getReference();
        databaseReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(fragV.getContext(), "Data saved", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fragV.getContext(), "Unable to complete, please try again" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }); */

        db.collection("users").document(currentUser.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "You have updated the database");
                        Toast.makeText(fragV.getContext(), "Record stored successfully", Toast.LENGTH_LONG).show();
                        pg.setVisibility(View.INVISIBLE);
                        Intent i = new Intent(getActivity(), DashboardActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed writing database due to:" +e.getMessage());
                        Toast.makeText(fragV.getContext(), "Record could not be stored", Toast.LENGTH_SHORT).show();
                        pg.setVisibility(View.INVISIBLE);
                    }
                });

    }

}

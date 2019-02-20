package com.aimitechsolutions.medipal.ui;

        import android.app.AlertDialog;
        import android.support.design.widget.TextInputEditText;
        import android.support.v4.app.FragmentManager;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.aimitechsolutions.medipal.R;
        import com.aimitechsolutions.medipal.model.User;
        import com.aimitechsolutions.medipal.utils.ConnectNetwork;
        import com.aimitechsolutions.medipal.utils.CustomToast;
        import com.aimitechsolutions.medipal.utils.Validator;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends Fragment {

    private static final String TAG = "LOGIN_FRAGMENT";

    //views
    View fragV;
    //TextInputLayout emailLay;
    TextInputEditText emailView;
    //TextInputLayout pwordLay;
    TextInputEditText pwordView;
    Button loginView;
    TextView gotoRegisterView;
    TextView gotoForgotView;
    String mEmailAddress;
    String mPassword;
    ProgressBar pg;

    FirebaseAuth getFirebaseInstance = FirebaseAuth.getInstance();
    FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.login_screen, container, false);
        //emailLay = fragV.findViewById(R.id.email_lay);
        emailView = fragV.findViewById(R.id.email_address);
        //pwordLay = fragV.findViewById(R.id.pass_lay);
        pwordView = fragV.findViewById(R.id.password);
        pg = fragV.findViewById(R.id.progress_bar2);

        gotoRegisterView = fragV.findViewById(R.id.goto_register);
        gotoRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register register = new Register();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragments_container, register);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        gotoForgotView = fragV.findViewById(R.id.goto_forgot);
        gotoForgotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forgot forgot = new Forgot();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragments_container, forgot);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        loginView = fragV.findViewById(R.id.login);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                mEmailAddress = emailView.getText().toString();
                mPassword = pwordView.getText().toString();

                if(ConnectNetwork.checkInternet(getActivity())){
                    if(!Validator.isEmpty(mEmailAddress) && !Validator.isEmpty(mPassword)){
                        if(Validator.emailValid(mEmailAddress)){
                            if(Validator.passwordValid(mPassword)){
                                //start loging in
                                logUserIn();

                            }
                            else CustomToast.displayToast(getActivity(), fragV, "Password must not be less than 6 characters");
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

    private void hideKeyboard(){
        if(fragV != null){
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(fragV.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void logUserIn(){
        if(!mEmailAddress.isEmpty() && !mPassword.isEmpty()){
            pg.setVisibility(View.VISIBLE);
            getFirebaseInstance.signInWithEmailAndPassword(mEmailAddress, mPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "New User logged in: "+getFirebaseInstance.getCurrentUser().getUid());
                                FirebaseUser user = getFirebaseInstance.getCurrentUser();
                                if(user!=null && user.isEmailVerified()) {
                                    //currentUser logged in...if currentUser in DB goto dashboard else goto account update
                                    checkUserInDbAndLogin();
                                }
                                else {
                                    Toast.makeText(getActivity(), "You have not verified your email address", Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setIcon(R.drawable.red_tick)
                                            .setMessage(mEmailAddress+" has not been verified! Resend verification email?")
                                            .setTitle("Verification error")
                                            .setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(pg.getVisibility() == View.INVISIBLE) pg.setVisibility(View.VISIBLE);
                                                    resendVerificationEmail();
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog dialog = dialogBuilder.create();
                                    dialog.show();
                                    pg.setVisibility(View.INVISIBLE);
                                }
                            }
                            else{
                                Log.d(TAG, "Sign in Task failed due to:" +task.getException());
                                pg.setVisibility(View.INVISIBLE);
                                CustomToast.displayToast(getActivity(), fragV, "Incorrect username or password!");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Sign onFailure due to:"+e.getMessage());
                    pg.setVisibility(View.INVISIBLE);
                    //CustomToast.displayToast(getActivity(), fragV, "Incorrect username or password");
                }
            });
        }
    }

    private void resendVerificationEmail(){
        FirebaseUser user = getFirebaseInstance.getCurrentUser();
        if(user != null) user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setIcon(R.drawable.green_tick)
                            .setMessage("A verification email has been sent to "+mEmailAddress)
                            .setTitle("Successful")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    if(pg.getVisibility() == View.VISIBLE) pg.setVisibility(View.INVISIBLE);
                                }
                            });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
                else Toast.makeText(getActivity(), "Could not send verification email, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserInDbAndLogin(){
        final String userID = getFirebaseInstance.getCurrentUser().getUid();
        /*DatabaseReference databaseReference = databaseInstance.getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userID)){
                    Toast.makeText(getActivity(), "The currentUser is in database", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), DashboardActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
                else {
                    AccountDetail accountSetting = new AccountDetail();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragments_container, accountSetting);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong due to=>" +databaseError, Toast.LENGTH_SHORT).show();
            }
        }); */

        DocumentReference documentReference = db.collection("users").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot ds = task.getResult();
                            //User gUser = ds.toObject(User.class);
                            if(ds.exists()) {
                                Log.d(TAG, "User is in database" + task.getResult().getId());
                                Intent i = new Intent(getActivity(), DashboardActivity.class);
                                startActivity(i);
                                getActivity().finish();
                                pg.setVisibility(View.INVISIBLE);
                            }
                            else {
                                Log.d(TAG, "User not in database");
                                AccountDetail accountSetting = new AccountDetail();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.fragments_container, accountSetting);
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                transaction.commit();
                                pg.setVisibility(View.INVISIBLE);
                            }
                        }
                        else Log.d(TAG, "Error fetching from database"); pg.setVisibility(View.INVISIBLE);
                    }
                });
    }
}

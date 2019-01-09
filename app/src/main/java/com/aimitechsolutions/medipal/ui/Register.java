package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.utils.ConnectNetwork;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.aimitechsolutions.medipal.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends Fragment {

    private static final String TAG = "RegisterFragment";
    //views
    View fragV;
    //TextInputLayout emailLay;
    TextInputEditText emailView;
    //TextInputLayout passLay;
    TextInputEditText pwordView;
    Button registerButton;
    TextView gotoLoginView;
    TextView openResendVerifyView;
    String mEmailAddress;
    String mPassword;
    ProgressBar progressBar;

    FirebaseAuth getFirebaseInstance = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.register_screen, container, false);

        //emailLay = fragV.findViewById(R.id.email_lay);
        emailView = fragV.findViewById(R.id.email_address);
        //passLay = fragV.findViewById(R.id.pass_lay);
        pwordView = fragV.findViewById(R.id.password);
        progressBar = fragV.findViewById(R.id.progress_bar_reg);

        gotoLoginView = fragV.findViewById(R.id.goto_login);
        gotoLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragments_container, login);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        registerButton = fragV.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mEmailAddress = emailView.getText().toString();
                mPassword = pwordView.getText().toString();

                if(ConnectNetwork.checkInternet(getActivity())){
                    if(!Validator.isEmpty(mEmailAddress) && !Validator.isEmpty(mPassword)){
                        if(Validator.emailValid(mEmailAddress)){
                            if(Validator.passwordValid(mPassword)){
                                progressBar.setVisibility(View.VISIBLE);
                                registerUser();
                            }
                            else{
                                CustomToast.displayToast(getActivity(), fragV, "Password must not be less than 6 characters");
                            }
                        }
                        else{
                            CustomToast.displayToast(getActivity(), fragV, "Invalid email address");
                        }
                    }
                    else{
                        CustomToast.displayToast(getActivity(), fragV, "Please enter all fields");
                    }
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

    public void registerUser(){
        getFirebaseInstance.createUserWithEmailAndPassword(mEmailAddress, mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "New user created" + getFirebaseInstance.getCurrentUser().getUid());
                            sendVerificationEmail();
                        }
                        else{
                            CustomToast.displayToast(getActivity(), fragV, "Account already exist");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToast.displayToast(getActivity(), fragV, "Account already exist");
            }
        });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = getFirebaseInstance.getCurrentUser();
        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "Send verification mail successful"+task.isSuccessful());
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                dialogBuilder.setIcon(R.drawable.green_tick)
                                        .setMessage(R.string.verify_your_email)
                                        .setTitle("Registration successful")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog dialog = dialogBuilder.create();
                                dialog.show();
                                emailView.setText("");
                                pwordView.setText("");

                                FirebaseAuth.getInstance().signOut(); //sign out of the just registered account
                                if(progressBar.getVisibility() == View.VISIBLE) progressBar.setVisibility(View.INVISIBLE);

                                Login login = new Login();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.fragments_container, login);
                                ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                            else{
                                Log.d(TAG, "Send verification mail successful"+task.isSuccessful());
                                Toast.makeText(getActivity(), "Unable to send verification mail, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}

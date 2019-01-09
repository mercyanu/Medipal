package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.aimitechsolutions.medipal.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends Fragment {

    private static final String TAG = "FORGOT_PASSWORD";
    //views
    View fragV;
    //TextInputLayout emailLay;
    TextInputEditText emailView;
    Button retrieveButton;
    TextView gotoRegisterView;
    String mEmailAddress;
    ProgressBar progressBar;

    FirebaseAuth getFirebaseInstance = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.forgot_screen, container, false);

        //emailLay = fragV.findViewById(R.id.email_lay);
        emailView = fragV.findViewById(R.id.email_address);
        progressBar = fragV.findViewById(R.id.progress_bar);

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

        retrieveButton = fragV.findViewById(R.id.retrieve);
        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mEmailAddress = emailView.getText().toString();

                if(!Validator.isEmpty(mEmailAddress)){
                    if(Validator.emailValid(mEmailAddress)){
                        //send forgot password link to email
                        progressBar.setVisibility(View.VISIBLE);
                        resetPasswrd();
                    }
                    else CustomToast.displayToast(getActivity(), fragV, "Invalid email address");
                }
                else CustomToast.displayToast(getActivity(), fragV, "Please enter email field");
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

    private void resetPasswrd() {
        if (mEmailAddress != null) {
            getFirebaseInstance.sendPasswordResetEmail(mEmailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Reset password task success?:" + task.isSuccessful());
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setIcon(R.drawable.green_tick)
                                .setMessage(R.string.forgot_email)
                                .setTitle("Successful")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if(progressBar.getVisibility() == View.VISIBLE) progressBar.setVisibility(View.INVISIBLE);
                                        emailView.setText("");
                                        Login login = new Login();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.fragments_container, login);
                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                    else Toast.makeText(getActivity(), "Email not found in the database", Toast.LENGTH_LONG).show();
                    if(progressBar.getVisibility() == View.VISIBLE) progressBar.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    CustomToast.displayToast(getActivity(), fragV, "Email not found in the database");
                    if(progressBar.getVisibility() == View.VISIBLE) progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

}

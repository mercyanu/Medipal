package com.aimitechsolutions.medipal.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final String regExp = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    /* //to use constructor logic instead of used static
    private String mEmail;
    private String mPassword;

    public Validator(String email, String password){
        mEmail = email;
        mPassword = password;
    }

    public boolean isEmpty(){
        if(mEmail.equals("") || mEmail.length()==0 || mPassword.equals("") || mPassword.length()==0){
            return true;
        }
        else return false;
    }

    public boolean emailValid(){
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mEmail);
        return m.find();
    }

    public boolean passwrodIsValid(){
        return mPassword.length() > 5;
    } */


    //should in case we opt for statics
    public static boolean isEmpty(String text){
        if(text.equals("") || text.length()==0) return true;
        else return false;
    }

    public static boolean emailValid(String text){
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static boolean passwordValid(String text){
        return text.length()>5;
    }

}

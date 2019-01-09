package com.aimitechsolutions.medipal.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;

public class CustomToast {
    public static void displayToast(Context context, View view, String error){
        //Instantiate Layout Inflater for inflating custom view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate the layout over the view parameter
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) view.findViewById(R.id.toast_viewGroup));

        TextView error_text = layout.findViewById(R.id.toast_textView);
        error_text.setText(error);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP |Gravity.FILL_HORIZONTAL, 0, 20);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();



    }
}
